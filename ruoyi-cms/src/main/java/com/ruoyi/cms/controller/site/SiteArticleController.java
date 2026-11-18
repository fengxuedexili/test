package com.ruoyi.cms.controller.site;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.PageInfo;
import com.ruoyi.cms.domain.CmsArticle;
import com.ruoyi.cms.domain.vo.SiteArticleVo;
import com.ruoyi.cms.service.ICmsArticleService;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;

/**
 * 前台 - 文章
 *
 * @author site
 */
@Anonymous
@RestController
@RequestMapping("/api/site/article")
public class SiteArticleController extends BaseController
{
    @Autowired
    private ICmsArticleService articleService;

    /**
     * 资讯分页：categoryId / title / pageNum / pageSize
     */
    @GetMapping("/page")
    public AjaxResult page(CmsArticle query)
    {
        startPage();
        List<CmsArticle> list = articleService.selectSiteArticleList(query);
        PageInfo<CmsArticle> pageInfo = new PageInfo<>(list);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("total", pageInfo.getTotal());
        data.put("pageNum", pageInfo.getPageNum());
        data.put("pageSize", pageInfo.getPageSize());
        data.put("rows", articleService.toSiteArticleCards(list));
        return AjaxResult.success(data);
    }

    /**
     * 文章详情（浏览量 +1，含相关推荐）
     */
    @GetMapping("/{articleId}")
    public AjaxResult detail(@PathVariable Long articleId)
    {
        SiteArticleVo vo = articleService.selectSiteArticleDetail(articleId, 4);
        if (vo == null)
        {
            return AjaxResult.error("文章不存在或已下线");
        }
        return AjaxResult.success(vo);
    }
}
