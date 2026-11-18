package com.ruoyi.cms.service;

import java.util.List;

import com.ruoyi.cms.domain.CmsStore;
import com.ruoyi.cms.domain.vo.SiteStoreVo;

/**
 * 官网门店 Service
 *
 * @author site
 */
public interface ICmsStoreService
{
    public List<CmsStore> selectStoreList(CmsStore store);

    public CmsStore selectStoreById(Long storeId);

    /** 前台门店列表 */
    public List<SiteStoreVo> selectSiteStoreList(CmsStore store);

    /** 前台省份列表 */
    public List<String> selectProvinces();

    public int insertStore(CmsStore store);

    public int updateStore(CmsStore store);

    public int deleteStoreByIds(Long[] storeIds);
}
