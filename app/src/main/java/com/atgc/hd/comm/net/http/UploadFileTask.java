
package com.atgc.hd.comm.net.http;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.atgc.hd.comm.Constants;
import com.atgc.hd.comm.utils.FileUtil;
import com.atgc.hd.comm.utils.StringUtils;
import com.orhanobut.logger.Logger;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.HttpResponse;

/**
 * <p>描述：上传文件
 * <p>作者：duanjisi 2018年 01月 19日
 */

public class UploadFileTask extends AsyncTask<String, Integer, String> {
    private Context context;
    private String filePath;
    private ProgressDialog pd;
    private long totalSize;

    public UploadFileTask(Context context, String filePath) {
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
        String serverResponse = null;

        HttpClient httpClient = new DefaultHttpClient();
        HttpContext httpContext = new BasicHttpContext();
        HttpPost httpPost = new HttpPost(Constants.UP_LOAD_IMAGE);
        try {
            CustomMultipartEntity multipartContent = new CustomMultipartEntity(
                    new CustomMultipartEntity.ProgressListener() {
                        @Override
                        public void transferred(long num) {
                            publishProgress((int) ((num / (float) totalSize) * 100));
                        }
                    });
            // We use FileBody to transfer an image
            String json = StringUtils.getJson(filePath);
            multipartContent.addPart("", new StringBody(json));
//            multipartContent.addPart("data", new FileBody(new File(
//                    filePath)));
            totalSize = multipartContent.getContentLength();
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Accept", "application/json");
//            httpPost.setHeader("Content-Length", "" + totalSize);
            // Send it
            httpPost.setEntity(multipartContent);
            print(httpPost);
            HttpResponse response = httpClient.execute(httpPost, httpContext);
            serverResponse = EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return serverResponse;
    }

    private void print(HttpPost httpPost) {
        String method = httpPost.getMethod();
        Header[] headers = httpPost.getAllHeaders();
        HttpEntity entity = httpPost.getEntity();
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
}
