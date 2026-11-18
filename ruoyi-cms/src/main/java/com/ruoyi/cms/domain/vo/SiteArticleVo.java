package com.ruoyi.cms.domain.vo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.ruoyi.cms.domain.CmsArticle;

/**
 * 前台文章 VO（列表与详情共用；列表不返回 content）
 *
 * @author site
 */
public class SiteArticleVo
{
    private Long id;

    private Long categoryId;

    private String title;

    private String cover;

    private String summary;

    private String author;

    private String source;

    private Integer viewCount;

    private boolean top;

    private String publishTime;

    private String content;

    private Map<String, String> seo = new LinkedHashMap<>();

    private List<SiteArticleVo> related = new ArrayList<>();

    public SiteArticleVo()
    {
    }

    public SiteArticleVo(CmsArticle article)
    {
        this.id = article.getArticleId();
        this.categoryId = article.getCategoryId();
        this.title = article.getTitle();
        this.cover = article.getCover();
        this.summary = article.getSummary();
        this.author = article.getAuthor();
        this.source = article.getSource();
        this.viewCount = article.getViewCount();
        this.top = "1".equals(article.getIsTop());
        this.publishTime = article.getPublishTime() == null ? null
                : new SimpleDateFormat("yyyy-MM-dd").format(article.getPublishTime());
        this.seo.put("title", article.getSeoTitle());
        this.seo.put("keywords", article.getSeoKeywords());
        this.seo.put("description", article.getSeoDescription());
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
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

    public boolean isTop()
    {
        return top;
    }

    public void setTop(boolean top)
    {
        this.top = top;
    }

    public String getPublishTime()
    {
        return publishTime;
    }

    public void setPublishTime(String publishTime)
    {
        this.publishTime = publishTime;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public Map<String, String> getSeo()
    {
        return seo;
    }

    public void setSeo(Map<String, String> seo)
    {
        this.seo = seo;
    }

    public List<SiteArticleVo> getRelated()
    {
        return related;
    }

    public void setRelated(List<SiteArticleVo> related)
    {
        this.related = related;
    }
}
