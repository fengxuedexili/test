package com.ruoyi.cms.service;

import java.util.List;

import com.ruoyi.cms.domain.CmsSeries;

/**
 * 官网产品系列 Service
 *
 * @author site
 */
public interface ICmsSeriesService
{
    public List<CmsSeries> selectSeriesList(CmsSeries series);

    public CmsSeries selectSeriesById(Long seriesId);

    public int insertSeries(CmsSeries series);

    public int updateSeries(CmsSeries series);

    public int deleteSeriesByIds(Long[] seriesIds);
}
