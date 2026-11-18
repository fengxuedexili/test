package com.ruoyi.cms.service;

import java.util.List;

import com.ruoyi.cms.domain.CmsBanner;
import com.ruoyi.cms.domain.vo.SiteBannerVo;

/**
 * 官网轮播 Service
 *
 * @author site
 */
public interface ICmsBannerService
{
    public List<CmsBanner> selectBannerList(CmsBanner banner);

    /** 前台轮播（仅正常状态 + 当前时段） */
    public List<SiteBannerVo> selectSiteBanners(String position);

    public CmsBanner selectBannerById(Long bannerId);

    public int insertBanner(CmsBanner banner);

    public int updateBanner(CmsBanner banner);

    public int deleteBannerByIds(Long[] bannerIds);
}
