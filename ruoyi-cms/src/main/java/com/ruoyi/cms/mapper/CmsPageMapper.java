package com.ruoyi.cms.mapper;

import java.util.List;

import com.ruoyi.cms.domain.CmsPage;

/**
 * 官网单页 Mapper
 *
 * @author site
 */
public interface CmsPageMapper
{
    public List<CmsPage> selectPageList(CmsPage page);

    public CmsPage selectPageById(Long pageId);

    public CmsPage selectPageBySlug(String slug);

    /** 仅用于唯一性校验（不限状态） */
    public CmsPage selectPageBySlugAny(String slug);

    public int insertPage(CmsPage page);

    public int updatePage(CmsPage page);

    public int deletePageByIds(Long[] pageIds);
}
