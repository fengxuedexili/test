package com.ruoyi.cms.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.cms.domain.CmsArticle;
import com.ruoyi.cms.domain.CmsLead;

/**
 * 后台看板统计 Mapper
 *
 * @author site
 */
public interface CmsDashboardMapper
{
    public long countProducts();

    public long countPublishedArticles();

    public long countPendingLeads();

    public long countTodayLeads();

    public long countActiveStores();

    public long countActivePages();

    public List<CmsLead> selectRecentLeads(@Param("limit") int limit);

    public List<CmsArticle> selectLatestArticles(@Param("limit") int limit);

    public List<Map<String, Object>> selectLeadTrend();
}
