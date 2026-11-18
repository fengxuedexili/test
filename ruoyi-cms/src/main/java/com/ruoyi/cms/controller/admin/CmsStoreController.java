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

import com.ruoyi.cms.domain.CmsStore;
import com.ruoyi.cms.service.ICmsStoreService;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;

/**
 * 后台 - 门店管理
 *
 * @author site
 */
@RestController
@RequestMapping("/cms/store")
public class CmsStoreController extends BaseController
{
    @Autowired
    private ICmsStoreService storeService;

    @PreAuthorize("@ss.hasPermi('cms:store:list')")
    @GetMapping("/list")
    public TableDataInfo list(CmsStore store)
    {
        startPage();
        return getDataTable(storeService.selectStoreList(store));
    }

    @PreAuthorize("@ss.hasPermi('cms:store:query')")
    @GetMapping("/{storeId}")
    public AjaxResult getInfo(@PathVariable Long storeId)
    {
        return AjaxResult.success(storeService.selectStoreById(storeId));
    }

    @Log(title = "门店管理", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('cms:store:add')")
    @PostMapping
    public AjaxResult add(@RequestBody CmsStore store)
    {
        return toAjax(storeService.insertStore(store));
    }

    @Log(title = "门店管理", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('cms:store:edit')")
    @PutMapping
    public AjaxResult edit(@RequestBody CmsStore store)
    {
        return toAjax(storeService.updateStore(store));
    }

    @Log(title = "门店管理", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('cms:store:remove')")
    @DeleteMapping("/{storeIds}")
    public AjaxResult remove(@PathVariable Long[] storeIds)
    {
        return toAjax(storeService.deleteStoreByIds(storeIds));
    }
}
