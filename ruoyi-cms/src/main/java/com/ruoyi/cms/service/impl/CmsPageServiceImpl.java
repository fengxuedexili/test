package com.ruoyi.cms.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.cms.domain.CmsPage;
import com.ruoyi.cms.domain.vo.SitePageVo;
import com.ruoyi.cms.mapper.CmsPageMapper;
import com.ruoyi.cms.service.ICmsPageService;
import com.ruoyi.cms.util.CmsHtmlSanitizer;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;

/**
 * 官网单页 Service 实现
 *
 * @author site
 */
@Service
public class CmsPageServiceImpl implements ICmsPageService
{
    @Autowired
    private CmsPageMapper pageMapper;

    @Override
    public List<CmsPage> selectPageList(CmsPage page)
    {
        return pageMapper.selectPageList(page);
    }

    @Override
    public CmsPage selectPageById(Long pageId)
    {
        return pageMapper.selectPageById(pageId);
    }

    @Override
    public SitePageVo selectSitePageBySlug(String slug)
    {
        if (StringUtils.isEmpty(slug))
        {
            return null;
        }
        CmsPage page = pageMapper.selectPageBySlug(slug);
        if (page == null)
        {
            return null;
        }
        SitePageVo vo = new SitePageVo();
        vo.setId(page.getPageId());
        vo.setSlug(page.getSlug());
        vo.setTitle(page.getTitle());
        vo.setBlocks(parseBlocks(page.getBlocks()));
        vo.getSeo().put("title", page.getSeoTitle());
        vo.getSeo().put("keywords", page.getSeoKeywords());
        vo.getSeo().put("description", page.getSeoDescription());
        return vo;
    }

    @Override
    public int insertPage(CmsPage page)
    {
        validate(page);
        if (!checkSlugUnique(page))
        {
            throw new ServiceException("页面别名已存在：" + page.getSlug());
        }
        if (page.getStatus() == null)
        {
            page.setStatus("0");
        }
        page.setBlocks(sanitizeBlocks(page.getBlocks()));
        page.setCreateBy(SecurityUtils.getUsername());
        return pageMapper.insertPage(page);
    }

    @Override
    public int updatePage(CmsPage page)
    {
        if (page.getPageId() == null)
        {
            throw new ServiceException("页面ID不能为空");
        }
        validate(page);
        if (!checkSlugUnique(page))
        {
            throw new ServiceException("页面别名已存在：" + page.getSlug());
        }
        page.setBlocks(sanitizeBlocks(page.getBlocks()));
        page.setUpdateBy(SecurityUtils.getUsername());
        return pageMapper.updatePage(page);
    }

    @Override
    public int deletePageByIds(Long[] pageIds)
    {
        return pageMapper.deletePageByIds(pageIds);
    }

    private List<Map<String, Object>> parseBlocks(String json)
    {
        List<Map<String, Object>> list = new ArrayList<>();
        if (StringUtils.isEmpty(json))
        {
            return list;
        }
        try
        {
            JSONArray array = JSON.parseArray(json);
            if (array == null)
            {
                return list;
            }
            for (int i = 0; i < array.size(); i++)
            {
                list.add(array.getJSONObject(i));
            }
        }
        catch (Exception e)
        {
            // 区块格式错误时返回空，前台显示空状态
        }
        return list;
    }

    /**
     * 清洗 blocks 中 richText 的 html 字段（服务端兜底）
     */
    private String sanitizeBlocks(String blocks)
    {
        if (StringUtils.isEmpty(blocks))
        {
            return blocks;
        }
        try
        {
            JSONArray array = JSON.parseArray(blocks);
            if (array == null)
            {
                return blocks;
            }
            for (int i = 0; i < array.size(); i++)
            {
                JSONObject block = array.getJSONObject(i);
                if (block == null)
                {
                    continue;
                }
                JSONObject props = block.getJSONObject("props");
                if (props != null && props.containsKey("html"))
                {
                    props.put("html", CmsHtmlSanitizer.clean(props.getString("html")));
                }
            }
            return array.toJSONString();
        }
        catch (Exception e)
        {
            return blocks;
        }
    }

    private boolean checkSlugUnique(CmsPage page)
    {
        CmsPage existing = pageMapper.selectPageBySlugAny(page.getSlug());
        if (existing == null)
        {
            return true;
        }
        return page.getPageId() != null && existing.getPageId().equals(page.getPageId());
    }

    private void validate(CmsPage page)
    {
        if (StringUtils.isEmpty(page.getSlug()))
        {
            throw new ServiceException("页面别名不能为空");
        }
        if (StringUtils.isEmpty(page.getTitle()))
        {
            throw new ServiceException("页面标题不能为空");
        }
        if (StringUtils.isNotEmpty(page.getBlocks()))
        {
            try
            {
                JSON.parse(page.getBlocks());
            }
            catch (Exception e)
            {
                throw new ServiceException("区块内容不是合法的 JSON：" + e.getMessage());
            }
        }
    }
}
