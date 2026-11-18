package com.ruoyi.cms.service;

import java.util.List;

import com.ruoyi.cms.domain.CmsLead;

/**
 * 官网线索 Service
 *
 * @author site
 */
public interface ICmsLeadService
{
    public List<CmsLead> selectLeadList(CmsLead lead);

    public CmsLead selectLeadById(Long leadId);

    /** 前台提交（验证码 + 频控 + 蜜罐 + 幂等） */
    public int submitSiteLead(CmsLead lead);

    /** 后台跟进（仅状态/处理人/备注） */
    public int updateLead(CmsLead lead);

    public int deleteLeadByIds(Long[] leadIds);
}
