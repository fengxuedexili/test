package com.ruoyi.cms.controller.site;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.cms.domain.CmsCategory;
import com.ruoyi.cms.domain.vo.SiteCategoryVo;
import com.ruoyi.cms.service.ICmsCategoryService;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.domain.AjaxResult;

/**
 * 前台 - 栏目
 *
 * @author site
 */
@Anonymous
@RestController
@RequestMapping("/api/site/category")
public class SiteCategoryController
{
    @Autowired
    private ICmsCategoryService categoryService;

    /**
     * 栏目树（默认产品栏目 type=1）
     */
    @GetMapping("/tree")
    public AjaxResult tree(@RequestParam(value = "type", required = false, defaultValue = "1") String type)
    {
        return AjaxResult.success(toVoList(categoryService.selectSiteCategoryTree(type)));
    }

    private List<SiteCategoryVo> toVoList(List<CmsCategory> categories)
    {
        List<SiteCategoryVo> list = new ArrayList<>();
        if (categories == null)
        {
            return list;
        }
        for (CmsCategory category : categories)
        {
            SiteCategoryVo vo = new SiteCategoryVo(category);
            vo.setChildren(toVoList(category.getChildren()));
            list.add(vo);
        }
        return list;
    }
}
