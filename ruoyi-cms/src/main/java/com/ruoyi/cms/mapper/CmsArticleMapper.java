package com.ruoyi.cms.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.cms.domain.CmsArticle;

/**
 * 官网文章 Mapper
 *
 * @author site
 */
public interface CmsArticleMapper
{
    public List<CmsArticle> selectArticleList(CmsArticle article);

    public List<CmsArticle> selectSiteArticleList(CmsArticle article);

    public CmsArticle selectArticleById(Long articleId);

    public List<CmsArticle> selectRelatedArticles(@Param("categoryId") Long categoryId,
            @Param("excludeId") Long excludeId, @Param("limit") int limit);

    public int insertArticle(CmsArticle article);

    public int updateArticle(CmsArticle article);

    public int deleteArticleByIds(Long[] articleIds);

    public int incrementViewCount(Long articleId);
}
