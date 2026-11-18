package com.ruoyi.cms.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 官网产品 cms_product
 *
 * 说明：images / sellingPoints / params / downloads 为 JSON 列，实体统一用 String 承载，
 * 由 service 负责序列化与反序列化，避免 MyBatis 类型处理器配置成本。
 *
 * @author site
 */
public class CmsProduct extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 产品ID */
    private Long productId;

    /** 栏目ID */
    private Long categoryId;

    /** 系列ID */
    private Long seriesId;

    /** 型号 */
    private String model;

    /** 产品名称 */
    private String name;

    /** 一句话卖点 */
    private String subtitle;

    /** 主图 */
    private String cover;

    /** 图集 JSON：["url1","url2"] */
    private String images;

    /** 产品看点/详情长图 JSON：[{"url":"","caption":""}] */
    private String detailImages;

    /** 产品视频 */
    private String videoUrl;

    /** 卖点 JSON：[{"icon":"","title":"","desc":""}] */
    private String sellingPoints;

    /** 参数分组 JSON：[{"group":"","items":[{"label":"","value":"","unit":""}]}]（注意：不能命名为 params，避免覆盖 BaseEntity 的查询参数） */
    private String paramsJson;

    /** 下载资料 JSON：[{"name":"","url":"","size":""}] */
    private String downloads;

    /** 标签（逗号分隔） */
    private String tags;

    /** 是否推荐：0 否 / 1 是 */
    private String isRecommend;

    /** 显示顺序 */
    private Integer sort;

    /** 发布时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date publishTime;

    /** 浏览量 */
    private Integer viewCount;

    /** 状态：0 正常 / 1 停用 */
    private String status;

    /** 删除标志：0 存在 / 2 删除 */
    private String delFlag;

    /** SEO 标题 */
    private String seoTitle;

    /** SEO 关键词 */
    private String seoKeywords;

    /** SEO 描述 */
    private String seoDescription;

    /** 栏目名称（联表查询，非持久化） */
    private String categoryName;

    /** 栏目别名（前台按 slug 查询，非持久化） */
    private String categorySlug;

    /** 关键字（前台搜索，非持久化） */
    private String keyword;

    public Long getProductId()
    {
        return productId;
    }

    public void setProductId(Long productId)
    {
        this.productId = productId;
    }

    public Long getCategoryId()
    {
        return categoryId;
    }

    public void setCategoryId(Long categoryId)
    {
        this.categoryId = categoryId;
    }

    public Long getSeriesId()
    {
        return seriesId;
    }

    public void setSeriesId(Long seriesId)
    {
        this.seriesId = seriesId;
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

    public String getImages()
    {
        return images;
    }

    public void setImages(String images)
    {
        this.images = images;
    }

    public String getDetailImages()
    {
        return detailImages;
    }

    public void setDetailImages(String detailImages)
    {
        this.detailImages = detailImages;
    }

    public String getVideoUrl()
    {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl)
    {
        this.videoUrl = videoUrl;
    }

    public String getSellingPoints()
    {
        return sellingPoints;
    }

    public void setSellingPoints(String sellingPoints)
    {
        this.sellingPoints = sellingPoints;
    }

    public String getParamsJson()
    {
        return paramsJson;
    }

    public void setParamsJson(String paramsJson)
    {
        this.paramsJson = paramsJson;
    }

    public String getDownloads()
    {
        return downloads;
    }

    public void setDownloads(String downloads)
    {
        this.downloads = downloads;
    }

    public String getTags()
    {
        return tags;
    }

    public void setTags(String tags)
    {
        this.tags = tags;
    }

    public String getIsRecommend()
    {
        return isRecommend;
    }

    public void setIsRecommend(String isRecommend)
    {
        this.isRecommend = isRecommend;
    }

    public Integer getSort()
    {
        return sort;
    }

    public void setSort(Integer sort)
    {
        this.sort = sort;
    }

    public Date getPublishTime()
    {
        return publishTime;
    }

    public void setPublishTime(Date publishTime)
    {
        this.publishTime = publishTime;
    }

    public Integer getViewCount()
    {
        return viewCount;
    }

    public void setViewCount(Integer viewCount)
    {
        this.viewCount = viewCount;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }

    public String getSeoTitle()
    {
        return seoTitle;
    }

    public void setSeoTitle(String seoTitle)
    {
        this.seoTitle = seoTitle;
    }

    public String getSeoKeywords()
    {
        return seoKeywords;
    }

    public void setSeoKeywords(String seoKeywords)
    {
        this.seoKeywords = seoKeywords;
    }

    public String getSeoDescription()
    {
        return seoDescription;
    }

    public void setSeoDescription(String seoDescription)
    {
        this.seoDescription = seoDescription;
    }

    public String getCategoryName()
    {
        return categoryName;
    }

    public void setCategoryName(String categoryName)
    {
        this.categoryName = categoryName;
    }

    public String getCategorySlug()
    {
        return categorySlug;
    }

    public void setCategorySlug(String categorySlug)
    {
        this.categorySlug = categorySlug;
    }

    public String getKeyword()
    {
        return keyword;
    }

    public void setKeyword(String keyword)
    {
        this.keyword = keyword;
    }
}
