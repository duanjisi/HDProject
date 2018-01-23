/**
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.atgc.hd.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.atgc.hd.R;
import com.atgc.hd.TestActivity;
import com.atgc.hd.adapter.PictureAdapter;
import com.atgc.hd.adapter.UpdateCallback;
import com.atgc.hd.base.BaseActivity;
import com.atgc.hd.comm.Utils;
import com.atgc.hd.comm.net.BaseDataRequest;
import com.atgc.hd.comm.net.http.MyTask;
import com.atgc.hd.comm.net.http.UploadFileTask;
import com.atgc.hd.comm.net.request.UploadEventRequest;
import com.atgc.hd.comm.utils.DateUtil;
import com.atgc.hd.comm.utils.FileUtil;
import com.atgc.hd.comm.utils.PermissonUtil.PermissionListener;
import com.atgc.hd.comm.utils.PermissonUtil.PermissionUtil;
import com.atgc.hd.comm.utils.PhotoAlbumUtil.MultiImageSelector;
import com.atgc.hd.comm.utils.PhotoAlbumUtil.MultiImageSelectorActivity;
import com.atgc.hd.comm.utils.PreferenceUtils;
import com.atgc.hd.comm.utils.UIUtils;
import com.atgc.hd.entity.UploadEntity;
import com.atgc.hd.widget.ActionSheet;
import com.atgc.hd.widget.MyGridView;
import com.atgc.hd.widget.MyView;
import com.atgc.hd.widget.RoundImageView;
import com.bumptech.glide.Glide;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * <p>描述：应急事件
 * <p>作者：duanjisi 2018年 01月 18日
 */

public class EmergencyEventActivity extends BaseActivity implements
        ActionSheet.OnSheetItemClickListener {
    private final String TAG = EmergencyEventActivity.class.getSimpleName();
    private ActionSheet actionSheet;
    private int cameraType;//1:相机 2：相册
    private Uri imageUri;
    private String imageName;
    private final int OPEN_CAMERA = 1;//相机
    private final int OPEN_ALBUM = 2;//相册
    private final int REQUEST_CLIP_IMAGE = 3;//裁剪
    private final int RECORD_VIDEO = 4;//录制视频
    private final int LOCAL_VIDEO = 5;//本地视频
    private String savePath;
    private String mOutputPath;
    @BindView(R.id.tv_write)
    EditText tvWrite;
    @BindView(R.id.iv)
    ImageView iv;
    @BindView(R.id.btn_submit)
    Button btnSubmit;
    @BindView(R.id.grid)
    MyGridView gridView;
    @BindView(R.id.ll_image)
    LinearLayout ll_image;
    private Handler handler = new Handler();
    private PermissionListener permissionListener;
    //    private PictureAdapter pictureAdapter;
    private int mImageHeight;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT == 19 || Build.VERSION.SDK_INT == 20) {
            savePath = getFilesDir().getPath() + "/";
        } else {
            savePath = Environment.getExternalStorageDirectory() + "/HDProject/";
        }
//        mOutputPath = new File(getExternalCacheDir(), "chosen.jpg").getPath();
        barHelper.setTitleColor(getResources().getColor(R.color.white));
        mImageHeight = (UIUtils.getScreenWidth(context) - UIUtils.dip2px(context, 60)) / 4;
//        pictureAdapter = new PictureAdapter(context);
//        gridView.setAdapter(pictureAdapter);
//        gridView.setOnItemClickListener(new itemClickListener());
//        pictureAdapter.initData();

        addView("lastItem");
