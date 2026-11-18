package com.ruoyi.cms.domain.vo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 前台单页 VO
 *
 * @author site
 */
public class SitePageVo
{
    private Long id;

    private String slug;

    private String title;

    /** 区块数组：[{"type":"richText","props":{...}}] */
    private List<Map<String, Object>> blocks = new ArrayList<>();

    private Map<String, String> seo = new LinkedHashMap<>();

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getSlug()
    {
        return slug;
    }

    public void setSlug(String slug)
    {
        this.slug = slug;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public List<Map<String, Object>> getBlocks()
    {
        return blocks;
    }

    public void setBlocks(List<Map<String, Object>> blocks)
    {
        this.blocks = blocks;
    }

    public Map<String, String> getSeo()
    {
        return seo;
    }

    public void setSeo(Map<String, String> seo)
    {
        this.seo = seo;
    }
}
