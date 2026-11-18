package com.ruoyi.cms.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.cms.domain.CmsStore;
import com.ruoyi.cms.domain.vo.SiteStoreVo;
import com.ruoyi.cms.mapper.CmsStoreMapper;
import com.ruoyi.cms.service.ICmsStoreService;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;

/**
 * 官网门店 Service 实现
 *
 * @author site
 */
@Service
public class CmsStoreServiceImpl implements ICmsStoreService
{
    @Autowired
    private CmsStoreMapper storeMapper;

    @Override
    public List<CmsStore> selectStoreList(CmsStore store)
    {
        return storeMapper.selectStoreList(store);
    }

    @Override
    public CmsStore selectStoreById(Long storeId)
    {
        return storeMapper.selectStoreById(storeId);
    }

    @Override
    public List<SiteStoreVo> selectSiteStoreList(CmsStore store)
    {
        List<SiteStoreVo> result = new ArrayList<>();
        List<CmsStore> list = storeMapper.selectSiteStoreList(store);
        if (list != null)
        {
            for (CmsStore item : list)
            {
                result.add(new SiteStoreVo(item));
            }
        }
        return result;
    }

    @Override
    public List<String> selectProvinces()
    {
        return storeMapper.selectProvinces();
    }

    @Override
    public int insertStore(CmsStore store)
    {
        validate(store);
        if (store.getType() == null)
        {
            store.setType("2");
        }
        if (store.getSort() == null)
        {
            store.setSort(0);
        }
        if (store.getStatus() == null)
        {
            store.setStatus("0");
        }
        store.setCreateBy(SecurityUtils.getUsername());
        return storeMapper.insertStore(store);
    }

    @Override
    public int updateStore(CmsStore store)
    {
        if (store.getStoreId() == null)
        {
            throw new ServiceException("门店ID不能为空");
        }
        validate(store);
        store.setUpdateBy(SecurityUtils.getUsername());
        return storeMapper.updateStore(store);
    }

    @Override
    public int deleteStoreByIds(Long[] storeIds)
    {
        return storeMapper.deleteStoreByIds(storeIds);
    }

    private void validate(CmsStore store)
    {
        if (StringUtils.isEmpty(store.getName()))
        {
            throw new ServiceException("门店名称不能为空");
        }
    }
}
