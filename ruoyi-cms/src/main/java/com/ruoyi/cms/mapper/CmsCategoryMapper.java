package com.ruoyi.cms.mapper;

import java.util.List;

import com.ruoyi.cms.domain.CmsCategory;

/**
 * 官网栏目 Mapper
 *
 * @author site
 */
public interface CmsCategoryMapper
{
    public List<CmsCategory> selectCategoryList(CmsCategory category);

    public CmsCategory selectCategoryById(Long categoryId);

    public CmsCategory checkSlugUnique(String slug);

    public int insertCategory(CmsCategory category);

    public int updateCategory(CmsCategory category);

    public int deleteCategoryByIds(Long[] categoryIds);

    public int countChildren(Long parentId);
}
