package com.ruoyi.cms.service.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.cms.domain.CmsCategory;
import com.ruoyi.cms.mapper.CmsCategoryMapper;
import com.ruoyi.cms.mapper.CmsProductMapper;
import com.ruoyi.cms.service.ICmsCategoryService;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;

/**
 * 官网栏目 Service 实现
 *
 * @author site
 */
@Service
public class CmsCategoryServiceImpl implements ICmsCategoryService
{
    @Autowired
    private CmsCategoryMapper categoryMapper;

    @Autowired
    private CmsProductMapper productMapper;

    @Override
    public List<CmsCategory> selectCategoryList(CmsCategory category)
    {
        return categoryMapper.selectCategoryList(category);
    }

    @Override
    public List<CmsCategory> selectCategoryTree(String type)
    {
        CmsCategory query = new CmsCategory();
        query.setType(type);
        return buildTree(categoryMapper.selectCategoryList(query));
    }

    @Override
    public List<CmsCategory> selectSiteCategoryTree(String type)
    {
        CmsCategory query = new CmsCategory();
        query.setType(type);
        query.setStatus("0");
        return buildTree(categoryMapper.selectCategoryList(query));
    }

    private List<CmsCategory> buildTree(List<CmsCategory> all)
    {
        Map<Long, CmsCategory> indexed = new LinkedHashMap<>();
        for (CmsCategory item : all)
        {
            item.setChildren(new ArrayList<>());
            indexed.put(item.getCategoryId(), item);
        }

        List<CmsCategory> roots = new ArrayList<>();
        for (CmsCategory item : all)
        {
            Long parentId = item.getParentId();
            CmsCategory parent = (parentId == null || parentId == 0L) ? null : indexed.get(parentId);
            if (parent == null)
            {
                roots.add(item);
            }
            else
            {
                parent.getChildren().add(item);
            }
        }
        return roots;
    }

    @Override
    public CmsCategory selectCategoryById(Long categoryId)
    {
        return categoryMapper.selectCategoryById(categoryId);
    }

    @Override
    public int insertCategory(CmsCategory category)
    {
        validate(category);
        if (!checkSlugUnique(category))
        {
            throw new ServiceException("栏目别名已存在：" + category.getSlug());
        }
        category.setCreateBy(SecurityUtils.getUsername());
        return categoryMapper.insertCategory(category);
    }

    @Override
    public int updateCategory(CmsCategory category)
    {
        validate(category);
        if (!checkSlugUnique(category))
        {
            throw new ServiceException("栏目别名已存在：" + category.getSlug());
        }
        category.setUpdateBy(SecurityUtils.getUsername());
        return categoryMapper.updateCategory(category);
    }

    @Override
    public int deleteCategoryByIds(Long[] categoryIds)
    {
        for (Long categoryId : categoryIds)
        {
            if (categoryMapper.countChildren(categoryId) > 0)
            {
                throw new ServiceException("存在子栏目，不允许删除");
            }
            if (productMapper.countProductByCategoryId(categoryId) > 0)
            {
                throw new ServiceException("栏目下存在产品，不允许删除");
            }
        }
        return categoryMapper.deleteCategoryByIds(categoryIds);
    }

    @Override
    public boolean checkSlugUnique(CmsCategory category)
    {
        if (StringUtils.isEmpty(category.getSlug()))
        {
            return false;
        }
        CmsCategory existing = categoryMapper.checkSlugUnique(category.getSlug());
        if (existing == null)
        {
            return true;
        }
        return category.getCategoryId() != null && existing.getCategoryId().equals(category.getCategoryId());
    }

    private void validate(CmsCategory category)
    {
        if (StringUtils.isEmpty(category.getName()))
        {
            throw new ServiceException("栏目名称不能为空");
        }
        if (StringUtils.isEmpty(category.getSlug()))
        {
            throw new ServiceException("栏目别名不能为空");
        }
        if (StringUtils.isEmpty(category.getType()))
        {
            category.setType("1");
        }
        if (category.getParentId() == null)
        {
            category.setParentId(0L);
        }
    }
}
