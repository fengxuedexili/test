package com.ruoyi.cms.service;

import java.util.List;

import com.ruoyi.cms.domain.CmsPage;
import com.ruoyi.cms.domain.vo.SitePageVo;

/**
 * 官网单页 Service
 *
 * @author site
 */
public interface ICmsPageService
{
    public List<CmsPage> selectPageList(CmsPage page);

    public CmsPage selectPageById(Long pageId);

    /** 前台按 slug 读取（仅 status=0） */
    public SitePageVo selectSitePageBySlug(String slug);

    public int insertPage(CmsPage page);

    public int updatePage(CmsPage page);

    public int deletePageByIds(Long[] pageIds);
}
