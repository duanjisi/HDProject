package com.hdsocket.net.request;

import com.hdsocket.utils.DigitalUtils;
import com.xuhao.android.libsocket.sdk.bean.IPulseSendable;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>描述：只用于心跳包
 * <p>作者：liangguokui 2018/2/7
 */
public class SendablePulse implements IPulseSendable {

    public String Command = "";
    public List<BaseRequest> Data = new ArrayList<>();

    public SendablePulse(BaseRequest pulseEntity) {
        Command = pulseEntity.getRequestCommand();
        Data.add(pulseEntity);
    }

    @Override
    public byte[] parse() {
        return DigitalUtils.getBytes(this);
    }

}
