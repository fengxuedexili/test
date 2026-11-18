package com.ruoyi.cms.domain.vo;

import java.math.BigDecimal;

import com.ruoyi.cms.domain.CmsStore;

/**
 * 前台门店 VO
 *
 * @author site
 */
public class SiteStoreVo
{
    private Long id;

    private String name;

    private String province;

    private String city;

    private String district;

    private String address;

    private String phone;

    private String type;

    private BigDecimal lng;

    private BigDecimal lat;

    public SiteStoreVo()
    {
    }

    public SiteStoreVo(CmsStore store)
    {
        this.id = store.getStoreId();
        this.name = store.getName();
        this.province = store.getProvince();
        this.city = store.getCity();
        this.district = store.getDistrict();
        this.address = store.getAddress();
        this.phone = store.getPhone();
        this.type = store.getType();
        this.lng = store.getLng();
        this.lat = store.getLat();
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getProvince()
    {
        return province;
    }

    public void setProvince(String province)
    {
        this.province = province;
    }

    public String getCity()
    {
        return city;
    }

    public void setCity(String city)
    {
        this.city = city;
    }

    public String getDistrict()
    {
        return district;
    }

    public void setDistrict(String district)
    {
        this.district = district;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public BigDecimal getLng()
    {
        return lng;
    }

    public void setLng(BigDecimal lng)
    {
        this.lng = lng;
    }

    public BigDecimal getLat()
    {
        return lat;
    }

    public void setLat(BigDecimal lat)
    {
        this.lat = lat;
    }
}
