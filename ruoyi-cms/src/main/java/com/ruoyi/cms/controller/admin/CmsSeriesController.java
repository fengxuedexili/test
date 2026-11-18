package com.ruoyi.cms.controller.admin;

import java.util.List;

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

import com.ruoyi.cms.domain.CmsSeries;
import com.ruoyi.cms.service.ICmsSeriesService;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;

/**
 * 后台 - 产品系列管理
 *
 * @author site
 */
@RestController
@RequestMapping("/cms/series")
public class CmsSeriesController extends BaseController
{
    @Autowired
    private ICmsSeriesService seriesService;

    @PreAuthorize("@ss.hasPermi('cms:series:list')")
    @GetMapping("/list")
    public AjaxResult list(CmsSeries series)
    {
        List<CmsSeries> list = seriesService.selectSeriesList(series);
        return AjaxResult.success(list);
    }

    @PreAuthorize("@ss.hasPermi('cms:series:query')")
    @GetMapping("/{seriesId}")
    public AjaxResult getInfo(@PathVariable Long seriesId)
    {
        return AjaxResult.success(seriesService.selectSeriesById(seriesId));
    }

    @Log(title = "产品系列", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('cms:series:add')")
    @PostMapping
    public AjaxResult add(@RequestBody CmsSeries series)
    {
        return toAjax(seriesService.insertSeries(series));
    }

    @Log(title = "产品系列", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('cms:series:edit')")
    @PutMapping
    public AjaxResult edit(@RequestBody CmsSeries series)
    {
        return toAjax(seriesService.updateSeries(series));
    }

    @Log(title = "产品系列", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('cms:series:remove')")
    @DeleteMapping("/{seriesIds}")
    public AjaxResult remove(@PathVariable Long[] seriesIds)
    {
        return toAjax(seriesService.deleteSeriesByIds(seriesIds));
    }
}
