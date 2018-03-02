/**
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.atgc.hd.client.emergency;

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
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.atgc.hd.R;
import com.atgc.hd.adapter.PictureAdapter;
import com.atgc.hd.base.BaseActivity;
import com.atgc.hd.comm.Constants;
import com.atgc.hd.comm.DeviceCmd;
import com.atgc.hd.comm.Utils;
import com.atgc.hd.comm.config.DeviceParams;
import com.atgc.hd.comm.net.request.UploadEventRequest;
import com.atgc.hd.comm.socket.OnActionAdapter;
import com.atgc.hd.comm.socket.SocketManager;
import com.atgc.hd.comm.utils.DateUtil;
import com.atgc.hd.comm.utils.FileUtil;
import com.atgc.hd.comm.utils.PermissonUtil.PermissionListener;
import com.atgc.hd.comm.utils.PermissonUtil.PermissionUtil;
import com.atgc.hd.comm.utils.PhotoAlbumUtil.MultiImageSelector;
import com.atgc.hd.comm.utils.PhotoAlbumUtil.MultiImageSelectorActivity;
import com.atgc.hd.comm.utils.StringUtils;
import com.atgc.hd.comm.utils.UIUtils;
import com.atgc.hd.db.dao.EventDao;
import com.atgc.hd.entity.ActionEntity;
import com.atgc.hd.entity.EventEntity;
import com.atgc.hd.entity.UploadEntity;
import com.atgc.hd.widget.ActionSheet;
import com.atgc.hd.widget.MyGridView;
import com.atgc.hd.widget.MyView;
import com.atgc.hd.widget.RoundImageView;

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
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;

/**
 * <p>描述：应急事件
 * <p>作者：duanjisi 2018年 01月 18日
 */

