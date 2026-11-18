package com.ruoyi.cms.service;

import com.ruoyi.cms.domain.vo.CmsDashboardVo;

/**
 * 后台看板 Service
 *
 * @author site
 */
public interface ICmsDashboardService
{
    /** 汇总数据（统计 + 最近线索 + 最新文章 + 7 日趋势） */
    public CmsDashboardVo selectSummary();
}
