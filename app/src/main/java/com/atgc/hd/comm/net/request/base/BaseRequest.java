package com.atgc.hd.comm.net.request.base;

import com.alibaba.fastjson.annotation.JSONField;
import com.atgc.hd.comm.net.response.base.BaseResponse;

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

    @JSONField(serialize = false)
    public Class<?> responseClass() {
        Class<?> target = getResponseClass();
        if (target == null) {
            return BaseResponse.class;
        } else {
            return target;
        }
    }
}