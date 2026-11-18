package com.ruoyi.cms.domain.vo;

import com.ruoyi.cms.domain.CmsBanner;

/**
 * 前台轮播 VO
 *
 * @author site
 */
public class SiteBannerVo
{
    private Long id;

    private String mediaType;

    private String imageUrl;

    private String videoUrl;

    private String title;

    private String subtitle;

    private String btnText;

    private String linkUrl;

    public SiteBannerVo()
    {
    }

    public SiteBannerVo(CmsBanner banner)
    {
        this.id = banner.getBannerId();
        this.mediaType = banner.getMediaType();
        this.imageUrl = banner.getImageUrl();
        this.videoUrl = banner.getVideoUrl();
        this.title = banner.getTitle();
        this.subtitle = banner.getSubtitle();
        this.btnText = banner.getBtnText();
        this.linkUrl = banner.getLinkUrl();
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getMediaType()
    {
        return mediaType;
    }

    public void setMediaType(String mediaType)
    {
        this.mediaType = mediaType;
    }

    public String getImageUrl()
    {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl)
    {
        this.imageUrl = imageUrl;
    }

    public String getVideoUrl()
    {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl)
    {
        this.videoUrl = videoUrl;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getSubtitle()
    {
        return subtitle;
    }

    public void setSubtitle(String subtitle)
    {
        this.subtitle = subtitle;
    }

    public String getBtnText()
    {
        return btnText;
    }

    public void setBtnText(String btnText)
    {
        this.btnText = btnText;
    }

    public String getLinkUrl()
    {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl)
    {
        this.linkUrl = linkUrl;
    }
}
