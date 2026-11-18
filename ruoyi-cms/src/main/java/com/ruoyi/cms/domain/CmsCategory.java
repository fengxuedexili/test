package com.ruoyi.cms.domain;

import java.util.List;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 官网栏目 cms_category
 *
 * @author site
 */
public class CmsCategory extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 栏目ID */
    private Long categoryId;

    /** 父栏目ID（0 为顶级） */
    private Long parentId;

    /** 栏目名称 */
    private String name;

    /** URL 别名（唯一） */
    private String slug;

    /** 类型：1 产品 / 2 资讯 */
    private String type;

    /** 栏目封面 */
    private String cover;

    /** 栏目简介 */
    private String summary;

    /** 显示顺序 */
    private Integer sort;

    /** SEO 标题 */
    private String seoTitle;

    /** SEO 关键词 */
    private String seoKeywords;

    /** SEO 描述 */
    private String seoDescription;

    /** 状态：0 正常 / 1 停用 */
    private String status;

    /** 删除标志：0 存在 / 2 删除 */
    private String delFlag;

    /** 子栏目（树形返回用） */
    private List<CmsCategory> children;

    public Long getCategoryId()
    {
        return categoryId;
    }

    public void setCategoryId(Long categoryId)
    {
        this.categoryId = categoryId;
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

    public Integer getSort()
    {
        return sort;
    }

    public void setSort(Integer sort)
    {
        this.sort = sort;
    }

    public String getSeoTitle()
    {
        return seoTitle;
    }

    public void setSeoTitle(String seoTitle)
    {
        this.seoTitle = seoTitle;
    }

    public String getSeoKeywords()
    {
        return seoKeywords;
    }

    public void setSeoKeywords(String seoKeywords)
    {
        this.seoKeywords = seoKeywords;
    }

    public String getSeoDescription()
    {
        return seoDescription;
    }

    public void setSeoDescription(String seoDescription)
    {
        this.seoDescription = seoDescription;
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

    public List<CmsCategory> getChildren()
    {
        return children;
    }

    public void setChildren(List<CmsCategory> children)
    {
        this.children = children;
    }
}
