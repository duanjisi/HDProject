/**
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.atgc.hd.comm.net.http;

import android.content.Context;
import android.os.Handler;

import com.atgc.hd.comm.Constants;
import com.atgc.hd.comm.utils.FileUtil;
import com.atgc.hd.comm.utils.StringUtils;
import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * <p>描述：
 * <p>作者：duanjisi 2018年 01月 22日
 */

public class UploadTask implements Runnable {
    private Context context;
    private String filePath;
    private updateProgressCallback callback;
    private Handler handler = new Handler();

    public UploadTask(Context context, String filePath) {
        this.context = context;
        this.filePath = filePath;
    }

    public void setCallback(updateProgressCallback callback) {
        this.callback = callback;
    }

    @Override
    public void run() {
        /**.
         * 上传文件post
         * @param 图片的id
         * @return id
         */
//        PrintWriter out = null;
        DataOutputStream out = null;
        BufferedReader in = null;
        String result = "";
        String param = StringUtils.getJson(filePath);
        final long total = param.getBytes().length;
        String size = FileUtil.getFormatSize(total);

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
//            out = new PrintWriter(conn.getOutputStream());
            out = new DataOutputStream(conn.getOutputStream());
            // 发送请求参数
//            out.print(param);
            InputStream is = new ByteArrayInputStream(param.getBytes());
            byte[] buffer = new byte[1024];
            int count = -1;
            int length = 0;
            while ((count = is.read(buffer)) != -1) {
//                baos.write(buffer, 0, len);
                out.write(buffer, 0, count);
                length += count;
                final int finalLength = length;
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (callback != null) {
//                            callback.onProgressUpdate((int) ((finalLength / (float) total) * 100));
//                        }
//                    }
//                });
                //这里是测试时为了演示进度,休眠500毫秒，正常应去掉
//                Thread.sleep(25);
            }
            // flush输出流的缓冲
            out.flush();

            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
//                publishProgress((int) (((result.getBytes().length) / (float) total) * 100));
            }
            final String finalResult = result;

            for (int i = 0; i < 100; i++) {
                //发布进度
                final int finalI = i;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (callback != null) {
                            callback.onProgressUpdate(finalI);
                        }
                    }
                });
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            handler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject object = new JSONObject(finalResult);
                        if (callback != null) {
                            String str = Constants.IMAGE_HEADER + object.getString("id");
                            callback.onPostExecute(Constants.IMAGE_HEADER + object.getString("id"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
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
    }

    public interface updateProgressCallback {
        void onPostExecute(String string);

        void onProgressUpdate(int progress);
    }
}
