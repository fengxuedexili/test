package com.ruoyi.cms.controller.site;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.cms.domain.CmsStore;
import com.ruoyi.cms.service.ICmsStoreService;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.domain.AjaxResult;

/**
 * 前台 - 门店
 *
 * @author site
 */
@Anonymous
@RestController
@RequestMapping("/api/site/store")
public class SiteStoreController
{
    @Autowired
    private ICmsStoreService storeService;

    /**
     * 门店列表（可选省/市/类型过滤）
     */
    @GetMapping
    public AjaxResult list(@RequestParam(value = "province", required = false) String province,
            @RequestParam(value = "city", required = false) String city,
            @RequestParam(value = "type", required = false) String type)
    {
        CmsStore query = new CmsStore();
        query.setProvince(province);
        query.setCity(city);
        query.setType(type);
        return AjaxResult.success(storeService.selectSiteStoreList(query));
    }

    /**
     * 可筛选省份列表
     */
    @GetMapping("/provinces")
    public AjaxResult provinces()
    {
        return AjaxResult.success(storeService.selectProvinces());
    }
}
