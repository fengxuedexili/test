package com.ruoyi.cms.service;

import java.util.List;

import com.ruoyi.cms.domain.CmsArticle;
import com.ruoyi.cms.domain.vo.SiteArticleVo;

/**
 * 官网文章 Service
 *
 * @author site
 */
public interface ICmsArticleService
{
    public List<CmsArticle> selectArticleList(CmsArticle article);

    public CmsArticle selectArticleById(Long articleId);

    public List<CmsArticle> selectSiteArticleList(CmsArticle article);

    /** 实体列表转前台卡片 VO（列表/相关推荐共用） */
    public List<SiteArticleVo> toSiteArticleCards(List<CmsArticle> articles);

    /** 前台详情（浏览量 +1，含相关推荐） */
    public SiteArticleVo selectSiteArticleDetail(Long articleId, int relatedLimit);

    public int insertArticle(CmsArticle article);

    public int updateArticle(CmsArticle article);

    public int deleteArticleByIds(Long[] articleIds);
}
