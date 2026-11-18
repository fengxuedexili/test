package com.ruoyi.cms.controller.site;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.cms.service.ICmsBannerService;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.domain.AjaxResult;

/**
 * 前台 - 轮播
 *
 * @author site
 */
@Anonymous
@RestController
@RequestMapping("/api/site/banner")
public class SiteBannerController
{
    @Autowired
    private ICmsBannerService bannerService;

    /**
     * 当前时段有效 Banner（默认首页）
     */
    @GetMapping
    public AjaxResult list(@RequestParam(value = "position", required = false, defaultValue = "home") String position)
    {
        return AjaxResult.success(bannerService.selectSiteBanners(position));
    }
}
