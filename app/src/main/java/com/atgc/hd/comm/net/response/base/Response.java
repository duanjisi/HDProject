package com.atgc.hd.comm.net.response.base;

import com.alibaba.fastjson.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>描述：
 * <p>作者：liangguokui 2018/2/9
 */
public class Response<T> {
    public String Command;
    public int Result;
    public int ErrorCode;
    public String ErrorMessage;
    public JSONArray Data;

    public List<T> dataArray;

    public void parseData(Class<T> target) {
        if (Result == 0 && target != null) {
            dataArray = Data.toJavaList(target);
        } else {
            dataArray = new ArrayList<>();
        }
    }
}
