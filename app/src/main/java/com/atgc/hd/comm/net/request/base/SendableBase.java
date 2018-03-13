package com.atgc.hd.comm.net.request.base;

import com.alibaba.fastjson.JSON;
import com.atgc.hd.comm.utils.CRCUtil;
import com.atgc.hd.comm.utils.DigitalUtils;
import com.atgc.hd.entity.EventMessage;
import com.orhanobut.logger.Logger;
import com.xuhao.android.libsocket.sdk.bean.ISendable;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * <p>描述：
 * <p>作者：liangguokui 2018/2/7
 */
public class SendableBase implements ISendable {

    public String Command = "";
    public List<BaseRequest> Data = new ArrayList<>();

    public SendableBase(BaseRequest data) {
        Command = data.getRequestCommand();
        Data.add(data);
    }

    public SendableBase(List<BaseRequest> data) {
        if (data == null && data.isEmpty()) {
            return;
        } else {
            Command = data.get(0).getRequestCommand();
            Data.addAll(data);
        }
    }

    @Override
    public byte[] parse() {
        String bodyData = JSON.toJSONString(this);

        byte[] bodyBytes = bodyData.getBytes();
        byte[] crcCode = CRCUtil.getParamCRC(bodyBytes);

        HeaderRequest headerRequest = new HeaderRequest();
        headerRequest.setContentLength(bodyBytes.length);
        headerRequest.setCrc(crcCode);
        byte[] headerBytes = headerRequest.toBytes();

        byte[] restule = new byte[headerBytes.length + bodyBytes.length];
        System.arraycopy(headerBytes, 0, restule, 0, headerBytes.length);
        System.arraycopy(bodyBytes, 0, restule, headerBytes.length, bodyBytes.length);

        String headerHexStr = DigitalUtils.toHexString(headerBytes);
        Logger.e("请求报文头：\n" + headerHexStr + "\n请求报文体：\n" + bodyData);

        EventMessage msg = new EventMessage("socket_log", Command + "##请求##" + bodyData);
        EventBus.getDefault().post(msg);
        return restule;
    }
}
