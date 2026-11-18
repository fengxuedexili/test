package com.ruoyi.cms.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.cms.domain.CmsSeries;
import com.ruoyi.cms.mapper.CmsSeriesMapper;
import com.ruoyi.cms.service.ICmsSeriesService;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;

/**
 * 官网产品系列 Service 实现
 *
 * @author site
 */
@Service
public class CmsSeriesServiceImpl implements ICmsSeriesService
{
    @Autowired
    private CmsSeriesMapper seriesMapper;

    @Override
    public List<CmsSeries> selectSeriesList(CmsSeries series)
    {
        return seriesMapper.selectSeriesList(series);
    }

    @Override
    public CmsSeries selectSeriesById(Long seriesId)
    {
        return seriesMapper.selectSeriesById(seriesId);
    }

    @Override
    public int insertSeries(CmsSeries series)
    {
        validate(series);
        if (series.getSort() == null)
        {
            series.setSort(0);
        }
        if (series.getStatus() == null)
        {
            series.setStatus("0");
        }
        series.setCreateBy(SecurityUtils.getUsername());
        return seriesMapper.insertSeries(series);
    }

    @Override
    public int updateSeries(CmsSeries series)
    {
        if (series.getSeriesId() == null)
        {
            throw new ServiceException("系列ID不能为空");
        }
        validate(series);
        series.setUpdateBy(SecurityUtils.getUsername());
        return seriesMapper.updateSeries(series);
    }

    @Override
    public int deleteSeriesByIds(Long[] seriesIds)
    {
        return seriesMapper.deleteSeriesByIds(seriesIds);
    }

    private void validate(CmsSeries series)
    {
        if (series.getCategoryId() == null)
        {
            throw new ServiceException("所属栏目不能为空");
        }
        if (StringUtils.isEmpty(series.getName()))
        {
            throw new ServiceException("系列名称不能为空");
        }
    }
}