public class EmergencyEventActivity2 extends BaseActivity implements
        ActionSheet.OnSheetItemClickListener {
    private final String TAG = EmergencyEventActivity2.class.getSimpleName();
    private ActionSheet actionSheet;
    private int cameraType;//1:相机 2：相册
    private Uri imageUri;
    private String imageName;
    private final int URLS_IMAGES = 1;
    private final int URLS_VIDEOS = 2;
    private final int URLS_ALL = 3;
    private final int OPEN_CAMERA = 1;//相机
    private final int OPEN_ALBUM = 2;//相册
    private final int REQUEST_CLIP_IMAGE = 3;//裁剪
    private final int RECORD_VIDEO = 4;//录制视频
    private final int LOCAL_VIDEO = 5;//本地视频
    private String savePath;
    private String mOutputPath;
    @BindView(R.id.tv_write)
    EditText tvWrite;
    @BindView(R.id.tv_place)
    EditText tvPlace;
    @BindView(R.id.iv)
    ImageView iv;
    @BindView(R.id.btn_submit)
    Button btnSubmit;
    @BindView(R.id.grid)
    MyGridView gridView;
    private PictureAdapter pictureAdapter;
    private Handler handler = new Handler();
    private PermissionListener permissionListener;
    private int mImageHeight;
    private int imageCount = 0;
    private int videoCount = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event3);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        if (Build.VERSION.SDK_INT == 19 || Build.VERSION.SDK_INT == 20) {
            savePath = getFilesDir().getPath() + "/";
        } else {
            savePath = Environment.getExternalStorageDirectory() + "/HDProject/";
        }
        barHelper.setTitleColor(getResources().getColor(R.color.white));
        mImageHeight = (UIUtils.getScreenWidth(context) - UIUtils.dip2px(context, 60)) / 4;
        pictureAdapter = new PictureAdapter(context);
        gridView.setAdapter(pictureAdapter);
        gridView.setOnItemClickListener(new itemClickListener());
        pictureAdapter.initData();
        registerOnReceiveListener();
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
//                submit();
                showActionSheet();
                break;
        }
    }

    @Subscribe
    public void onMessageEvent(ActionEntity event) {
        if (event != null) {
            String action = event.getAction();
            if (action.equals(Constants.Action.EXIT_ACTIVITY)) {
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
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
        actionSheet = new ActionSheet(EmergencyEventActivity2.this)
                .builder()
                .setCancelable(false)
                .setCanceledOnTouchOutside(true);

        actionSheet.addSheetItem("从手机相册选择", ActionSheet.SheetItemColor.Black,
                EmergencyEventActivity2.this)
                .addSheetItem("拍照片", ActionSheet.SheetItemColor.Black,
                        EmergencyEventActivity2.this)
                .addSheetItem("本地视频", ActionSheet.SheetItemColor.Black,
                        EmergencyEventActivity2.this)
                .addSheetItem("拍视频", ActionSheet.SheetItemColor.Black,
                        EmergencyEventActivity2.this);
        actionSheet.show();
    }

    @Override
    public void onClick(int which) {
        switch (which) {
            case 1:
                if (imageCount < 3) {
                    cameraType = OPEN_ALBUM;
                    getPermission();
                } else {
                    showToast("最多传三张图片");
                }
                break;
            case 2:
                if (imageCount < 3) {
                    cameraType = OPEN_CAMERA;
                    getPermission();
                } else {
                    showToast("最多传三张图片");
                }
                break;
            case 3:
                if (videoCount < 1) {
                    cameraType = LOCAL_VIDEO;
                    openActvityForResult(LocalVideoFilesActivity.class, LOCAL_VIDEO);
                } else {
                    showToast("最多传一个视频");
                }
                break;
            case 4:
                if (videoCount < 1) {
                    cameraType = RECORD_VIDEO;
                    getPermission();
                } else {
                    showToast("最多传一个视频");
                }
                break;
        }
    }

    private EventEntity eventEntity = null;
    private String serialNumTaskRequest;

    private void submit() {
        final String urls = getUrls(URLS_IMAGES);
        final String videos = getUrls(URLS_VIDEOS);
        final String urls_all = getUrls(URLS_ALL);
        final String description = getText(tvWrite);
        final String place = getText(tvPlace);

        if (TextUtils.isEmpty(urls_all)) {
            showToast("未上传图片或视频!");
            return;
        }

//        if (!isUploaded()) {
//            showToast("等待上传完毕，再提交!");
//            return;
//        }

        if (TextUtils.isEmpty(place)) {
            showToast("地址不能为空!");
            return;
        }

        if (TextUtils.isEmpty(description)) {
            showToast("描述内容为空!");
            return;
        }

        final String time = DateUtil.currentTime();
//        HashMap<String, String> map = new HashMap<>();
//        map.put("deviceID", "10012017020000000000");
//        map.put("longitude", "108.6");
//        map.put("latitude", "110");
//        map.put("uploadTime", time);
//        map.put("description", description);
//        map.put("picUrl", urls);
//        map.put("videoUrl", videos);
//        map.put("place", place);
//        map.put("eventType", "");
        String url = getUrls(URLS_ALL);
        eventEntity = new EventEntity();
        eventEntity.setTime(time);
        eventEntity.setDescription(description);
        eventEntity.setPlace(place);
        eventEntity.setUrls(url);

        UploadEventRequest request = new UploadEventRequest();
        request.deviceID = DeviceParams.getInstance().getDeviceId();
        request.longitude = "108.6";
        request.latitude = "110";
        request.uploadTime = time;
        request.description = description;
        request.picUrl = urls;
        request.videoUrl = videos;
        request.place = place;
        request.eventType = "";
        request.serialNum = StringUtils.getRandomString(20);
        serialNumTaskRequest = SocketManager.intance().launch(request);
//        showProgressDialog();
//        request.send(new BaseDataRequest.RequestCallback<String>() {
//            @Override
//            public void onSuccess(String json) {
//                String url = getUrls(URLS_ALL);
//                EventEntity entity = new EventEntity();
//                entity.setTime(time);
//                entity.setDescription(description);
//                entity.setPlace(place);
//                entity.setUrls(url);
//                EventDao.getInstance().save(entity);
//                openActvityForResult(UploadStateActivity.class, 100);
//            }
//
//            @Override
//            public void onFailure(String msg) {
//                showToast(msg);
//            }
//        });
    }

    private void registerOnReceiveListener() {
        SocketManager.intance().registertOnActionListener(DeviceCmd.UP_LOAD_EMERGENCY, new OnActionAdapter() {
            @Override
            public void onSendSucess(String cmd, String serialNum) {
                super.onSendSucess(cmd, serialNum);
                dismissProgressDialog();
                if (!serialNum.equals(serialNumTaskRequest)) {
                    return;
                }
                if (eventEntity != null) {
                    EventDao.getInstance().save(eventEntity);
                    openActvityForResult(UploadStateActivity.class, 100);
                }
            }

            @Override
            public void onResponseFaile(String cmd, String serialNum, String errorCode, String errorMsg) {
                super.onResponseFaile(cmd, serialNum, errorCode, errorMsg);
                dismissProgressDialog();
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
                        imageUri = FileProvider.getUriForFile(EmergencyEventActivity2.this, "com.atgc.cotton.fileProvider", file);//这里进行替换uri的获得方式
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//这里加入flag
                        startActivityForResult(intent, OPEN_CAMERA);
                    }
                } else if (cameraType == OPEN_ALBUM) {
//                    int count=3-imageCount;
                    MultiImageSelector.create(context).
                            showCamera(false).
                            count(1)
                            .multi() // 多选模式, 默认模式;
                            .start(EmergencyEventActivity2.this, OPEN_ALBUM);
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
        } else if (requestCode == 100 && resultCode == RESULT_OK) {
            finish();
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
                        .start(EmergencyEventActivity2.this, 100);
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

    private int objId = 0;

    private void uploadImageFile(final String path) {
        objId++;
        UploadEntity entity = new UploadEntity();
        entity.setLocalPath(path);
        entity.id = objId;
        pictureAdapter.addItem2(entity);
    }

    private void uploadVideoFile(final String path) {
        videoCount++;
//        addView(path);
    }


    private ArrayList<String> images = new ArrayList<>();

//    private void addView(String path) {
//        if (!images.contains(path)) {
//            images.add(path);
//        }
//        int count = ll_image.getChildCount();
//        if (count == 4) {
//            deleteLastItem();
//        }
//        if (count >= 1) {
//            ll_image.addView(insertImage(path, 0), count - 1);
//        } else {
//            ll_image.addView(insertImage(path, 0), 0);
//        }
//        ll_image.requestLayout();
//    }

//    private StringBuilder sb = new StringBuilder();

    private String getUrls(int type) {
        StringBuilder sb = new StringBuilder();
        String url = "";
        String str = sb.toString();
        if (str.contains(",")) {
            url = str.substring(0, str.length() - 1);
        }
        return url;
    }


    private boolean isImage(String url) {
        if (url.contains(".jpg") ||
                url.contains(".jpeg") ||
                url.contains(".png")) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isVideo(String url) {
        if (url.contains(".mp4") ||
                url.contains(".MP4") ||
                url.contains(".3gp")) {
            return true;
        } else {
            return false;
        }
    }
}
