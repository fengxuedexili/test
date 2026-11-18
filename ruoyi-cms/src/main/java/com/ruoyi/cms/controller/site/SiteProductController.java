package com.ruoyi.cms.controller.site;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.PageInfo;
import com.ruoyi.cms.domain.CmsProduct;
import com.ruoyi.cms.domain.vo.SiteProductVo;
import com.ruoyi.cms.service.ICmsProductService;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;

/**
 * 前台 - 产品
 *
 * @author site
 */
@Anonymous
@RestController
@RequestMapping("/api/site/product")
public class SiteProductController extends BaseController
{
    @Autowired
    private ICmsProductService productService;

    /**
     * 产品分页：categoryId / categorySlug / keyword / pageNum / pageSize
     */
    @GetMapping("/page")
    public AjaxResult page(CmsProduct query)
    {
        startPage();
        List<CmsProduct> list = productService.selectSiteProductList(query);
        PageInfo<CmsProduct> pageInfo = new PageInfo<>(list);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("total", pageInfo.getTotal());
        data.put("pageNum", pageInfo.getPageNum());
        data.put("pageSize", pageInfo.getPageSize());
        data.put("rows", productService.toSiteCardList(list));
        return AjaxResult.success(data);
    }

    /**
     * 首页明星产品
     */
    @GetMapping("/recommend")
    public AjaxResult recommend(@RequestParam(value = "limit", required = false, defaultValue = "6") int limit)
    {
        return AjaxResult.success(productService.selectRecommendProducts(limit));
    }

    /**
     * 产品详情（含相关推荐；浏览量 +1）
     */
    @GetMapping("/{productId}")
    public AjaxResult detail(@PathVariable Long productId)
    {
        SiteProductVo vo = productService.selectSiteProductDetail(productId, 4);
        if (vo == null)
        {
            return AjaxResult.error("产品不存在或已下线");
        }
        return AjaxResult.success(vo);
    }
}
