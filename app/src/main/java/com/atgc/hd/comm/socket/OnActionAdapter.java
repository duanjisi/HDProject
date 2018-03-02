package com.atgc.hd.comm.socket;

import android.os.Bundle;

import com.atgc.hd.comm.net.response.base.Response;

/**
 * <p>描述：
 * <p>作者：liangguokui 2018/2/10
 */
public class OnActionAdapter implements OnActionListener {
    /**
     * 注释参考{@link OnActionListener#onSendSucess(String, String, Bundle)}  }
     *
     * @param cmd
     * @param serialNum
     */
    @Override
    public void onSendSucess(String cmd, String serialNum, Bundle bundle) {
    }

    /**
     * 注释参考{@link OnActionListener#onSendFail(String, String, String, String, Bundle)}  }
     *
     * @param cmd
     * @param serialNum
     * @param errorCode
     * @param errorMsg
     */
    @Override
    public void onSendFail(String cmd, String serialNum, String errorCode, String errorMsg, Bundle bundle) {
    }

    /**
     * 注释参考{@link OnActionListener#onResponseSuccess(String, String, Response, Bundle)}  }
     *
     * @param cmd
     * @param serialNum
     * @param response
     */
    @Override
    public void onResponseSuccess(String cmd, String serialNum, Response response, Bundle bundle) {

    }

    /**
     * 注释参考{@link OnActionListener#onResponseFaile(String, String, String, String, Bundle)}  }
     *
     * @param cmd
     * @param serialNum
     * @param errorCode Result != 0 返回ErrorCode，serverResult != 0 返回serverResult
     * @param errorMsg  Result != 0 返回ErrorMessage，serverResult != 0 errMsg
     */
    @Override
    public void onResponseFaile(String cmd, String serialNum, String errorCode, String errorMsg, Bundle bundle) {
    }
}
