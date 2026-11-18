package com.ruoyi.cms.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.cms.domain.CmsProduct;

/**
 * 官网产品 Mapper
 *
 * @author site
 */
public interface CmsProductMapper
{
    public List<CmsProduct> selectProductList(CmsProduct product);

    public List<CmsProduct> selectSiteProductList(CmsProduct product);

    public CmsProduct selectProductById(Long productId);

    public List<CmsProduct> selectRecommendProducts(@Param("limit") int limit);

    public List<CmsProduct> selectRelatedProducts(@Param("categoryId") Long categoryId,
            @Param("excludeId") Long excludeId, @Param("limit") int limit);

    public int insertProduct(CmsProduct product);

    public int updateProduct(CmsProduct product);

    public int deleteProductByIds(Long[] productIds);

    public int incrementViewCount(Long productId);

    public int countProductByCategoryId(Long categoryId);
}
