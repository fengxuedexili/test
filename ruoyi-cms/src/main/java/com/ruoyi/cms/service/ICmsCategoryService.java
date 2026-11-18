package com.ruoyi.cms.service;

import java.util.List;

import com.ruoyi.cms.domain.CmsCategory;

/**
 * 官网栏目 Service
 *
 * @author site
 */
public interface ICmsCategoryService
{
    public List<CmsCategory> selectCategoryList(CmsCategory category);

    /** 栏目树（type 为空时返回全部类型） */
    public List<CmsCategory> selectCategoryTree(String type);

    /** 前台栏目树（仅 status='0'） */
    public List<CmsCategory> selectSiteCategoryTree(String type);

    public CmsCategory selectCategoryById(Long categoryId);

    public int insertCategory(CmsCategory category);

    public int updateCategory(CmsCategory category);

    public int deleteCategoryByIds(Long[] categoryIds);

    public boolean checkSlugUnique(CmsCategory category);
}
