package com.ruoyi.cms.controller.admin;

import java.util.List;

import jakarta.servlet.http.HttpServletResponse;

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

import com.ruoyi.cms.domain.CmsLead;
import com.ruoyi.cms.service.ICmsLeadService;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;

/**
 * 后台 - 线索管理
 *
 * @author site
 */
@RestController
@RequestMapping("/cms/lead")
public class CmsLeadController extends BaseController
{
    @Autowired
    private ICmsLeadService leadService;

    @PreAuthorize("@ss.hasPermi('cms:lead:list')")
    @GetMapping("/list")
    public TableDataInfo list(CmsLead lead)
    {
        startPage();
        return getDataTable(leadService.selectLeadList(lead));
    }

    @PreAuthorize("@ss.hasPermi('cms:lead:query')")
    @GetMapping("/{leadId}")
    public AjaxResult getInfo(@PathVariable Long leadId)
    {
        return AjaxResult.success(leadService.selectLeadById(leadId));
    }

    @Log(title = "线索管理", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('cms:lead:edit')")
    @PutMapping
    public AjaxResult edit(@RequestBody CmsLead lead)
    {
        return toAjax(leadService.updateLead(lead));
    }

    @Log(title = "线索管理", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('cms:lead:remove')")
    @DeleteMapping("/{leadIds}")
    public AjaxResult remove(@PathVariable Long[] leadIds)
    {
        return toAjax(leadService.deleteLeadByIds(leadIds));
    }

    @Log(title = "线索管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('cms:lead:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, CmsLead lead)
    {
        List<CmsLead> list = leadService.selectLeadList(lead);
        ExcelUtil<CmsLead> util = new ExcelUtil<>(CmsLead.class);
        util.exportExcel(response, list, "线索数据");
    }
}
