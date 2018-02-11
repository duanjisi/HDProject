package com.atgc.hd.comm.socket;

import com.atgc.hd.comm.net.response.base.Response;

/**
 * <p>描述：
 * <p>作者：liangguokui 2018/2/10
 */
public interface OnActionListener<T> {
    /**
     * {
     * "Command": "PAT_SEND_TASK",
     * "Result":0,
     * "ErrorCode":0,
     * "ErrorMessage":"",
     * "Data": [
     *     {
     *         "serialNum":"1354jhjy546hjvj",
     *         "serverResult":"0",
     *         "errMsg":"",
     *         "data": []
     *     }
     * ]
     * }
     */

    /**
     * 参考上面报文，现在的响应流程是：
     * 当C 端（客户端）发送数据到S 端（网关端），S 端会转发到W端（物联网端），
     * 根据S 端发送数据到W 端的情况，S 端会立即发送回执给C 端，C 端通过对【Command、Result、ErrorCode】做对应的
     * 业务处理；
     * W 端收到数据处理完毕后，会将数据结果发送给S 端，此时S 端会将带有数据结果的回执发送给C 端，
     * C 端通过对【Command、Result、ErrorCode】、Data里面的【serialNum、serverResult】再做对应的业务处理
     */

    /**
     * C 端发送数据收到S 端回执，Result == 0 && Data != null 时回调
     *
     * @param cmd
     * @param serialNum
     */
    void onSendSucess(String cmd, String serialNum);

    /**
     * C 端发送数据收到S 端回执，Result != 0 && Data != null时回调
     *
     * @param cmd
     * @param serialNum
     * @param errorCode
     * @param errorMsg
     */
    void onSendFail(String cmd, String serialNum, String errorCode, String errorMsg);

    /**
     * S 端下发回执到C 端，Result == 0 && Data == null && serverResult == 0 时回调
     *
     * @param cmd
     * @param serialNum
     * @param response
     */
    void onResponseSuccess(String cmd, String serialNum, Response response);

    /**
     * S 端下发回执到C 端，Result != 0 || Data == null  || serverResult != 0 时回调
     *
     * @param cmd
     * @param serialNum
     * @param errorCode Result != 0 返回ErrorCode，serverResult != 0 返回serverResult
     * @param errorMsg  Result != 0 返回ErrorMessage，serverResult != 0 errMsg
     */
    void onResponseFaile(String cmd, String serialNum, String errorCode, String errorMsg);
}
