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

import com.ruoyi.cms.domain.CmsProduct;
import com.ruoyi.cms.service.ICmsProductService;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;

/**
 * 后台 - 产品管理
 *
 * @author site
 */
@RestController
@RequestMapping("/cms/product")
public class CmsProductController extends BaseController
{
    @Autowired
    private ICmsProductService productService;

    @PreAuthorize("@ss.hasPermi('cms:product:list')")
    @GetMapping("/list")
    public TableDataInfo list(CmsProduct product)
    {
        startPage();
        return getDataTable(productService.selectProductList(product));
    }

    @PreAuthorize("@ss.hasPermi('cms:product:query')")
    @GetMapping("/{productId}")
    public AjaxResult getInfo(@PathVariable Long productId)
    {
        return AjaxResult.success(productService.selectProductById(productId));
    }

    @Log(title = "产品管理", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('cms:product:add')")
    @PostMapping
    public AjaxResult add(@RequestBody CmsProduct product)
    {
        return toAjax(productService.insertProduct(product));
    }

    @Log(title = "产品管理", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('cms:product:edit')")
    @PutMapping
    public AjaxResult edit(@RequestBody CmsProduct product)
    {
        return toAjax(productService.updateProduct(product));
    }

    @Log(title = "产品管理", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('cms:product:remove')")
    @DeleteMapping("/{productIds}")
    public AjaxResult remove(@PathVariable Long[] productIds)
    {
        return toAjax(productService.deleteProductByIds(productIds));
    }
}
