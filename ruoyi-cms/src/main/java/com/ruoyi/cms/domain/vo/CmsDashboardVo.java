package com.ruoyi.cms.domain.vo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ruoyi.cms.domain.CmsArticle;
import com.ruoyi.cms.domain.CmsLead;

/**
 * 后台首页看板数据
 *
 * @author site
 */
public class CmsDashboardVo
{
    private long productCount;

    private long articleCount;

    private long pendingLeadCount;

    private long todayLeadCount;

    private long storeCount;

    private long pageCount;

    private List<CmsLead> recentLeads = new ArrayList<>();

    private List<CmsArticle> latestArticles = new ArrayList<>();

    /** 近 7 天线索量：[{d:'2026-11-12', c:3}] */
    private List<Map<String, Object>> leadTrend = new ArrayList<>();

    public long getProductCount()
    {
        return productCount;
    }

    public void setProductCount(long productCount)
    {
        this.productCount = productCount;
    }

    public long getArticleCount()
    {
        return articleCount;
    }

    public void setArticleCount(long articleCount)
    {
        this.articleCount = articleCount;
    }

    public long getPendingLeadCount()
    {
        return pendingLeadCount;
    }

    public void setPendingLeadCount(long pendingLeadCount)
    {
        this.pendingLeadCount = pendingLeadCount;
    }

    public long getTodayLeadCount()
    {
        return todayLeadCount;
    }

    public void setTodayLeadCount(long todayLeadCount)
    {
        this.todayLeadCount = todayLeadCount;
    }

    public long getStoreCount()
    {
        return storeCount;
    }

    public void setStoreCount(long storeCount)
    {
        this.storeCount = storeCount;
    }

    public long getPageCount()
    {
        return pageCount;
    }

    public void setPageCount(long pageCount)
    {
        this.pageCount = pageCount;
    }

    public List<CmsLead> getRecentLeads()
    {
        return recentLeads;
    }

    public void setRecentLeads(List<CmsLead> recentLeads)
    {
        this.recentLeads = recentLeads;
    }

    public List<CmsArticle> getLatestArticles()
    {
        return latestArticles;
    }

    public void setLatestArticles(List<CmsArticle> latestArticles)
    {
        this.latestArticles = latestArticles;
    }

    public List<Map<String, Object>> getLeadTrend()
    {
        return leadTrend;
    }

    public void setLeadTrend(List<Map<String, Object>> leadTrend)
    {
        this.leadTrend = leadTrend;
    }
}
