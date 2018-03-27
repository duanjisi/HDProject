package com.atgc.hd.comm.net.request.base;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.atgc.hd.comm.Constants;
import com.atgc.hd.comm.config.DeviceParams;
import com.atgc.hd.comm.utils.AESUtils;
import com.atgc.hd.comm.utils.CRCUtil;
import com.atgc.hd.comm.utils.DigitalUtils;
import com.atgc.hd.entity.EventMessage;
import com.orhanobut.logger.Logger;
import com.xuhao.android.libsocket.sdk.bean.ISendable;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

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
        String temp = JSON.toJSONString(this);
        byte[] bodyBytes = getBodyData(temp);

        byte[] crcCode = CRCUtil.getParamCRC(bodyBytes);

        HeaderRequest headerRequest = new HeaderRequest();
        headerRequest.setContentLength(bodyBytes.length);
        headerRequest.setCrc(crcCode);
        byte[] headerBytes = headerRequest.toBytes();

        byte[] restule = new byte[headerBytes.length + bodyBytes.length];
        System.arraycopy(headerBytes, 0, restule, 0, headerBytes.length);
        System.arraycopy(bodyBytes, 0, restule, headerBytes.length, bodyBytes.length);

        String headerHexStr = DigitalUtils.toHexString(headerBytes);
        Logger.e("请求报文头：\n" + headerHexStr + "\n请求报文体：\n" + temp);

        EventMessage msg = new EventMessage("socket_log", Command + "##请求##" + temp);
        EventBus.getDefault().post(msg);
        return restule;
    }

    private byte[] getBodyData(String srcData) {
        if (!Constants.isEntry) {
            return srcData.getBytes();
        }
        try {
            byte[] tempBytes = AESUtils.encrypt(srcData, DeviceParams.getInstance().getAESkey());
            return tempBytes;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
