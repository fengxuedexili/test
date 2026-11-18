package com.ruoyi.cms.util;

import java.util.regex.Pattern;

import com.ruoyi.common.utils.StringUtils;

/**
 * 富文本服务端清洗（兜底）
 *
 * 说明：当前环境无法离线引入 Jsoup，这里用保守的正则移除高危结构：
 * 1) script / style / iframe / object / embed / form 等标签
 * 2) on* 事件属性
 * 3) javascript: / vbscript: 协议
 * 4) 内联 style 中的 expression()
 *
 * 这不是完整 HTML 解析器，前端仍必须使用 DOMPurify；后续可平滑替换为 Jsoup 白名单实现。
 *
 * @author site
 */
public final class CmsHtmlSanitizer
{
    private CmsHtmlSanitizer()
    {
    }

    private static final Pattern DANGEROUS_BLOCK = Pattern.compile(
            "(?is)<\\s*(script|style|iframe|object|embed|form|input|button|textarea|select|link|meta)\\b[^>]*>.*?<\\s*/\\s*\\1\\s*>");

    private static final Pattern DANGEROUS_SELF_CLOSING = Pattern.compile(
            "(?is)<\\s*(script|style|iframe|object|embed|form|input|button|textarea|select|link|meta)\\b[^>]*/?>");

    private static final Pattern EVENT_ATTRIBUTE = Pattern.compile(
            "(?i)\\s+on[a-z]+\\s*=\\s*(\"[^\"]*\"|'[^']*'|[^\\s>]+)");

    private static final Pattern JS_PROTOCOL = Pattern.compile(
            "(?i)(href|src|action)\\s*=\\s*(\"|')\\s*(javascript|vbscript)\\s*:[^\"']*\\2");

    private static final Pattern CSS_EXPRESSION = Pattern.compile("(?i)expression\\s*\\(");

    /**
     * 清洗富文本 HTML
     */
    public static String clean(String html)
    {
        if (StringUtils.isEmpty(html))
        {
            return html;
        }
        String result = html;
        result = DANGEROUS_BLOCK.matcher(result).replaceAll("");
        result = DANGEROUS_SELF_CLOSING.matcher(result).replaceAll("");
        result = EVENT_ATTRIBUTE.matcher(result).replaceAll("");
        result = JS_PROTOCOL.matcher(result).replaceAll("$1=$2#$2");
        result = CSS_EXPRESSION.matcher(result).replaceAll("blocked(");
        return result;
    }
}
