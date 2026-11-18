package com.ruoyi.cms.service.impl;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.ruoyi.cms.domain.CmsLead;
import com.ruoyi.cms.exception.SiteBizException;
import com.ruoyi.cms.mapper.CmsLeadMapper;
import com.ruoyi.cms.service.ICmsLeadService;
import com.ruoyi.common.constant.CacheConstants;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.service.ISysConfigService;

/**
 * 官网线索 Service 实现
 *
 * 前台提交保护：
 * 1) 图形验证码（复用 /captchaImage，Redis 校验后即删除）
 * 2) 同手机号 60s 锁定 + 24h 5 次
 * 3) 同 IP 1h 10 次 + 24h 30 次
 * 4) 蜜罐字段 website 非空直接静默丢弃
 * 5) requestId 5 分钟幂等
 *
 * @author site
 */
@Service
public class CmsLeadServiceImpl implements ICmsLeadService
{
    private static final Logger log = LoggerFactory.getLogger(CmsLeadServiceImpl.class);

    private static final String PHONE_LOCK_PREFIX = "site:lead:phone:lock:";

    private static final String PHONE_DAY_PREFIX = "site:lead:phone:day:";

    private static final String IP_HOUR_PREFIX = "site:lead:ip:hour:";

    private static final String IP_DAY_PREFIX = "site:lead:ip:day:";

    private static final String REQUEST_PREFIX = "site:lead:req:";

    private static final String PHONE_REGEX = "^1[3-9]\\d{9}$";

    @Autowired
    private CmsLeadMapper leadMapper;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Autowired
    private ISysConfigService configService;

    @Override
    public List<CmsLead> selectLeadList(CmsLead lead)
    {
        return leadMapper.selectLeadList(lead);
    }

    @Override
    public CmsLead selectLeadById(Long leadId)
    {
        return leadMapper.selectLeadById(leadId);
    }

    @Override
    public int submitSiteLead(CmsLead lead)
    {
        // 蜜罐：机器人常填隐藏字段，静默成功但不入库
        if (StringUtils.isNotEmpty(lead.getWebsite()))
        {
            return 1;
        }

        String phone = lead.getPhone();
        if (StringUtils.isEmpty(phone) || !phone.matches(PHONE_REGEX))
        {
            throw new SiteBizException(4002, "手机号格式不正确");
        }

        String requestKey = StringUtils.isEmpty(lead.getRequestId())
                ? null
                : REQUEST_PREFIX + lead.getRequestId();
        if (requestKey != null && redisCache.hasKey(requestKey))
        {
            return 1;
        }

        if (captchaEnabled())
        {
            checkCaptcha(lead);
        }

        String phoneLockKey = PHONE_LOCK_PREFIX + phone;
        if (redisCache.hasKey(phoneLockKey))
        {
            throw new SiteBizException(4003, "提交过于频繁，请 60 秒后再试");
        }

        incrWithTtl(PHONE_DAY_PREFIX + phone, TimeUnit.DAYS.toSeconds(1), 5);
        if (StringUtils.isNotEmpty(lead.getIp()))
        {
            incrWithTtl(IP_HOUR_PREFIX + lead.getIp(), TimeUnit.HOURS.toSeconds(1), 10);
            incrWithTtl(IP_DAY_PREFIX + lead.getIp(), TimeUnit.DAYS.toSeconds(1), 30);
        }

        if (StringUtils.isEmpty(lead.getType()))
        {
            lead.setType("1");
        }
        lead.setStatus("0");
        lead.setCreateBy("site");

        int rows = leadMapper.insertLead(lead);

        redisCache.setCacheObject(phoneLockKey, 1, 60, TimeUnit.SECONDS);
        if (requestKey != null)
        {
            redisCache.setCacheObject(requestKey, 1, 5, TimeUnit.MINUTES);
        }
        log.info("site lead submitted: id={}, type={}, ip={}", lead.getLeadId(), lead.getType(), lead.getIp());
        return rows;
    }

    @Override
    public int updateLead(CmsLead lead)
    {
        if (lead.getLeadId() == null)
        {
            throw new ServiceException("线索ID不能为空");
        }
        if (StringUtils.isEmpty(lead.getStatus()))
        {
            throw new ServiceException("状态不能为空");
        }
        lead.setUpdateBy(SecurityUtils.getUsername());
        lead.setHandleTime(DateUtils.getNowDate());
        return leadMapper.updateLead(lead);
    }

    @Override
    public int deleteLeadByIds(Long[] leadIds)
    {
        return leadMapper.deleteLeadByIds(leadIds);
    }

    private boolean captchaEnabled()
    {
        return "true".equalsIgnoreCase(configService.selectConfigByKey("sys.account.captchaEnabled"));
    }

    private void checkCaptcha(CmsLead lead)
    {
        String code = lead.getCode();
        String uuid = lead.getUuid();
        if (StringUtils.isEmpty(code) || StringUtils.isEmpty(uuid))
        {
            throw new SiteBizException(4001, "验证码不能为空");
        }
        String cacheKey = CacheConstants.CAPTCHA_CODE_KEY + uuid;
        String expect = redisCache.getCacheObject(cacheKey);
        redisCache.deleteObject(cacheKey);
        if (expect == null || !expect.equalsIgnoreCase(code))
        {
            throw new SiteBizException(4001, "验证码错误或已过期");
        }
    }

    private void incrWithTtl(String key, long ttlSeconds, int limit)
    {
        Long count = redisTemplate.opsForValue().increment(key);
        if (count != null && count == 1L)
        {
            redisTemplate.expire(key, ttlSeconds, TimeUnit.SECONDS);
        }
        if (count != null && count > limit)
        {
            throw new SiteBizException(4003, "提交过于频繁，请稍后再试");
        }
    }
}
