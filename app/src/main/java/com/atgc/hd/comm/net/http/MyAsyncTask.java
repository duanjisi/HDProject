/**
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.atgc.hd.comm.net.http;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.View;

import com.atgc.hd.R;
import com.atgc.hd.adapter.PictureAdapter;
import com.atgc.hd.comm.Constants;
import com.atgc.hd.comm.utils.FileUtil;
import com.atgc.hd.comm.utils.StringUtils;
import com.atgc.hd.entity.UploadEntity;
import com.bumptech.glide.Glide;
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
import java.util.List;

/**
 * <p>描述：
 * <p>作者：duanjisi 2018年 02月 27日
 */

public class MyAsyncTask extends AsyncTask<UploadEntity, Integer, String> {

    private UploadEntity myObject;     //单个数据，用于完成后的处理
    private List<View> viewList;                //视图对象集合，用于设置样式
    private Integer viewId;                     //视图标识，用于匹配视图对象
    private Context context;
    private View convertView = null;
    private String path;

    public MyAsyncTask(Context context, List<View> viewList, UploadEntity entity) {
        this.context = context;
        this.viewList = viewList;
        this.viewId = entity.id;
        this.path = entity.getLocalPath();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        /* 匹配视图对象 */
        for (int i = 0; i < viewList.size(); i++) {
            if (viewList.get(i).getTag(R.id.grid) == viewId) {
                //检查所有视图ID，如果ID匹配则取出该对象
                convertView = viewList.get(i);
                break;
            }
        }
        if (convertView != null) {
            //将视图对象中缓存的ViewHolder对象取出，并使用该对象对控件进行更新
            PictureAdapter.ViewHolder viewHolder = (PictureAdapter.ViewHolder) convertView.getTag();
            String fileName = FileUtil.getFileName(path);
            if (fileName.contains(".mp4") || fileName.contains(".MP4")) {
                viewHolder.ivPlay.setVisibility(View.VISIBLE);
            }
            Glide.with(context).load(path).
                    placeholder(R.drawable.zf_default_message_image).
                    crossFade().into(viewHolder.ivPic);
            viewHolder.tvProgress.setVisibility(View.VISIBLE);
            viewHolder.ivBg.setVisibility(View.VISIBLE);
            viewHolder.ivClose.setVisibility(View.GONE);
            viewHolder.tvProgress.setText("开始上传");
//            if (!loaded) {
//                if (!isLoading) {
//                    tvProgress.setVisibility(View.VISIBLE);
//                    ivBg.setVisibility(View.VISIBLE);
//                    ivClose.setVisibility(View.GONE);
//                    tvProgress.setText("开始上传");
//                }
//            } else {
//                tvProgress.setVisibility(View.GONE);
//                ivBg.setVisibility(View.GONE);
//                ivClose.setVisibility(View.GONE);
//            }
        }
    }

    @Override
    protected String doInBackground(UploadEntity... params) {
        myObject = params[0];
        DataOutputStream out = null;
        BufferedReader in = null;
        String result = "";
        String param = StringUtils.getJson(myObject.getLocalPath());
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
                publishProgress((int) ((finalLength / (float) total) * 100));
                //这里是测试时为了演示进度,休眠500毫秒，正常应去掉
                Thread.sleep(25);
            }
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
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
    protected void onProgressUpdate(Integer... values) {
        for (int i = 0; i < viewList.size(); i++) {
            if (viewList.get(i).getTag(R.id.grid) == viewId) {
                //检查所有视图ID，如果ID匹配则取出该对象
                convertView = viewList.get(i);
                break;
            }
        }
        if (convertView != null) {
            //将视图对象中缓存的ViewHolder对象取出，并使用该对象对控件进行更新
            PictureAdapter.ViewHolder viewHolder = (PictureAdapter.ViewHolder) convertView.getTag();
//            viewHolder.progressBar.setProgress(values[0]);
            viewHolder.tvProgress.setText("" + values[0]);
        }
        myObject.progress = values[0];
    }

    @Override
    protected void onPostExecute(String result) {
        //更新数据源信息
        if (!TextUtils.isEmpty(result)) {
            try {
                JSONObject object = new JSONObject(result);
                myObject.progress = 100;
                myObject.setUrl(Constants.IMAGE_HEADER + object.getString("id"));
                myObject.setDownloaded(true);

                for (int i = 0; i < viewList.size(); i++) {
                    if (viewList.get(i).getTag(R.id.grid) == viewId) {
                        //检查所有视图ID，如果ID匹配则取出该对象
                        convertView = viewList.get(i);
                        break;
                    }
                }

                if (convertView != null) {
                    //将视图对象中缓存的ViewHolder对象取出，并使用该对象对控件进行更新
                    PictureAdapter.ViewHolder viewHolder = (PictureAdapter.ViewHolder) convertView.getTag();
//            viewHolder.button.setText("删除");
                    viewHolder.tvProgress.setVisibility(View.GONE);
                    viewHolder.ivBg.setVisibility(View.GONE);
                    viewHolder.ivClose.setVisibility(View.VISIBLE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
