package com.ruoyi.cms.service.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.cms.domain.vo.CmsDashboardVo;
import com.ruoyi.cms.mapper.CmsDashboardMapper;
import com.ruoyi.cms.service.ICmsDashboardService;

/**
 * 后台看板 Service 实现
 *
 * @author site
 */
@Service
public class CmsDashboardServiceImpl implements ICmsDashboardService
{
    private static final DateTimeFormatter DAY_FORMAT = DateTimeFormatter.ofPattern("MM-dd");

    @Autowired
    private CmsDashboardMapper dashboardMapper;

    @Override
    public CmsDashboardVo selectSummary()
    {
        CmsDashboardVo vo = new CmsDashboardVo();
        vo.setProductCount(dashboardMapper.countProducts());
        vo.setArticleCount(dashboardMapper.countPublishedArticles());
        vo.setPendingLeadCount(dashboardMapper.countPendingLeads());
        vo.setTodayLeadCount(dashboardMapper.countTodayLeads());
        vo.setStoreCount(dashboardMapper.countActiveStores());
        vo.setPageCount(dashboardMapper.countActivePages());
        vo.setRecentLeads(dashboardMapper.selectRecentLeads(5));
        vo.setLatestArticles(dashboardMapper.selectLatestArticles(5));
        vo.setLeadTrend(fillTrend(dashboardMapper.selectLeadTrend()));
        return vo;
    }

    /**
     * 把「仅有数据的日期」补齐为最近 7 天（含 0）
     */
    private List<Map<String, Object>> fillTrend(List<Map<String, Object>> raw)
    {
        Map<String, Long> counts = new HashMap<>();
        if (raw != null)
        {
            for (Map<String, Object> row : raw)
            {
                String day = null;
                long count = 0L;
                for (Map.Entry<String, Object> entry : row.entrySet())
                {
                    String key = entry.getKey() == null ? "" : entry.getKey().toLowerCase();
                    if ("d".equals(key) && entry.getValue() != null)
                    {
                        day = String.valueOf(entry.getValue());
                    }
                    else if ("c".equals(key) && entry.getValue() instanceof Number)
                    {
                        count = ((Number) entry.getValue()).longValue();
                    }
                }
                if (day != null)
                {
                    counts.put(day, count);
                }
            }
        }

        List<Map<String, Object>> trend = new ArrayList<>();
        for (int i = 6; i >= 0; i--)
        {
            String day = LocalDate.now().minusDays(i).format(DAY_FORMAT);
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("d", day);
            item.put("c", counts.getOrDefault(day, 0L));
            trend.add(item);
        }
        return trend;
    }
}
