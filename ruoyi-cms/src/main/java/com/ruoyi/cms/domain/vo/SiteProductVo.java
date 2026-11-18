package com.ruoyi.cms.domain.vo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.ruoyi.cms.domain.CmsProduct;

/**
 * 前台产品 VO（详情与列表共用；不暴露 del_flag / create_by / remark）
 *
 * @author site
 */
public class SiteProductVo
{
    private Long id;

    private Long categoryId;

    private String categoryName;

    private String model;

    private String name;

    private String subtitle;

    private String cover;

    /** 悬停图（图集第二张，可为空） */
    private String hoverImage;

    private List<String> tags = new ArrayList<>();

    private String videoUrl;

    private List<String> images = new ArrayList<>();

    /** 产品看点/详情长图：[{"url":"","caption":""}] */
    private List<Map<String, Object>> detailImages = new ArrayList<>();

    private List<Map<String, Object>> sellingPoints = new ArrayList<>();

    private List<Map<String, Object>> paramGroups = new ArrayList<>();

    private List<Map<String, Object>> downloads = new ArrayList<>();

    private boolean recommend;

    private String publishTime;

    private Map<String, String> seo = new LinkedHashMap<>();

    /** 相关推荐（仅详情返回） */
    private List<SiteProductVo> related = new ArrayList<>();

    public SiteProductVo()
    {
    }

    public SiteProductVo(CmsProduct product)
    {
        this.id = product.getProductId();
        this.categoryId = product.getCategoryId();
        this.categoryName = product.getCategoryName();
        this.model = product.getModel();
        this.name = product.getName();
        this.subtitle = product.getSubtitle();
        this.cover = product.getCover();
        this.videoUrl = product.getVideoUrl();
        this.recommend = "1".equals(product.getIsRecommend());
        this.publishTime = product.getPublishTime() == null ? null : new java.text.SimpleDateFormat("yyyy-MM-dd").format(product.getPublishTime());
        this.seo.put("title", product.getSeoTitle());
        this.seo.put("keywords", product.getSeoKeywords());
        this.seo.put("description", product.getSeoDescription());
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getCategoryId()
    {
        return categoryId;
    }

    public void setCategoryId(Long categoryId)
    {
        this.categoryId = categoryId;
    }

    public String getCategoryName()
    {
        return categoryName;
    }

    public void setCategoryName(String categoryName)
    {
        this.categoryName = categoryName;
    }

    public String getModel()
    {
        return model;
    }

    public void setModel(String model)
    {
        this.model = model;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getSubtitle()
    {
        return subtitle;
    }

    public void setSubtitle(String subtitle)
    {
        this.subtitle = subtitle;
    }

    public String getCover()
    {
        return cover;
    }

    public void setCover(String cover)
    {
        this.cover = cover;
    }

    public String getHoverImage()
    {
        return hoverImage;
    }

    public void setHoverImage(String hoverImage)
    {
        this.hoverImage = hoverImage;
    }

    public List<String> getTags()
    {
        return tags;
    }

    public void setTags(List<String> tags)
    {
        this.tags = tags;
    }

    public String getVideoUrl()
    {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl)
    {
        this.videoUrl = videoUrl;
    }

    public List<String> getImages()
    {
        return images;
    }

    public void setImages(List<String> images)
    {
        this.images = images;
    }

    public List<Map<String, Object>> getDetailImages()
    {
        return detailImages;
    }

    public void setDetailImages(List<Map<String, Object>> detailImages)
    {
        this.detailImages = detailImages;
    }

    public List<Map<String, Object>> getSellingPoints()
    {
        return sellingPoints;
    }

    public void setSellingPoints(List<Map<String, Object>> sellingPoints)
    {
        this.sellingPoints = sellingPoints;
    }

    public List<Map<String, Object>> getParamGroups()
    {
        return paramGroups;
    }

    public void setParamGroups(List<Map<String, Object>> paramGroups)
    {
        this.paramGroups = paramGroups;
    }

    public List<Map<String, Object>> getDownloads()
    {
        return downloads;
    }

    public void setDownloads(List<Map<String, Object>> downloads)
    {
        this.downloads = downloads;
    }

    public boolean isRecommend()
    {
        return recommend;
    }

    public void setRecommend(boolean recommend)
    {
        this.recommend = recommend;
    }

    public String getPublishTime()
    {
        return publishTime;
    }

    public void setPublishTime(String publishTime)
    {
        this.publishTime = publishTime;
    }

    public Map<String, String> getSeo()
    {
        return seo;
    }

    public void setSeo(Map<String, String> seo)
    {
        this.seo = seo;
    }

    public List<SiteProductVo> getRelated()
    {
        return related;
    }

    public void setRelated(List<SiteProductVo> related)
    {
        this.related = related;
    }
}
