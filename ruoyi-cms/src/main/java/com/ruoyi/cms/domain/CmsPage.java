package com.ruoyi.cms.domain;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 官网单页 cms_page（品牌/隐私/服务说明，区块化）
 *
 * @author site
 */
public class CmsPage extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long pageId;

    /** URL 别名（唯一） */
    private String slug;

    private String title;

    /** 区块 JSON：[{"type":"richText","props":{...}}] */
    private String blocks;

    /** 状态：0 正常 / 1 停用 */
    private String status;

    private String seoTitle;

    private String seoKeywords;

    private String seoDescription;

    public Long getPageId()
    {
        return pageId;
    }

    public void setPageId(Long pageId)
    {
        this.pageId = pageId;
    }

    public String getSlug()
    {
        return slug;
    }

    public void setSlug(String slug)
    {
        this.slug = slug;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getBlocks()
    {
        return blocks;
    }

    public void setBlocks(String blocks)
    {
        this.blocks = blocks;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
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
}
