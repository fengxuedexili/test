package com.ruoyi.cms.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.cms.domain.CmsBanner;

/**
 * 官网轮播 Mapper
 *
 * @author site
 */
public interface CmsBannerMapper
{
    public List<CmsBanner> selectBannerList(CmsBanner banner);

    public List<CmsBanner> selectSiteBanners(@Param("position") String position);

    public CmsBanner selectBannerById(Long bannerId);

    public int insertBanner(CmsBanner banner);

    public int updateBanner(CmsBanner banner);

    public int deleteBannerByIds(Long[] bannerIds);
}
