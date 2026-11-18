package com.ruoyi.cms.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.cms.domain.CmsProduct;
import com.ruoyi.cms.domain.vo.SiteProductVo;
import com.ruoyi.cms.mapper.CmsProductMapper;
import com.ruoyi.cms.service.ICmsProductService;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;

/**
 * 官网产品 Service 实现
 *
 * @author site
 */
@Service
public class CmsProductServiceImpl implements ICmsProductService
{
    @Autowired
    private CmsProductMapper productMapper;

    @Override
    public List<CmsProduct> selectProductList(CmsProduct product)
    {
        return productMapper.selectProductList(product);
    }

    @Override
    public CmsProduct selectProductById(Long productId)
    {
        return productMapper.selectProductById(productId);
    }

    @Override
    public List<CmsProduct> selectSiteProductList(CmsProduct product)
    {
        return productMapper.selectSiteProductList(product);
    }

    @Override
    public List<SiteProductVo> selectRecommendProducts(int limit)
    {
        return toCards(productMapper.selectRecommendProducts(normalizeLimit(limit)));
    }

    @Override
    public List<SiteProductVo> toSiteCardList(List<CmsProduct> products)
    {
        return toCards(products);
    }

    @Override
    public SiteProductVo selectSiteProductDetail(Long productId, int relatedLimit)
    {
        CmsProduct product = productMapper.selectProductById(productId);
        if (product == null || !"0".equals(product.getStatus()))
        {
            return null;
        }

        productMapper.incrementViewCount(productId);

        SiteProductVo vo = toCard(product);
        vo.setVideoUrl(product.getVideoUrl());
        vo.setSellingPoints(parseMapList(product.getSellingPoints()));
        vo.setParamGroups(parseMapList(product.getParamsJson()));
        vo.setDownloads(parseMapList(product.getDownloads()));
        vo.setDetailImages(parseMapList(product.getDetailImages()));
        if (vo.getImages().isEmpty() && StringUtils.isNotEmpty(product.getCover()))
        {
            vo.getImages().add(product.getCover());
        }

        int limit = normalizeLimit(relatedLimit);
        if (product.getCategoryId() != null)
        {
            vo.setRelated(toCards(productMapper.selectRelatedProducts(product.getCategoryId(), productId, limit)));
        }
        return vo;
    }

    @Override
    public int insertProduct(CmsProduct product)
    {
        validate(product);
        if (product.getStatus() == null)
        {
            product.setStatus("0");
        }
        if (product.getIsRecommend() == null)
        {
            product.setIsRecommend("0");
        }
        if (product.getSort() == null)
        {
            product.setSort(0);
        }
        if (product.getViewCount() == null)
        {
            product.setViewCount(0);
        }
        product.setCreateBy(SecurityUtils.getUsername());
        return productMapper.insertProduct(product);
    }

    @Override
    public int updateProduct(CmsProduct product)
    {
        if (product.getProductId() == null)
        {
            throw new ServiceException("产品ID不能为空");
        }
        validateJsonFields(product);
        product.setUpdateBy(SecurityUtils.getUsername());
        return productMapper.updateProduct(product);
    }

    @Override
    public int deleteProductByIds(Long[] productIds)
    {
        return productMapper.deleteProductByIds(productIds);
    }

    @Override
    public int countProductByCategoryId(Long categoryId)
    {
        return productMapper.countProductByCategoryId(categoryId);
    }

    private List<SiteProductVo> toCards(List<CmsProduct> products)
    {
        List<SiteProductVo> list = new ArrayList<>();
        if (products == null)
        {
            return list;
        }
        for (CmsProduct product : products)
        {
            list.add(toCard(product));
        }
        return list;
    }

    private SiteProductVo toCard(CmsProduct product)
    {
        SiteProductVo vo = new SiteProductVo(product);
        List<String> images = parseStringList(product.getImages());
        vo.setImages(images);
        if (images.size() > 1)
        {
            vo.setHoverImage(images.get(1));
        }
        vo.setTags(parseTags(product.getTags()));
        return vo;
    }

    private List<String> parseStringList(String json)
    {
        if (StringUtils.isEmpty(json))
        {
            return new ArrayList<>();
        }
        try
        {
            List<String> list = JSON.parseArray(json, String.class);
            return list == null ? new ArrayList<>() : list;
        }
        catch (Exception e)
        {
            return new ArrayList<>();
        }
    }

    private List<Map<String, Object>> parseMapList(String json)
    {
        List<Map<String, Object>> list = new ArrayList<>();
        if (StringUtils.isEmpty(json))
        {
            return list;
        }
        try
        {
            com.alibaba.fastjson2.JSONArray array = JSON.parseArray(json);
            if (array == null)
            {
                return list;
            }
            for (int i = 0; i < array.size(); i++)
            {
                list.add(array.getJSONObject(i));
            }
            return list;
        }
        catch (Exception e)
        {
            return new ArrayList<>();
        }
    }

    private List<String> parseTags(String tags)
    {
        List<String> list = new ArrayList<>();
        if (StringUtils.isEmpty(tags))
        {
            return list;
        }
        for (String tag : tags.split(","))
        {
            if (StringUtils.isNotEmpty(tag.trim()))
            {
                list.add(tag.trim());
            }
        }
        return list;
    }

    private int normalizeLimit(int limit)
    {
        if (limit <= 0)
        {
            return 6;
        }
        return Math.min(limit, 50);
    }

    private void validate(CmsProduct product)
    {
        if (product.getCategoryId() == null)
        {
            throw new ServiceException("所属栏目不能为空");
        }
        if (StringUtils.isEmpty(product.getModel()))
        {
            throw new ServiceException("产品型号不能为空");
        }
        if (StringUtils.isEmpty(product.getName()))
        {
            throw new ServiceException("产品名称不能为空");
        }
        validateJsonFields(product);
    }

    private void validateJsonFields(CmsProduct product)
    {
        validateJson(product.getImages(), "图集");
        validateJson(product.getDetailImages(), "产品看点");
        validateJson(product.getSellingPoints(), "卖点");
        validateJson(product.getParamsJson(), "参数");
        validateJson(product.getDownloads(), "下载资料");
    }

    private void validateJson(String json, String label)
    {
        if (StringUtils.isEmpty(json))
        {
            return;
        }
        try
        {
            JSON.parse(json);
        }
        catch (Exception e)
        {
            throw new ServiceException(label + "不是合法的 JSON：" + e.getMessage());
        }
    }
}

