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

import com.ruoyi.cms.domain.CmsArticle;
import com.ruoyi.cms.service.ICmsArticleService;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;

/**
 * 后台 - 文章管理
 *
 * @author site
 */
@RestController
@RequestMapping("/cms/article")
public class CmsArticleController extends BaseController
{
    @Autowired
    private ICmsArticleService articleService;

    @PreAuthorize("@ss.hasPermi('cms:article:list')")
    @GetMapping("/list")
    public TableDataInfo list(CmsArticle article)
    {
        startPage();
        return getDataTable(articleService.selectArticleList(article));
    }

    @PreAuthorize("@ss.hasPermi('cms:article:query')")
    @GetMapping("/{articleId}")
    public AjaxResult getInfo(@PathVariable Long articleId)
    {
        return AjaxResult.success(articleService.selectArticleById(articleId));
    }

    @Log(title = "文章管理", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('cms:article:add')")
    @PostMapping
    public AjaxResult add(@RequestBody CmsArticle article)
    {
        return toAjax(articleService.insertArticle(article));
    }

    @Log(title = "文章管理", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('cms:article:edit')")
    @PutMapping
    public AjaxResult edit(@RequestBody CmsArticle article)
    {
        return toAjax(articleService.updateArticle(article));
    }

    @Log(title = "文章管理", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('cms:article:remove')")
    @DeleteMapping("/{articleIds}")
    public AjaxResult remove(@PathVariable Long[] articleIds)
    {
        return toAjax(articleService.deleteArticleByIds(articleIds));
    }
}
