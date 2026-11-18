package com.ruoyi.cms.controller.site;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.cms.domain.vo.SitePageVo;
import com.ruoyi.cms.service.ICmsPageService;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.domain.AjaxResult;

/**
 * 前台 - 单页（品牌/隐私/服务说明）
 *
 * @author site
 */
@Anonymous
@RestController
@RequestMapping("/api/site/page")
public class SitePageController
{
    @Autowired
    private ICmsPageService pageService;

    @GetMapping("/{slug}")
    public AjaxResult getBySlug(@PathVariable String slug)
    {
        SitePageVo vo = pageService.selectSitePageBySlug(slug);
        if (vo == null)
        {
            return AjaxResult.error("页面不存在或已下线");
        }
        return AjaxResult.success(vo);
    }
}
