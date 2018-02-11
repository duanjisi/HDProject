package com.atgc.hd.comm.socket;

import com.hdsocket.net.response.Response;

/**
 * <p>描述：
 * <p>作者：liangguokui 2018/2/10
 */
public class OnActionAdapter implements OnActionListener{
    /**
     * 注释参考{@link OnActionListener#onSendSucess(String, String)}  }
     * @param cmd
     * @param serialNum
     */
    @Override
    public void onSendSucess(String cmd, String serialNum) {
    }

    /**
     * 注释参考{@link OnActionListener#onSendFail(String, String, String, String)}  }
     * @param cmd
     * @param serialNum
     * @param errorCode
     * @param errorMsg
     */
    @Override
    public void onSendFail(String cmd, String serialNum, String errorCode, String errorMsg) {
    }

    /**
     * 注释参考{@link OnActionListener#onResponseSuccess(String, String, Response)}  }
     * @param cmd
     * @param serialNum
     * @param response
     */
    @Override
    public void onResponseSuccess(String cmd, String serialNum, Response response) {

    }

    /**
     * 注释参考{@link OnActionListener#onResponseFaile(String, String, String, String)}  }
     * @param cmd
     * @param serialNum
     * @param errorCode Result != 0 返回ErrorCode，serverResult != 0 返回serverResult
     * @param errorMsg Result != 0 返回ErrorMessage，serverResult != 0 errMsg
     */
    @Override
    public void onResponseFaile(String cmd, String serialNum, String errorCode, String errorMsg) {
    }
}
