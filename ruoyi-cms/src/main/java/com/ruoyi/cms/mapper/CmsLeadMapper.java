package com.ruoyi.cms.mapper;

import java.util.List;

import com.ruoyi.cms.domain.CmsLead;

/**
 * 官网线索 Mapper
 *
 * @author site
 */
public interface CmsLeadMapper
{
    public List<CmsLead> selectLeadList(CmsLead lead);

    public CmsLead selectLeadById(Long leadId);

    public int insertLead(CmsLead lead);

    public int updateLead(CmsLead lead);

    public int deleteLeadByIds(Long[] leadIds);
}
