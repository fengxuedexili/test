package com.ruoyi.cms.service;

import java.util.List;

import com.ruoyi.cms.domain.CmsProduct;
import com.ruoyi.cms.domain.vo.SiteProductVo;

/**
 * 官网产品 Service
 *
 * @author site
 */
public interface ICmsProductService
{
    public List<CmsProduct> selectProductList(CmsProduct product);

    public CmsProduct selectProductById(Long productId);

    /** 前台分页（只取已发布 + 正常状态） */
    public List<CmsProduct> selectSiteProductList(CmsProduct product);

    /** 前台推荐位 */
    public List<SiteProductVo> selectRecommendProducts(int limit);

    /** 实体列表转前台卡片 VO（分页/推荐共用） */
    public List<SiteProductVo> toSiteCardList(List<CmsProduct> products);

    /** 前台详情（含相关推荐，浏览量 +1） */
    public SiteProductVo selectSiteProductDetail(Long productId, int relatedLimit);

    public int insertProduct(CmsProduct product);

    public int updateProduct(CmsProduct product);

    public int deleteProductByIds(Long[] productIds);

    public int countProductByCategoryId(Long categoryId);
}
