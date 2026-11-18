package com.ruoyi.cms.exception;

/**
 * 前台业务异常（携带业务码，供 /api/site/** 返回 4001/4002/4003 等）
 *
 * @author site
 */
public class SiteBizException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    private final int code;

    public SiteBizException(int code, String message)
    {
        super(message);
        this.code = code;
    }

    public int getCode()
    {
        return code;
    }
}
