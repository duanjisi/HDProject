package com.atgc.hd.comm.utils;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import com.atgc.hd.HDApplication;
import com.orhanobut.logger.Logger;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;

import static com.nostra13.universalimageloader.utils.StorageUtils.getCacheDirectory;

/**
 * <p>描述： 文件操作工具类
 * <p>作者： liangguokui 2018/1/12
 */
public class FileUtil {

    public static final String SD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static final String ROOT_FOLDER = "hdpatrol";

    public static final String ROOT_PATH = SD_PATH + File.separatorChar + ROOT_FOLDER;
    public static final String LOG_PATH = File.separatorChar + ROOT_PATH + File.separatorChar + "log";
    public static final String CRASH_LOG_ROOT = LOG_PATH + File.separatorChar + "crashlog" + File.separatorChar;


    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";

    public static File createTmpFile(Context context) throws IOException {
        File dir = null;
        if (TextUtils.equals(Environment.getExternalStorageState(), Environment.MEDIA_MOUNTED)) {
            dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            if (!dir.exists()) {
                dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM + "/Camera");
                if (!dir.exists()) {
                    dir = getCacheDirectory(context, true);
                }
            }
        } else {
            dir = getCacheDirectory(context, true);
        }
        return File.createTempFile(JPEG_FILE_PREFIX, JPEG_FILE_SUFFIX, dir);
    }

    public static final void closeQuietly(Closeable closeable) {
        if (closeable == null) {
            return;
        } else {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void close(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveImageToGallery(Context context, Bitmap bmp) {
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), "boss66Im");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String path = file.getAbsolutePath();
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path)));
    }

    /**
     * 根据路径获取文件名
     *
     * @param path
     * @return
     */
    public static String getFileName(String path) {
        int index = path.lastIndexOf("/");
        return path.substring(index + 1);
    }

    /**
     * 文件转二进制数组
     *
     * @param filePath
     * @return
     */
    public static byte[] File2byte(String filePath) {
        byte[] buffer = null;
        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }

    public static String getBase64Data(String path) {
        byte[] bytes = File2byte(path);
        return Base64Utils.encodeBytes(bytes);
    }

    /**
     * 格式化单位
     *
     * @param size
     * @return
     */
    public static String getFormatSize(double size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            return size + "Byte";
        }
        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "KB";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "MB";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
                + "TB";
    }

    // 删除在/sdcard/dcim/Camera/默认生成的文件
    public static void deleteDefaultFile(Context context, Uri uri) {
        String fileName = null;
        if (uri != null) {
            // content
            Log.d("Scheme", uri.getScheme());
            if (uri.getScheme().equals("content")) {
                Cursor cursor = context.getContentResolver().query(uri, null,
                        null, null, null);
                if (cursor != null) {
                    if (cursor.moveToNext()) {
                        int columnIndex = cursor
                                .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
                        fileName = cursor.getString(columnIndex);
                        //获取缩略图id
                        int id = cursor.getInt(cursor
                                .getColumnIndex(MediaStore.Video.VideoColumns._ID));
                        //获取缩略图
//                    Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(
//                            getContentResolver(), id, MediaStore.Images.Thumbnails.MICRO_KIND,
//                            null);

                        if (!fileName.startsWith("/mnt")) {
                            fileName = "/mnt/" + fileName;
                        }
                        Log.d("fileName", fileName);
                    }
                    cursor.close();
                }
            }
        }
        // 删除文件
        File file = new File(fileName);
        if (file.exists()) {
            file.delete();
            Log.d("delete", "删除成功");
        }
    }

    public static String getAssets(String fileName) {
        InputStream is = null;
        try {
            is = getAssetsInputStream(HDApplication.applicationContext(), fileName);
            return stream2String(is);
        } catch (Exception e) {
            Logger.e(e, fileName + "解析失败");
        } finally {
            closeQuietly(is);
        }

        return "";
    }

    public static InputStream getAssetsInputStream(Context context, String fileName) {
        InputStream is = null;
        try {
            is = context.getAssets().open(fileName);
        } catch (IOException e) {
            Logger.e(e, fileName + "打开失败");
        }
        return is;
    }

    public static String stream2String(InputStream is) {
        if (is == null) {
            return "";
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuffer strBuffer = new StringBuffer("");

        try {
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                strBuffer.append(line);
                strBuffer.append("\n");
            }
        } catch (IOException e) {
            Logger.e(e,"读取失败");
        } finally {
            closeQuietly(reader);

        }
        return strBuffer.toString();
    }

}
