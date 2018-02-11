package com.hdsocket.net.request;

import com.hdsocket.utils.DigitalUtils;
import com.xuhao.android.libsocket.sdk.bean.ISendable;

import java.util.ArrayList;
import java.util.List;

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
        return DigitalUtils.getBytes(this);
    }
}
