package com.atgc.hd.comm.net.response.base;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 * <p>描述：
 * <p>作者：liangguokui 2018/2/10
 */
public class BaseResponse<T> {

    @JSONField(name = "requestTag")
    public String serialNum;

    public String serverResult;

    public String errMsg;

    public List<T> data;

    /**
     * 判断返回的是回执还是网关下发的数据
     * @return
     */
    public boolean isReceipt() {
        boolean receipt = serverResult == null && errMsg == null && data == null;
        return receipt;
    }
}
