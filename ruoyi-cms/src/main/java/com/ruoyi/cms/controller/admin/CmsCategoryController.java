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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.cms.domain.CmsCategory;
import com.ruoyi.cms.service.ICmsCategoryService;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;

/**
 * 后台 - 栏目管理
 *
 * @author site
 */
@RestController
@RequestMapping("/cms/category")
public class CmsCategoryController extends BaseController
{
    @Autowired
    private ICmsCategoryService categoryService;

    @PreAuthorize("@ss.hasPermi('cms:category:list')")
    @GetMapping("/list")
    public AjaxResult list(CmsCategory category)
    {
        return AjaxResult.success(categoryService.selectCategoryList(category));
    }

    @PreAuthorize("@ss.hasPermi('cms:category:list')")
    @GetMapping("/tree")
    public AjaxResult tree(@RequestParam(value = "type", required = false) String type)
    {
        return AjaxResult.success(categoryService.selectCategoryTree(type));
    }

    @PreAuthorize("@ss.hasPermi('cms:category:query')")
    @GetMapping("/{categoryId}")
    public AjaxResult getInfo(@PathVariable Long categoryId)
    {
        return AjaxResult.success(categoryService.selectCategoryById(categoryId));
    }

    @Log(title = "栏目管理", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('cms:category:add')")
    @PostMapping
    public AjaxResult add(@RequestBody CmsCategory category)
    {
        return toAjax(categoryService.insertCategory(category));
    }

    @Log(title = "栏目管理", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('cms:category:edit')")
    @PutMapping
    public AjaxResult edit(@RequestBody CmsCategory category)
    {
        return toAjax(categoryService.updateCategory(category));
    }

    @Log(title = "栏目管理", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('cms:category:remove')")
    @DeleteMapping("/{categoryIds}")
    public AjaxResult remove(@PathVariable Long[] categoryIds)
    {
        return toAjax(categoryService.deleteCategoryByIds(categoryIds));
    }
}
