package com.ruoyi.cms.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.cms.domain.CmsPage;
import com.ruoyi.cms.service.ICmsPageService;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;

/**
 * 后台 - 单页管理
 *
 * @author site
 */
@RestController
@RequestMapping("/cms/page")
public class CmsPageController extends BaseController
{
    @Autowired
    private ICmsPageService pageService;

    @PreAuthorize("@ss.hasPermi('cms:page:list')")
    @GetMapping("/list")
    public AjaxResult list(CmsPage page)
    {
        return AjaxResult.success(pageService.selectPageList(page));
    }

    @PreAuthorize("@ss.hasPermi('cms:page:query')")
    @GetMapping("/{pageId}")
    public AjaxResult getInfo(@PathVariable Long pageId)
    {
        return AjaxResult.success(pageService.selectPageById(pageId));
    }

    @Log(title = "单页管理", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('cms:page:add')")
    @PostMapping
    public AjaxResult add(@RequestBody CmsPage page)
    {
        return toAjax(pageService.insertPage(page));
    }

    @Log(title = "单页管理", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('cms:page:edit')")
    @PutMapping
    public AjaxResult edit(@RequestBody CmsPage page)
    {
        return toAjax(pageService.updatePage(page));
    }

    @Log(title = "单页管理", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('cms:page:remove')")
    @DeleteMapping("/{pageIds}")
    public AjaxResult remove(@PathVariable Long[] pageIds)
    {
        return toAjax(pageService.deletePageByIds(pageIds));
    }
}
