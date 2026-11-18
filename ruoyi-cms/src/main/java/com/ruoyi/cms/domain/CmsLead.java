package com.ruoyi.cms.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 官网线索 cms_lead
 *
 * @author site
 */
public class CmsLead extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @Excel(name = "线索ID")
    private Long leadId;

    /** 类型：1 预约体验 / 2 招商加盟 / 3 招聘 / 4 售后服务 */
    @Excel(name = "类型", readConverterExp = "1=预约体验,2=招商加盟,3=招聘,4=售后服务")
    private String type;

    @Excel(name = "姓名")
    private String name;

    @Excel(name = "手机号")
    private String phone;

    @Excel(name = "省")
    private String province;

    @Excel(name = "市")
    private String city;

    @Excel(name = "区/县")
    private String district;

    @Excel(name = "关联产品ID")
    private Long productId;

    @Excel(name = "留言/需求")
    private String content;

    @Excel(name = "来源页面")
    private String source;

    @Excel(name = "UTM")
    private String utm;

    @Excel(name = "提交IP")
    private String ip;

    /** 状态：0 待处理 / 1 已联系 / 2 无效 / 3 已成交 */
    @Excel(name = "状态", readConverterExp = "0=待处理,1=已联系,2=无效,3=已成交")
    private String status;

    @Excel(name = "处理人")
    private String handler;

    @Excel(name = "处理时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date handleTime;

    /** 蜜罐字段：正常用户留空，机器人会填 */
    private String website;

    /** 幂等键（不入库） */
    private String requestId;

    /** 验证码（不入库） */
    private String code;

    /** 验证码 uuid（不入库） */
    private String uuid;

    public Long getLeadId()
    {
        return leadId;
    }

    public void setLeadId(Long leadId)
    {
        this.leadId = leadId;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
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

    public Long getProductId()
    {
        return productId;
    }

    public void setProductId(Long productId)
    {
        this.productId = productId;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public String getSource()
    {
        return source;
    }

    public void setSource(String source)
    {
        this.source = source;
    }

    public String getUtm()
    {
        return utm;
    }

    public void setUtm(String utm)
    {
        this.utm = utm;
    }

    public String getIp()
    {
        return ip;
    }

    public void setIp(String ip)
    {
        this.ip = ip;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getHandler()
    {
        return handler;
    }

    public void setHandler(String handler)
    {
        this.handler = handler;
    }

    public Date getHandleTime()
    {
        return handleTime;
    }

    public void setHandleTime(Date handleTime)
    {
        this.handleTime = handleTime;
    }

    public String getWebsite()
    {
        return website;
    }

    public void setWebsite(String website)
    {
        this.website = website;
    }

    public String getRequestId()
    {
        return requestId;
    }

    public void setRequestId(String requestId)
    {
        this.requestId = requestId;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public String getUuid()
    {
        return uuid;
    }

    public void setUuid(String uuid)
    {
        this.uuid = uuid;
    }
}
