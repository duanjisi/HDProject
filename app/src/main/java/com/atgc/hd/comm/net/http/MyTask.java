/**
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.atgc.hd.comm.net.http;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.atgc.hd.comm.Constants;
import com.atgc.hd.comm.utils.StringUtils;
import com.orhanobut.logger.Logger;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * <p>描述：
 * <p>作者：duanjisi 2018年 01月 22日
 */

public class MyTask extends AsyncTask<String, Integer, String> {
    private Context context;
    private String filePath;
    private ProgressDialog pd;
    private long totalSize;

    public MyTask(Context context, String filePath) {
        this.context = context;
        this.filePath = filePath;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pd = new ProgressDialog(context);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setMessage("Uploading File...");
        pd.setCancelable(false);
        pd.show();
    }


    @Override
    protected String doInBackground(String... strings) {
        /**.
         * 上传文件post
         * @param 图片的id
         * @return id
         */
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        String param = StringUtils.getJson(filePath);
        long total = param.getBytes().length;
        try {
            URL realUrl = new URL(Constants.UP_LOAD_IMAGE);
            // 打开和URL之间的连接
            HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
                publishProgress((int) (((result.getBytes().length) / (float) total) * 100));
            }
        } catch (Exception e) {
            Logger.e("发送 POST 请求出现异常！");
        }
        //使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                Logger.e("关闭输入输出流异常！");
            }
        }
        return result;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        pd.setProgress((int) (progress[0]));
    }

    @Override
    protected void onPostExecute(String result) {
        Logger.i("===========result:" + result);
        pd.dismiss();
    }

    @Override
    protected void onCancelled() {
        System.out.println("cancle");
    }

    private void sendTxt(String json) {
        InputStream is = new ByteArrayInputStream(json.getBytes());
    }
}
