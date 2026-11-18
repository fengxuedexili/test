package com.ruoyi.cms.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 官网文章 cms_article
 *
 * @author site
 */
public class CmsArticle extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long articleId;

    private Long categoryId;

    private String title;

    private String cover;

    private String summary;

    /** 正文 HTML（后台富文本） */
    private String content;

    private String author;

    private String source;

    private Integer viewCount;

    /** 是否置顶：0 否 / 1 是 */
    private String isTop;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date publishTime;

    /** 状态：0 正常 / 1 停用 */
    private String status;

    /** 删除标志：0 存在 / 2 删除 */
    private String delFlag;

    private String seoTitle;

    private String seoKeywords;

    private String seoDescription;

    public Long getArticleId()
    {
        return articleId;
    }

    public void setArticleId(Long articleId)
    {
        this.articleId = articleId;
    }

    public Long getCategoryId()
    {
        return categoryId;
    }

    public void setCategoryId(Long categoryId)
    {
        this.categoryId = categoryId;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
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

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public String getAuthor()
    {
        return author;
    }

    public void setAuthor(String author)
    {
        this.author = author;
    }

    public String getSource()
    {
        return source;
    }

    public void setSource(String source)
    {
        this.source = source;
    }

    public Integer getViewCount()
    {
        return viewCount;
    }

    public void setViewCount(Integer viewCount)
    {
        this.viewCount = viewCount;
    }

    public String getIsTop()
    {
        return isTop;
    }

    public void setIsTop(String isTop)
    {
        this.isTop = isTop;
    }

    public Date getPublishTime()
    {
        return publishTime;
    }

    public void setPublishTime(Date publishTime)
    {
        this.publishTime = publishTime;
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
