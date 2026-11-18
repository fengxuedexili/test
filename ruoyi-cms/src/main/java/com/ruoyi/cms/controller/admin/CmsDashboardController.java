package com.ruoyi.cms.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.cms.service.ICmsDashboardService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;

/**
 * 后台 - 首页看板
 *
 * @author site
 */
@RestController
@RequestMapping("/cms/dashboard")
public class CmsDashboardController extends BaseController
{
    @Autowired
    private ICmsDashboardService dashboardService;

    @PreAuthorize("@ss.hasPermi('cms:dashboard:view')")
    @GetMapping("/summary")
    public AjaxResult summary()
    {
        return AjaxResult.success(dashboardService.selectSummary());
    }
}
