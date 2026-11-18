package com.ruoyi.cms.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.cms.domain.CmsBanner;
import com.ruoyi.cms.domain.vo.SiteBannerVo;
import com.ruoyi.cms.mapper.CmsBannerMapper;
import com.ruoyi.cms.service.ICmsBannerService;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;

/**
 * 官网轮播 Service 实现
 *
 * @author site
 */
@Service
public class CmsBannerServiceImpl implements ICmsBannerService
{
    @Autowired
    private CmsBannerMapper bannerMapper;

    @Override
    public List<CmsBanner> selectBannerList(CmsBanner banner)
    {
        return bannerMapper.selectBannerList(banner);
    }

    @Override
    public CmsBanner selectBannerById(Long bannerId)
    {
        return bannerMapper.selectBannerById(bannerId);
    }

    /**
     * 前台轮播（仅正常状态 + 当前时段）
     */
    @Override
    public List<SiteBannerVo> selectSiteBanners(String position)
    {
        String pos = StringUtils.isEmpty(position) ? "home" : position;
        List<SiteBannerVo> result = new ArrayList<>();
        List<CmsBanner> list = bannerMapper.selectSiteBanners(pos);
        if (list != null)
        {
            for (CmsBanner banner : list)
            {
                result.add(new SiteBannerVo(banner));
            }
        }
        return result;
    }

    @Override
    public int insertBanner(CmsBanner banner)
    {
        validate(banner);
        banner.setCreateBy(SecurityUtils.getUsername());
        return bannerMapper.insertBanner(banner);
    }

    @Override
    public int updateBanner(CmsBanner banner)
    {
        if (banner.getBannerId() == null)
        {
            throw new ServiceException("BannerID 不能为空");
        }
        validate(banner);
        banner.setUpdateBy(SecurityUtils.getUsername());
        return bannerMapper.updateBanner(banner);
    }

    @Override
    public int deleteBannerByIds(Long[] bannerIds)
    {
        return bannerMapper.deleteBannerByIds(bannerIds);
    }

    private void validate(CmsBanner banner)
    {
        if (StringUtils.isEmpty(banner.getPosition()))
        {
            banner.setPosition("home");
        }
        if (StringUtils.isEmpty(banner.getMediaType()))
        {
            banner.setMediaType("1");
        }
        if ("2".equals(banner.getMediaType()) && StringUtils.isEmpty(banner.getVideoUrl()))
        {
            throw new ServiceException("视频 Banner 必须填写视频地址");
        }
        if ("1".equals(banner.getMediaType()) && StringUtils.isEmpty(banner.getImageUrl()))
        {
            throw new ServiceException("图片 Banner 必须上传图片");
        }
        if (banner.getSort() == null)
        {
            banner.setSort(0);
        }
        if (banner.getStatus() == null)
        {
            banner.setStatus("0");
        }
    }
}