//        adapter = new MyAdapter();
//        gridView.setAdapter(adapter);
    }

    @Override
    public String toolBarTitle() {
        return getString(R.string.emergence_event);
    }


    @OnClick({R.id.iv, R.id.btn_submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv:
//                showActionSheet();
                break;
            case R.id.btn_submit:
                submit();
                break;
        }
    }


    private class itemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            UploadEntity entity = (UploadEntity) adapterView.getItemAtPosition(i);
            if (entity.getLocalPath().equals("lastItem")) {
                showActionSheet();
            }
        }
    }

    private void showActionSheet() {
        actionSheet = new ActionSheet(EmergencyEventActivity.this)
                .builder()
                .setCancelable(false)
                .setCanceledOnTouchOutside(true);

        actionSheet.addSheetItem("从手机相册选择", ActionSheet.SheetItemColor.Black,
                EmergencyEventActivity.this)
                .addSheetItem("拍照片", ActionSheet.SheetItemColor.Black,
                        EmergencyEventActivity.this)
                .addSheetItem("本地视频", ActionSheet.SheetItemColor.Black,
                        EmergencyEventActivity.this)
                .addSheetItem("拍视频", ActionSheet.SheetItemColor.Black,
                        EmergencyEventActivity.this);
        actionSheet.show();
    }

    @Override
    public void onClick(int which) {
        switch (which) {
            case 1:
                cameraType = OPEN_ALBUM;
                getPermission();
                break;
            case 2:
                cameraType = OPEN_CAMERA;
                getPermission();
                break;
            case 3:
                cameraType = LOCAL_VIDEO;
                openActvityForResult(LocalVideoFilesActivity.class, LOCAL_VIDEO);
                break;
            case 4:
                cameraType = RECORD_VIDEO;
                getPermission();
                break;
        }
    }

    private void submit() {
//        String urls = getImageUrls();
        String description = getText(tvWrite);
        HashMap<String, String> map = new HashMap<>();
//        map.put("userID", "");
        map.put("deviceID", "10012017020000000000");
        map.put("longitude", "108.6");
        map.put("latitude", "110");
        map.put("uploadTime", DateUtil.currentTime());
        map.put("description", "着火啦~!");
        map.put("picUrl", "http://192.168.0.246:8888/group1/M00/0A/1A/wKgA9lpnAL-AWoinAAC7fMwiVhY271.jpg");
        map.put("videoUrl", "");
        map.put("place", "恒大山水城");
        map.put("eventType", "");
//        map.put("eventStatus", "");
        UploadEventRequest request = new UploadEventRequest(TAG, map);
        request.send(new BaseDataRequest.RequestCallback<String>() {
            @Override
            public void onSuccess(String json) {
                Logger.i(json);
                showToast(json);
            }

            @Override
            public void onFailure(String msg) {
                showToast(msg);
            }
        });
    }

    private void getPermission() {
        permissionListener = new PermissionListener() {
            @Override
            public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
                PermissionUtil.onRequestPermissionsResult(this, requestCode, permissions, permissionListener);
            }

            @Override
            public void onRequestPermissionSuccess() {
                if (cameraType == OPEN_CAMERA) {
                    if (Build.VERSION.SDK_INT < 24) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        imageName = getNowTime() + ".jpg";
                        // 指定调用相机拍照后照片的储存路径
                        File dir;
                        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                            dir = Environment.getExternalStorageDirectory();
                        } else {
                            dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                        }
                        File file = new File(dir, imageName);
                        imageUri = Uri.fromFile(file);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        if (intent.resolveActivity(getPackageManager()) != null) {
                            startActivityForResult(intent, OPEN_CAMERA);
                        }
                    } else {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        String imageName = getNowTime() + ".jpg";
                        File file = new File(savePath, imageName);
                        imageUri = FileProvider.getUriForFile(EmergencyEventActivity.this, "com.atgc.cotton.fileProvider", file);//这里进行替换uri的获得方式
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//这里加入flag
                        startActivityForResult(intent, OPEN_CAMERA);
                    }
                } else if (cameraType == OPEN_ALBUM) {
                    MultiImageSelector.create(context).
                            showCamera(false).
                            count(1)
                            .multi() // 多选模式, 默认模式;
                            .start(EmergencyEventActivity.this, OPEN_ALBUM);
                } else if (cameraType == RECORD_VIDEO) {
                    Intent mIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    //mIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                    mIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
                    mIntent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 20 * 1024 * 1024L);
                    startActivityForResult(mIntent, RECORD_VIDEO);
                }
            }

            @Override
            public void onRequestPermissionError() {
                showToast(getString(R.string.giving_camera_permissions));
            }
        };
        PermissionUtil
                .with(this)
                .permissions(
                        PermissionUtil.PERMISSIONS_GROUP_CAMERA //相机权限
                ).request(permissionListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionUtil.onRequestPermissionsResult(this, requestCode, permissions, permissionListener);
    }

    @SuppressLint("SimpleDateFormat")
    private String getNowTime() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMddHHmmssSS");
        return dateFormat.format(date);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String fileName = System.currentTimeMillis() + ".jpg";
        mOutputPath = new File(getExternalCacheDir(), fileName).getPath();
        if (requestCode == OPEN_CAMERA && resultCode == RESULT_OK) {    //打开相机
            if (imageUri != null) {
                String path = null;
                if (Build.VERSION.SDK_INT < 24) {
                    path = Utils.getPath(this, imageUri);
                } else {
                    path = imageUri.toString();
                }
                if (Build.VERSION.SDK_INT >= 24 && path.contains("com.atgc.cotton.fileProvider") &&
                        path.contains("/IMProject/")) {
                    String[] arr = path.split("/IMProject/");
                    if (arr != null && arr.length > 1) {
                        path = savePath + arr[1];
                    }
                }
                if (!TextUtils.isEmpty(path)) {
                    ClipImageActivity.prepare()
                            .aspectX(2).aspectY(2)//裁剪框横向及纵向上的比例
                            .inputPath(path).outputPath(mOutputPath)//要裁剪的图片地址及裁剪后保存的地址
                            .startForResult(this, REQUEST_CLIP_IMAGE);
                }
            }
        } else if (requestCode == OPEN_ALBUM && resultCode == RESULT_OK) { //打开相册
            if (data == null) {
                return;
            } else {
                ArrayList<String> selectPicList = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                if (selectPicList != null && selectPicList.size() > 0) {
                    String path = selectPicList.get(0);
                    if (!TextUtils.isEmpty(path)) {
                        ClipImageActivity.prepare()
                                .aspectX(2).aspectY(2)//裁剪框横向及纵向上的比例
                                .inputPath(path).outputPath(mOutputPath)//要裁剪的图片地址及裁剪后保存的地址
                                .startForResult(this, REQUEST_CLIP_IMAGE);
                    }
                }
            }
        } else if (resultCode == RESULT_OK && requestCode == REQUEST_CLIP_IMAGE) {
            String path = ClipImageActivity.ClipOptions.createFromBundle(data).getOutputPath();
            if (path != null) {
                uploadImageFile(path);
            }
            return;
        } else if (requestCode == RECORD_VIDEO && resultCode == RESULT_OK) {
            // 录制视频完成
            try {
                AssetFileDescriptor videoAsset = getContentResolver()
                        .openAssetFileDescriptor(data.getData(), "r");
                FileInputStream fis = videoAsset.createInputStream();
                File tmpFile = new File(
                        Environment.getExternalStorageDirectory(),
                        "recordvideo.mp4");
                FileOutputStream fos = new FileOutputStream(tmpFile);

                byte[] buf = new byte[1024];
                int len;
                while ((len = fis.read(buf)) > 0) {
                    fos.write(buf, 0, len);
                }
                fis.close();
                fos.close();
                // 文件写完之后删除/sdcard/dcim/CAMERA/XXX.MP4
                FileUtil.deleteDefaultFile(context, data.getData());
                String videoPath = tmpFile.getAbsolutePath();
                uploadVideoFile(videoPath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == LOCAL_VIDEO && resultCode == RESULT_OK) {
            if (data != null) {
                String videoPath = data.getStringExtra("filePath");
                uploadVideoFile(videoPath);
            }
        }
    }

    private void getPermission(String[] permissions) {
        permissionListener = new PermissionListener() {
            @Override
            public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
                PermissionUtil.onRequestPermissionsResult(this, requestCode, permissions, permissionListener);
            }

            @Override
            public void onRequestPermissionSuccess() {
                MultiImageSelector.create(context).
                        showCamera(true).
                        count(1)
                        .start(EmergencyEventActivity.this, 100);
            }

            @Override
            public void onRequestPermissionError() {
                showToast(getString(R.string.giving_album_permissions), true);
            }
        };
        PermissionUtil
                .with(this)
                .permissions(
                        permissions//相机权限
                ).request(permissionListener);
    }

    private void uploadImageFile(final String path) {
//        UploadEntity entity = new UploadEntity();
//        entity.setLocalPath(path);
//        pictureAdapter.addItem2(entity);
        addView(path);
    }

    private void uploadVideoFile(final String path) {
        addView(path);
    }


    private ArrayList<String> images = new ArrayList<>();

    private void addView(String path) {
        if (!images.contains(path)) {
            images.add(path);
        }
        int count = ll_image.getChildCount();
        if (count == 4) {
            deleteLastItem();
        }
        if (count >= 1) {
            ll_image.addView(insertImage(path, 0), count - 1);
        } else {
            ll_image.addView(insertImage(path, 0), 0);
        }
        ll_image.requestLayout();
    }

    private void deleteLastItem() {
        if (images.contains("lastItem")) {
            images.remove("lastItem");
        }
        ll_image.removeViewAt(3);
    }

    private void addLastItem() {
        if (!images.contains("lastItem")) {
            ll_image.addView(insertImage("lastItem", 0), images.size());
            images.add("lastItem");
        }
    }


    private StringBuilder sb = new StringBuilder();

    private String getImageUrls() {
        return sb.toString();
    }

    private View insertImage(String localPath, int position) {
        final LinearLayout layout = new LinearLayout(getApplicationContext());
        layout.setLayoutParams(new LinearLayout.LayoutParams(mImageHeight, mImageHeight));
        layout.setGravity(Gravity.CENTER);
        layout.setTag(localPath);
        MyView child = new MyView(context);
        child.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        child.getLayoutParams().width = mImageHeight;
        child.getLayoutParams().height = mImageHeight;
        if (!localPath.equals("lastItem")) {
            child.setCallback(new MyView.callback() {
                @Override
                public void getFileUrl(String url) {
                    sb.append(url);
                    sb.append(",");
                }
            });
            child.setCloseListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteView((String) layout.getTag());
                }
            });
            child.startUpload(localPath);
        } else {
            child.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showActionSheet();
                }
            });
        }
        layout.addView(child);
        return layout;
    }

    private void deleteView(String path) {
        if (images.contains(path)) {
            images.remove(path);
        }
        int count = ll_image.getChildCount();
        if (count != 0) {
            for (int i = 0; i < count; i++) {
                View view = ll_image.getChildAt(i);
                String tag = (String) view.getTag();
                if (tag.equals(path)) {
                    ll_image.removeView(view);
                    ll_image.requestLayout();
                    addLastItem();
                    break;
                }
            }
        }
    }

    private MyAdapter adapter;
    private ArrayList<String> mList = new ArrayList<>();
    private HashMap<String, UploadEntity> map = new HashMap<>();
    private ViewHolder viewHolder = null;

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            //我们将map作为数据源
            if (map == null) {
                return 0;
            } else {
                return map.size();
            }
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            viewHolder = null;
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.item_picture, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            if (map != null && map.size() > 0) {
                //   mList.get(position)得到map的key          viewHolder.tv.setText(map.get(mList.get(position)).getCurrent() + "");
                viewHolder.tvProgress.setText(map.get(mList.get(position)).getCurrent() + "%" + "\n" + "正在上传");
            }
            return convertView;
        }
    }

    public static class ViewHolder {
        public ImageView ivClose;
        public RoundImageView ivPic;
        public RoundImageView ivBg;
        public TextView tvProgress;

        public ViewHolder(View view) {
            this.ivClose = (ImageView) view.findViewById(R.id.iv_close);
            this.ivPic = (RoundImageView) view.findViewById(R.id.iv_image);
            this.ivBg = (RoundImageView) view.findViewById(R.id.iv_bg);
            this.tvProgress = view.findViewById(R.id.tv_progress);
        }
    }
}
