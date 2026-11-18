package com.ruoyi.cms.domain.vo;

import java.util.ArrayList;
import java.util.List;

import com.ruoyi.cms.domain.CmsCategory;

/**
 * 前台栏目 VO（不暴露 del_flag / 后台字段）
 *
 * @author site
 */
public class SiteCategoryVo
{
    private Long id;

    private Long parentId;

    private String name;

    private String slug;

    private String type;

    private String cover;

    private String summary;

    private List<SiteCategoryVo> children = new ArrayList<>();

    public SiteCategoryVo()
    {
    }

    public SiteCategoryVo(CmsCategory category)
    {
        this.id = category.getCategoryId();
        this.parentId = category.getParentId();
        this.name = category.getName();
        this.slug = category.getSlug();
        this.type = category.getType();
        this.cover = category.getCover();
        this.summary = category.getSummary();
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getParentId()
    {
        return parentId;
    }

    public void setParentId(Long parentId)
    {
        this.parentId = parentId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getSlug()
    {
        return slug;
    }

    public void setSlug(String slug)
    {
        this.slug = slug;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getCover()
    {
        return cover;
    }

    public void setCover(String cover)
    {
        this.cover = cover;
    }

    public String getSummary()
    {
        return summary;
    }

    public void setSummary(String summary)
    {
        this.summary = summary;
    }

    public List<SiteCategoryVo> getChildren()
    {
        return children;
    }

    public void setChildren(List<SiteCategoryVo> children)
    {
        this.children = children;
    }
}
