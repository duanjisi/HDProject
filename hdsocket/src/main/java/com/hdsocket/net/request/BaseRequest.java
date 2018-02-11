package com.hdsocket.net.request;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * <p>描述：
 * <p>作者：liangguokui 2018/2/9
 */
public abstract class BaseRequest {
    @JSONField(name = "requestTag")
    public String serialNum;

    @JSONField(serialize = false)
    public abstract String getRequestCommand();

    @JSONField(serialize = false)
    public abstract String getResponseCommand();

    @JSONField(serialize = false)
    public abstract Class<?> getResponseClass();
}