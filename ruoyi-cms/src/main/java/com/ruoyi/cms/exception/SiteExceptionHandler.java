package com.ruoyi.cms.exception;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ruoyi.common.core.domain.AjaxResult;

/**
 * 前台业务异常处理：把 SiteBizException 的 code 原样返回
 *
 * 注意：必须优先于若依的 GlobalExceptionHandler（它兜底 RuntimeException），否则会被转成 500。
 *
 * @author site
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class SiteExceptionHandler
{
    @ExceptionHandler(SiteBizException.class)
    public AjaxResult handleSiteBizException(SiteBizException e)
    {
        return AjaxResult.error(e.getCode(), e.getMessage());
    }
}
