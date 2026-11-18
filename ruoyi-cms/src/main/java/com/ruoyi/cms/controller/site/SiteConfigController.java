package com.ruoyi.cms.controller.site;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.service.ISysConfigService;

/**
 * 官网前台 - 站点配置
 *
 * 数据来源：sys_config（键前缀 site.），运营可在后台「系统管理 → 参数设置」维护。
 *
 * @author site
 */
@Anonymous
@RestController
@RequestMapping("/api/site")
public class SiteConfigController
{
    @Autowired
    private ISysConfigService configService;

    /**
     * 站点基础信息（匿名只读）
     */
    @GetMapping("/config")
    public AjaxResult config()
    {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("name", get("site.name", "site"));
        data.put("shortName", get("site.shortName", "site"));
        data.put("logo", get("site.logo", ""));
        data.put("phone", get("site.phone", ""));
        data.put("email", get("site.email", ""));
        data.put("icp", get("site.icp", ""));
        data.put("copyright", get("site.copyright", ""));
        data.put("keywords", get("site.keywords", ""));
        data.put("description", get("site.description", ""));
        return AjaxResult.success(data);
    }

    private String get(String key, String defaultValue)
    {
        String value = configService.selectConfigByKey(key);
        return StringUtils.isEmpty(value) ? defaultValue : value;
    }
}
