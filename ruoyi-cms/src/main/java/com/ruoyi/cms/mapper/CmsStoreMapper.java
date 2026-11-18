package com.ruoyi.cms.mapper;

import java.util.List;

import com.ruoyi.cms.domain.CmsStore;

/**
 * 官网门店 Mapper
 *
 * @author site
 */
public interface CmsStoreMapper
{
    public List<CmsStore> selectStoreList(CmsStore store);

    public List<CmsStore> selectSiteStoreList(CmsStore store);

    public CmsStore selectStoreById(Long storeId);

    public List<String> selectProvinces();

    public int insertStore(CmsStore store);

    public int updateStore(CmsStore store);

    public int deleteStoreByIds(Long[] storeIds);
}
