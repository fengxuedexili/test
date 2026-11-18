package com.ruoyi.cms.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.cms.domain.CmsArticle;
import com.ruoyi.cms.domain.vo.SiteArticleVo;
import com.ruoyi.cms.mapper.CmsArticleMapper;
import com.ruoyi.cms.service.ICmsArticleService;
import com.ruoyi.cms.util.CmsHtmlSanitizer;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;

/**
 * 官网文章 Service 实现
 *
 * @author site
 */
@Service
public class CmsArticleServiceImpl implements ICmsArticleService
{
    @Autowired
    private CmsArticleMapper articleMapper;

    @Override
    public List<CmsArticle> selectArticleList(CmsArticle article)
    {
        return articleMapper.selectArticleList(article);
    }

    @Override
    public CmsArticle selectArticleById(Long articleId)
    {
        return articleMapper.selectArticleById(articleId);
    }

    @Override
    public List<CmsArticle> selectSiteArticleList(CmsArticle article)
    {
        return articleMapper.selectSiteArticleList(article);
    }

    @Override
    public List<SiteArticleVo> toSiteArticleCards(List<CmsArticle> articles)
    {
        List<SiteArticleVo> list = new ArrayList<>();
        if (articles == null)
        {
            return list;
        }
        for (CmsArticle article : articles)
        {
            list.add(new SiteArticleVo(article));
        }
        return list;
    }

    @Override
    public SiteArticleVo selectSiteArticleDetail(Long articleId, int relatedLimit)
    {
        CmsArticle article = articleMapper.selectArticleById(articleId);
        if (article == null || !"0".equals(article.getStatus()))
        {
            return null;
        }
        articleMapper.incrementViewCount(articleId);

        SiteArticleVo vo = new SiteArticleVo(article);
        vo.setContent(article.getContent());

        int limit = relatedLimit <= 0 ? 4 : Math.min(relatedLimit, 10);
        List<CmsArticle> related = articleMapper.selectRelatedArticles(article.getCategoryId(), articleId, limit);
        vo.setRelated(toSiteArticleCards(related));
        return vo;
    }

    @Override
    public int insertArticle(CmsArticle article)
    {
        validate(article);
        article.setContent(CmsHtmlSanitizer.clean(article.getContent()));
        if (article.getStatus() == null)
        {
            article.setStatus("0");
        }
        if (article.getIsTop() == null)
        {
            article.setIsTop("0");
        }
        if (article.getViewCount() == null)
        {
            article.setViewCount(0);
        }
        article.setCreateBy(SecurityUtils.getUsername());
        return articleMapper.insertArticle(article);
    }

    @Override
    public int updateArticle(CmsArticle article)
    {
        if (article.getArticleId() == null)
        {
            throw new ServiceException("文章ID不能为空");
        }
        validate(article);
        article.setContent(CmsHtmlSanitizer.clean(article.getContent()));
        article.setUpdateBy(SecurityUtils.getUsername());
        return articleMapper.updateArticle(article);
    }

    @Override
    public int deleteArticleByIds(Long[] articleIds)
    {
        return articleMapper.deleteArticleByIds(articleIds);
    }

    private void validate(CmsArticle article)
    {
        if (StringUtils.isEmpty(article.getTitle()))
        {
            throw new ServiceException("文章标题不能为空");
        }
    }
}
