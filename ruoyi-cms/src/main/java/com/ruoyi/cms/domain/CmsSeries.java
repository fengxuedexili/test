package com.ruoyi.cms.domain;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 官网产品系列 cms_series
 *
 * @author site
 */
public class CmsSeries extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long seriesId;

    private Long categoryId;

    private String name;

    private String subtitle;

    private String cover;

    private Integer sort;

    private String status;

    private String delFlag;

    public Long getSeriesId()
    {
        return seriesId;
    }

    public void setSeriesId(Long seriesId)
    {
        this.seriesId = seriesId;
    }

    public Long getCategoryId()
    {
        return categoryId;
    }

    public void setCategoryId(Long categoryId)
    {
        this.categoryId = categoryId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getSubtitle()
    {
        return subtitle;
    }

    public void setSubtitle(String subtitle)
    {
        this.subtitle = subtitle;
    }

    public String getCover()
    {
        return cover;
    }

    public void setCover(String cover)
    {
        this.cover = cover;
    }

    public Integer getSort()
    {
        return sort;
    }

    public void setSort(Integer sort)
    {
        this.sort = sort;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }
}
