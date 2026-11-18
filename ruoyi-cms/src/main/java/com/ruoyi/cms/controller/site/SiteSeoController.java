package com.ruoyi.cms.controller.site;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.cms.domain.CmsArticle;
import com.ruoyi.cms.domain.CmsCategory;
import com.ruoyi.cms.domain.CmsPage;
import com.ruoyi.cms.domain.CmsProduct;
import com.ruoyi.cms.service.ICmsArticleService;
import com.ruoyi.cms.service.ICmsCategoryService;
import com.ruoyi.cms.service.ICmsPageService;
import com.ruoyi.cms.service.ICmsProductService;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.service.ISysConfigService;

/**
 * 前台 - SEO（sitemap / robots）
 *
 * 站点绝对地址优先取 sys_config 的 site.baseUrl，未配置时按请求推导。
 *
 * @author site
 */
@Anonymous
@RestController
@RequestMapping("/api/site")
public class SiteSeoController
{
    @Autowired
    private ICmsCategoryService categoryService;

    @Autowired
    private ICmsProductService productService;

    @Autowired
    private ICmsArticleService articleService;

    @Autowired
    private ICmsPageService pageService;

    @Autowired
    private ISysConfigService configService;

    @GetMapping(value = "/sitemap.xml", produces = "application/xml;charset=UTF-8")
    public String sitemap(HttpServletRequest request)
    {
        String base = baseUrl(request);
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n");

        addUrl(sb, base + "/", "daily", "1.0");
        addUrl(sb, base + "/product", "weekly", "0.9");
        addUrl(sb, base + "/news", "daily", "0.8");
        addUrl(sb, base + "/store", "weekly", "0.7");

        CmsCategory categoryQuery = new CmsCategory();
        categoryQuery.setStatus("0");
        for (CmsCategory category : categoryService.selectCategoryList(categoryQuery))
        {
            addUrl(sb, base + "/product?categoryId=" + category.getCategoryId(), "weekly", "0.6");
        }

        for (CmsProduct product : productService.selectSiteProductList(new CmsProduct()))
        {
            addUrl(sb, base + "/product/d/" + product.getProductId(), "weekly", "0.8");
        }

        for (CmsArticle article : articleService.selectSiteArticleList(new CmsArticle()))
        {
            addUrl(sb, base + "/news/" + article.getArticleId(), "monthly", "0.6");
        }

        CmsPage pageQuery = new CmsPage();
        pageQuery.setStatus("0");
        for (CmsPage page : pageService.selectPageList(pageQuery))
        {
            addUrl(sb, base + "/p/" + page.getSlug(), "monthly", "0.4");
        }

        sb.append("</urlset>");
        return sb.toString();
    }

    @GetMapping(value = "/robots.txt", produces = "text/plain;charset=UTF-8")
    public String robots(HttpServletRequest request)
    {
        String base = baseUrl(request);
        return "User-agent: *\n"
                + "Disallow: /admin/\n"
                + "Disallow: /api/\n"
                + "Allow: /\n"
                + "\n"
                + "Sitemap: " + base + "/api/site/sitemap.xml\n";
    }

    private String baseUrl(HttpServletRequest request)
    {
        String base = configService.selectConfigByKey("site.baseUrl");
        if (StringUtils.isEmpty(base))
        {
            String scheme = request.getScheme();
            String host = request.getServerName();
            int port = request.getServerPort();
            boolean defaultPort = ("http".equals(scheme) && port == 80) || ("https".equals(scheme) && port == 443);
            base = scheme + "://" + host + (defaultPort ? "" : ":" + port);
        }
        return base.endsWith("/") ? base.substring(0, base.length() - 1) : base;
    }

    private void addUrl(StringBuilder sb, String loc, String changefreq, String priority)
    {
        sb.append("  <url><loc>").append(escape(loc)).append("</loc>")
                .append("<changefreq>").append(changefreq).append("</changefreq>")
                .append("<priority>").append(priority).append("</priority></url>\n");
    }

    private String escape(String value)
    {
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}
