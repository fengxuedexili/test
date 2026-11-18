package com.ruoyi.cms.controller.site;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.cms.domain.CmsLead;
import com.ruoyi.cms.service.ICmsLeadService;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.annotation.RateLimiter;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.ip.IpUtils;

/**
 * 前台 - 线索提交（预约/加盟/招聘/售后）
 *
 * @author site
 */
@Anonymous
@RestController
@RequestMapping("/api/site/lead")
public class SiteLeadController
{
    @Autowired
    private ICmsLeadService leadService;

    /**
     * 提交线索（匿名 + 频控；详细频控在 service 内按手机号/IP 细分）
     */
    @RateLimiter(time = 60, count = 30)
    @PostMapping
    public AjaxResult submit(@RequestBody CmsLead lead, HttpServletRequest request)
    {
        lead.setIp(IpUtils.getIpAddr(request));
        leadService.submitSiteLead(lead);
        return AjaxResult.success("提交成功，我们会尽快联系您");
    }
}
