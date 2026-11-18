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

import com.ruoyi.cms.domain.CmsBanner;
import com.ruoyi.cms.service.ICmsBannerService;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;

/**
 * 后台 - Banner 管理
 *
 * @author site
 */
@RestController
@RequestMapping("/cms/banner")
public class CmsBannerController extends BaseController
{
    @Autowired
    private ICmsBannerService bannerService;

    @PreAuthorize("@ss.hasPermi('cms:banner:list')")
    @GetMapping("/list")
    public TableDataInfo list(CmsBanner banner)
    {
        startPage();
        return getDataTable(bannerService.selectBannerList(banner));
    }

    @PreAuthorize("@ss.hasPermi('cms:banner:query')")
    @GetMapping("/{bannerId}")
    public AjaxResult getInfo(@PathVariable Long bannerId)
    {
        return AjaxResult.success(bannerService.selectBannerById(bannerId));
    }

    @Log(title = "Banner管理", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('cms:banner:add')")
    @PostMapping
    public AjaxResult add(@RequestBody CmsBanner banner)
    {
        return toAjax(bannerService.insertBanner(banner));
    }

    @Log(title = "Banner管理", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('cms:banner:edit')")
    @PutMapping
    public AjaxResult edit(@RequestBody CmsBanner banner)
    {
        return toAjax(bannerService.updateBanner(banner));
    }

    @Log(title = "Banner管理", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('cms:banner:remove')")
    @DeleteMapping("/{bannerIds}")
    public AjaxResult remove(@PathVariable Long[] bannerIds)
    {
        return toAjax(bannerService.deleteBannerByIds(bannerIds));
    }
}
