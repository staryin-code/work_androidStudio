package com.zeustel.top9.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.disc.impl.LimitedAgeDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.zeustel.top9.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 工具类
 *
 * @author NiuLei
 * @email niulei@zeustel.com
 * @date 2015/6/25 11:16
 */
public class Tools {
    /**
     * 01/09
     */
    public static final SimpleDateFormat formatDM = new SimpleDateFormat("dd/MM");
    /**
     * 8-10 09:40
     */
    public static final SimpleDateFormat formatMMddHHmm = new SimpleDateFormat("MM-dd HH:mm");
/*登录注册验证*/
    /**
     * 09:40
     */
    public static final SimpleDateFormat formatHHmm = new SimpleDateFormat("HH:mm");
    /**
     * 2015-08-19
     */
    public static final SimpleDateFormat formatyyyyDDNN = new SimpleDateFormat("yyyy-dd-MM");
    /**
     * 2015-08-19  12:58
     */
    public static final SimpleDateFormat formatAll = new SimpleDateFormat("yyyy-dd-MM  HH:mm");
    /**
     *
     */
    public static ExecutorService pool = Executors.newCachedThreadPool();
    public static Animation animLeftIn = null;
    public static Animation animRightIn = null;
    /**
     * 默认配置
     */
    public static DisplayImageOptions options = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.ic_loading).showImageOnFail(R.drawable.ic_loading).showImageOnLoading(R.drawable.ic_loading).cacheOnDisc().bitmapConfig(Bitmap.Config.RGB_565).imageScaleType(ImageScaleType.IN_SAMPLE_INT).displayer(new SimpleBitmapDisplayer()).build();

    public static void initDbUtils() {

    }

    /**
     * ImageLoader开源框架配置及初始化
     *
     * @param context
     */
    public static void initImageLoaderSDK(Context context) {
        /**
         * 缓存图片大小
         */
        int maxRam = Tools.getMaxRAM(context);
        int maxLimit = 0;
        if (maxRam == 0)
            maxLimit = 2 * 1024 * 1024;//2MB
        else
            maxLimit = maxRam / 8;//系统最大Ram的1/8
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.memoryCacheExtraOptions(400, 800);//max width, max height，即保存的每个缓存文件的最大长宽
        config.memoryCache(new LruMemoryCache(maxLimit));//最近最少算法
        //        config.memoryCacheSizePercentage(20);//内存缓存百分比
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.diskCache(new LimitedAgeDiskCache(Tools.getCacheDirImage(context), 50 * 1024 *
                1024));//自定义缓存路径
        if (Constants.DEBUG)
            config.writeDebugLogs();// Remove for release app
        ImageLoader.getInstance().init(config.build());
    }

    /**
     * 获取应用版本
     */
    public static int getAppVersion(Context context) {
        PackageInfo info = null;
        try {
            info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return -1;
        }
        return info.versionCode;
    }

    /**
     * 获取友盟统计渠道
     *
     * @param context
     * @return
     * @throws PackageManager.NameNotFoundException
     */
    public static String getAppChannel(Context context) throws PackageManager.NameNotFoundException {
        ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
        return appInfo.metaData.getString("UMENG_APPKEY");
    }

    /**
     * 高亮字符串
     *
     * @param text      原始字符串
     * @param lightText 高亮部分
     * @param color     颜色
     * @return 加工过的
     */
    public static SpannableStringBuilder highLight(String text, String lightText, int color) {
        if (!Tools.isEmpty(text)) {
            SpannableStringBuilder ssb = new SpannableStringBuilder(text);
            if (!Tools.isEmpty(lightText)) {
                int index = text.indexOf(lightText);
                if (index != -1) {
                    //                    BackgroundColorSpan 背景色
                    //                    ForegroundColorSpan 字体颜色
                    ssb.setSpan(new ForegroundColorSpan(color), index, index + lightText.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                }
            }
            return ssb;
        }
        return null;
    }

    /**
     * 监听软键盘完成键
     *
     * @param editText 输入框
     * @param runnable 操作
     */
    public static void listenSoftInput(EditText editText, final Runnable runnable) {
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm.isActive()) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                    new Handler(Looper.getMainLooper()).post(runnable);
                    return true;
                }
                return false;
            }
        });
    }

    public static Bitmap getThumbnail(Context context, Uri uri, int size) throws FileNotFoundException, IOException {
        InputStream input = context.getContentResolver().openInputStream(uri);
        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither = true;//optional
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();
        if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1))
            return null;
        int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight : onlyBoundsOptions.outWidth;
        double ratio = (originalSize > size) ? (originalSize / size) : 1.0;
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
        bitmapOptions.inDither = true;//optional
        bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        input = context.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();
        return bitmap;
    }

    private static int getPowerOfTwoForSampleRatio(double ratio) {
        int k = Integer.highestOneBit((int) Math.floor(ratio));
        if (k == 0)
            return 1;
        else
            return k;
    }

    /**
     * bitmap转换成文件
     *
     * @param bitmap
     * @param path
     * @return
     */
    @Deprecated
    public static String bitmap2File(Bitmap bitmap, String path) {
        boolean isSave = Tools.saveBitmap2Native(bitmap, path);
        Tools.log_i(Tools.class, "bitmap2File", "is : " + isSave);
        if (isSave) {
            return path;
        }
       /* File file = new File(path);
        if (file.exists()) {
            file.delete();
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
                return file.getPath();
            } catch (Exception e) {
                e.printStackTrace();
                Tools.log_i(Tools.class,"bitmap2File","e");
                return null;
            } finally {
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    out = null;
                }
            }
        }*/
        return null;
    }

    /**
     * 开始裁剪图片意图
     */
    public static void startTailoringPicture(Uri imageUri, Fragment fragment, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 400);
        intent.putExtra("outputY", 400);
        intent.putExtra("scale", true);
        intent.putExtra("scaleUpIfNeeded", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        fragment.startActivityForResult(intent, requestCode);
    }

    /**
     * 检查权限
     * Tools.checkPermission(VoiceActivity.this);
     * http://blog.csdn.net/singleton1900/article/details/39480187
     */
    public static void checkPermission(Context context) {
        String packName = context.getPackageName();
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(packName, PackageManager.GET_PERMISSIONS);
            String[] requestedPermissions = packageInfo.requestedPermissions;
            if (requestedPermissions != null && requestedPermissions.length > 0) {
                for (int i = 0; i < requestedPermissions.length; i++) {
                    String permission = requestedPermissions[i];
                    boolean have = PackageManager.PERMISSION_GRANTED == packageManager.checkPermission(permission, packName);
                    if (have) {
                        Tools.log_i(Tools.class, "checkPermission", "have : " + permission);
                    } else {
                        Tools.log_i(Tools.class, "checkPermission", "not : " + permission);

                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 监听软键盘完成键
     *
     * @param editText  输入框
     * @param imeAction 软键盘action see{@link EditorInfo}
     * @param hide      是否隐藏软键盘
     * @param runnable  操作
     */
    public static void listenSoftInput(EditText editText, final int imeAction, final boolean hide, final Runnable runnable) {
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == imeAction) {
                    if (hide) {
                        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm.isActive()) {
                            imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        }
                    }
                    new Handler(Looper.getMainLooper()).post(runnable);
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * 隐藏软键盘
     */
    public static void hideSoftInput(Context context, IBinder windowToken) {
        InputMethodManager im = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (im.isActive()) {
            im.hideSoftInputFromWindow(windowToken, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 显示软键盘
     */
    public static void showSoftInput(Context context, IBinder windowToken) {
        InputMethodManager im = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (!im.isActive()) {
            im.showSoftInputFromInputMethod(windowToken, InputMethodManager.RESULT_SHOWN);
        }
    }

    /**
     * 字符串转换成图片集合
     */
    public static List<String> convertPathList(String pathStr) {
        String path[] = convertPathArray(pathStr);
        if (path != null) {
            return Arrays.asList(path);
        }
        return null;
    }

    /**
     * 字符串转换成图片数组
     */
    public static String[] convertPathArray(String pathStr) {
        if (!Tools.isEmpty(pathStr)) {
            String[] path = pathStr.split(",");
            return path;
        }
        return null;
    }

    /**
     * 图片路径集合转换成字符串
     */
    public static String convertPathStr(List<String> pathList) {
        if (!Tools.isEmpty(pathList)) {
            StringBuffer buff = new StringBuffer();
            for (int i = 0; i < pathList.size(); i++) {
                buff.append(pathList.get(i));
                if (pathList.size() - 1 != i) {
                    buff.append(",");
                }
            }
            return buff.toString();
        }
        return null;
    }

    /**
     * md5加密字符串
     */
    public static String md5Encrypt(String str) throws NoSuchAlgorithmException {
        if (str == null) {
            return null;
        }
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(str.getBytes());
        return toHexString(md5.digest());
    }

    /**
     * 屏幕截图 (状态栏部分 空白)
     *
     * @param activity 所在主页面
     * @param name     图片名称
     * @return
     */
    public static boolean screenShot(Activity activity, String name) {
        if (activity == null) {
            return false;
        }
        View view = activity.getWindow().getDecorView();
        Bitmap bitmap = Tools.getViewDrawingCache(view);
        int[] displayMetrics = Tools.getDisplayMetrics(activity);
        int statusBarHeight = Tools.measureStateBarHeight(activity);
        //除去状态栏
        bitmap = Bitmap.createBitmap(bitmap, 0, statusBarHeight, displayMetrics[0], displayMetrics[1] - statusBarHeight);
        return Tools.saveBitmap2Native(bitmap, name);
    }

    /**
     * view 截图
     */
    public static boolean screenShot(View view, String name) {
        Bitmap bitmap = Tools.getViewDrawingCache(view);
        return Tools.saveBitmap2Native(bitmap, name);
    }

    /**
     * 获取view缓存视图
     */
    public static Bitmap getViewDrawingCache(View view) {
        if (view == null) {
            return null;
        }
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap drawingCache = view.getDrawingCache();
        view.destroyDrawingCache();
        return drawingCache;
    }

    /**
     * 保存位图到本地
     *
     * @param bitmap 位图
     * @param name   名称
     * @return
     */
    public static boolean saveBitmap2Native(Bitmap bitmap, String name) {
        if (bitmap == null) {
            return false;
        }
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(name);
            boolean isSave = bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            Tools.log_i(Tools.class, "saveBitmap2Native", "isSave " + isSave);
            return isSave;
        } catch (IOException e) {
            e.printStackTrace();
            Tools.log_i(Tools.class, "saveBitmap2Native", "" + e.getMessage());
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                out = null;
            }
            /*if (bitmap != null) {
                bitmap.recycle();
            }*/
        }
        return false;
    }

    /**
     * 文件信息摘要
     *
     * @param file
     */
    public static String md5Encrypt(File file) throws NoSuchAlgorithmException {
        if (file == null) {
            return null;
        }
        if (!file.exists())
            return null;
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        MappedByteBuffer byteBuffer;
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            byteBuffer = in.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            md5.update(byteBuffer);
            return toHexString(md5.digest());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 返回此 BigInteger 的十六进制字符串表示形式
     */
    private static String toHexString(byte[] buff) {
        if (buff == null) {
            return null;
        }
        BigInteger bi = new BigInteger(1, buff);
        return bi.toString(16);
    }

    /**
     * 赞+1效果
     */
    public static void startGoodOne(Context context, final View goodView) {
        if (goodView == null) {
            return;
        }
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.good_add_one_anim);
        animation.setDuration(500);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                goodView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                animation.cancel();
                goodView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        goodView.startAnimation(animation);
    }

    /**
     * log 管理
     */
    public static void log_i(Class cls, String methodName) {
        if (Constants.DEBUG)
            Log.i(Constants.TAG, "class : " + cls.getSimpleName() + ", method : " + methodName);
    }

    public static void log_i(Class cls, String methodName, String msg) {
        if (Constants.DEBUG)
            Log.i(Constants.TAG, "class : " + cls.getSimpleName() + ", method : " + methodName +
                    ", msg ==>>> " + msg);
    }

    public static void log_e(Class cls, String methodName) {
        if (Constants.DEBUG)
            Log.e(Constants.TAG, "class : " + cls.getSimpleName() + ", method : " + methodName);
    }

    public static void log_e(Class cls, String methodName, String msg) {
        if (Constants.DEBUG)
            Log.e(Constants.TAG, "class : " + cls.getSimpleName() + ", method : " + methodName +
                    ", msg ==>>> " + msg);
    }

    public static void loadAnim(Context context) {
        if (animLeftIn == null) {
            animLeftIn = AnimationUtils.loadAnimation(context, R.anim.left_in);
        }
        if (animRightIn == null) {
            animRightIn = AnimationUtils.loadAnimation(context, R.anim.right_in);
        }
    }

    public static void animIn(final View itemView, Handler handler, boolean isLeftIn) {
        Tools.pool.submit(new DelayAnimRunnable(itemView, handler, isLeftIn));
    }

    /**
     * color 转换成字符串
     */
    public static String colorToString(Resources res, int colorId) {
        int color = res.getColor(colorId);
        return "#" + Integer.toHexString(color);
    }

    /**
     * 判断是否为历史请求
     *
     * @param time      时间
     * @param isHistory 是否为历史
     * @return 是否为历史
     */
    public static boolean isHistory(long time, boolean isHistory) {
        //同时满足才为历史
        return isHistory && time > 0;
    }

    /**
     * 设置宽高 解决RecylerAdapter item视图显示问题
     *
     * @param view
     * @return
     */
    public static View setLayoutParams(View view) {
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
        if (params == null) {
            params = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
        } else {
            params.width = RecyclerView.LayoutParams.MATCH_PARENT;
            params.height = RecyclerView.LayoutParams.MATCH_PARENT;
        }
        view.setLayoutParams(params);
        return view;
    }

    /**
     * activity全屏
     *
     * @param activity
     * @note 需在setcontentView之前
     */
    public static void fullScreen(Activity activity) {
        if (activity != null) {
            activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    /**
     * 数组是否为空
     *
     * @param array 数组
     * @return 是否为空
     */
    public static <T> boolean isEmpty(T[] array) {
        return !(array != null && array.length > 0);
    }

    /**
     * list集合是否为空
     *
     * @param data list集合
     * @return 是否为空
     */
    public static boolean isEmpty(List data) {
        return !(data != null && !data.isEmpty());
    }

    /**
     * list集合是否为空
     *
     * @param data list集合
     * @return 是否为空
     */
    public static boolean isEmptyWithIgnore(List data, int ignore) {
        return !(data != null && data.size() > ignore);
    }

    /**
     * 获取集合第一条数据
     *
     * @param data 集合
     * @param <T>  类型
     * @return 第一个数据
     */
    public static <T> T getFirstData(List<T> data) {
        if (!Tools.isEmpty(data))
            return data.get(0);
        return null;
    }

    /**
     * 获取集合最后一条数据
     *
     * @param data 集合
     * @param <T>  类型
     * @return 最后一个数据
     */
    public static <T> T getLastData(List<T> data) {
        if (!Tools.isEmpty(data))
            return data.get(data.size() - 1);
        return null;
    }

    /**
     * 获取集合第一条数据
     *
     * @param data   集合
     * @param <T>    类型
     * @param ignore 忽略条数
     * @return 忽略一定数目后第一条数据
     */
    public static <T> T getFirstDataWithIgnore(List<T> data, int ignore) {
        if (ignore <= 0) {
            return getFirstData(data);
        } else {
            if (data != null && data.size() > ignore)
                return data.get(ignore);
        }
        return null;
    }

    /**
     * 获取集合最后条数据
     *
     * @param data   集合
     * @param <T>    类型
     * @param ignore 忽略条数
     * @return 忽略一定数目后最后一条数据
     */
    public static <T> T getLastDataWithIgnore(List<T> data, int ignore) {
        if (ignore <= 0) {
            return getLastData(data);
        } else {
            if (data != null && data.size() > ignore)
                return data.get(data.size() - 1);
        }
        return null;
    }

    /**
     * 是否触摸到这个View外部
     */
    public static boolean isOutsideOf(View v, MotionEvent ev) {
        Rect rect = new Rect();
        v.getGlobalVisibleRect(rect);
        return !rect.contains((int) ev.getX(), (int) ev.getY());
    }

    /**
     * 提示
     *
     * @param context
     * @param text    需提示的信息
     */
    public static void showToast(Context context, String text) {
        if (context != null)
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    /**
     * 提示
     *
     * @param context
     * @param resId   需提示信息的资源id
     */
    public static void showToast(Context context, int resId) {
        if (context != null)
            Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
    }

    /**
     * 判断字符串是否为空 空格 null字符串
     */
    public static boolean isEmpty(String text) {
        if (!TextUtils.isEmpty(text)) {
            String trim = text.trim();
            if (trim.length() > 0) {
                return trim.equals("null");
            }
        }
        return true;
    }

    /**
     * 用户头像 圆形
     */

    public static DisplayImageOptions newOptions(int cornerRadiusPixels) {
        return new DisplayImageOptions.Builder().showStubImage(R.mipmap.ic_icon).showImageForEmptyUri(R.mipmap.ic_icon).showImageOnFail(R.mipmap.ic_icon).cacheOnDisc().bitmapConfig(Bitmap.Config.RGB_565).imageScaleType(ImageScaleType.IN_SAMPLE_INT).displayer(new RoundedBitmapDisplayer(cornerRadiusPixels)).build();
    }

    public static String getTimeFormatMD(long time) {
        Date date = new Date(time);
        return formatDM.format(date);
    }

    public static String getTimeFormatMMddHHmm(long time) {
        Date date = new Date(time);
        return formatMMddHHmm.format(date);
    }

    public static String getTimeFormatformatHHmm(long time) {
        Date date = new Date(time);
        return formatHHmm.format(date);
    }

    public static String getTimeFormatformatyyyyDDNN(long time) {
        Date date = new Date(time);
        return formatyyyyDDNN.format(date);
    }

    public static String getTimeformatAll(long time) {
        Date date = new Date(time);
        return formatAll.format(date);
    }

    /**
     * 2015年01月09日
     */
    public static String getTimeFormatYMD(Resources res, long milliseconds) {
        return new SimpleDateFormat(res.getString(R.string.date_format_str, "yyyy", "dd", "MM")).format(new Date(milliseconds));
    }

    /**
     * 组件绘制完成后操作
     *
     * @param view     组件
     * @param runnable 操作
     */
    public static void endCreateOperate(final View view, final Runnable runnable) {
        if (view != null && runnable != null) {
            ViewTreeObserver vto = view.getViewTreeObserver();
            vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    view.getViewTreeObserver().removeOnPreDrawListener(this);
                    new Handler(Looper.myLooper()).post(runnable);
                    return false;
                }
            });
        }
    }

    /**
     * 获取资源文件尺寸 并四舍五入
     *
     * @param res Resources
     * @param id  资源Id
     * @return int类型尺寸
     */
    public static int getDimension(Resources res, int id) {
        return Math.round(res.getDimension(id));
    }


    /**
     * 代码实现selecotr
     *
     * @param normal  默认颜色资源id
     * @param checked 选中颜色资源id
     * @return ColorStateList
     */
    public static StateListDrawable getCheckStateDrawable(Resources res, int normal, int checked) {
        /*颜色选择器*/
        /*int[][] state = {{-android.R.attr.state_checked}, {android.R.attr.state_checked}};
        int[] color = {getResources().getColor(normal), getResources().getColor(checked)};
        ColorStateList colorStateList = new ColorStateList(state, color);*/
        /*drawable选择器*/
        StateListDrawable drawable = new StateListDrawable();
        drawable.addState(new int[]{-android.R.attr.state_checked}, res.getDrawable(normal));
        /*drawable.addState(new int[]{-android.R.attr.state_selected},res.getDrawable(normal));*/
        drawable.addState(new int[]{android.R.attr.state_checked}, res.getDrawable(checked));
        /*drawable.addState(new int[]{android.R.attr.state_selected},res.getDrawable(checked));*/
        return drawable;
    }

    /**
     * 代码实现selecotr
     *
     * @param normal   默认颜色资源id
     * @param selected 选中颜色资源id
     * @return ColorStateList
     */
    public static StateListDrawable getListViewSelector(Resources res, int normal, int selected) {
        /*颜色选择器*/
        /*int[][] state = {{-android.R.attr.state_checked}, {android.R.attr.state_checked}};
        int[] color = {getResources().getColor(normal), getResources().getColor(checked)};
        ColorStateList colorStateList = new ColorStateList(state, color);*/
        /*drawable选择器*/
        Drawable nor = res.getDrawable(normal);
        Drawable sel = res.getDrawable(selected);
        StateListDrawable drawable = new StateListDrawable();
        drawable.addState(new int[]{-android.R.attr.state_selected}, nor);
        /*drawable.addState(new int[]{-android.R.attr.state_selected},res.getDrawable(normal));*/
        drawable.addState(new int[]{android.R.attr.state_selected}, sel);
        /*drawable.addState(new int[]{-android.R.attr.state_focused},nor);*/
        /*drawable.addState(new int[]{android.R.attr.state_selected},res.getDrawable(checked));*/
     /*   <item android:state_window_focused="false"
        android:drawable="@drawable/pic1" />
        <!-- 非触摸模式下获得焦点并单击时的背景图片 -->
        <item android:state_focused="true" android:state_pressed="true"   android:drawable=
        "@drawable/pic2" />
        <!-- 触摸模式下单击时的背景图片-->
        <item android:state_focused="false" android:state_pressed="true"
        android:drawable="@drawable/pic3" />
        <!--选中时的图片背景-->
        <item android:state_selected="true"   android:drawable="@drawable/pic4" />
        <!--获得焦点时的图片背景-->
        <item android:state_focused="true"   android:drawable="@drawable/pic5" />*/
        return drawable;
    }

    /**
     * 获取状态栏高度
     */
    public static int measureStateBarHeight(Activity activity) {
        if (activity == null) {
            return 0;
        }
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        return frame.top;
    }

    /**
     * 获取标题栏高度
     */
    public static int measureTitleBarHeight(Activity activity) {
        int contentTop = activity.getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
        return contentTop - measureStateBarHeight(activity);
    }

    /**
     * 获取屏幕分辨率
     *
     * @return 宽高数组
     */
    public static int[] getDisplayMetrics(Context context) {
        try {
            return getRealityDpi(context);
        } catch (Exception e) {
            e.printStackTrace();
            return getNormalDpi(context);
        }
    }

    public static int getAnimIn() {
        switch (new Random().nextInt(3)) {
            case 0:
                return R.anim.left_to_right_in;
            case 1:
                return R.anim.right_to_left_in;
            case 2:
                return R.anim.zoomin;
            // case 3:
            // return R.anim.zoom_back_in;
            // case 4:
            // return R.anim.move_down_enter;
            // case 5:
            // return R.anim.move_up_enter;
            // case 6:
            // return R.anim.anticlockwise_in;
            // case 7:
            // return R.anim.clockwise_in;
            // case 8:
            // return R.anim.bottom_left_corner_up_in;
            // case 9:
            // return R.anim.bottom_right_corner_up_in;
            // case 10:
            // return R.anim.top_left_corner_down_in;
            // case 11:
            // return R.anim.top_right_corner_down_in;
        }
        return R.anim.left_to_right_in;
    }

    public static int getAnimOut() {
        switch (new Random().nextInt(3)) {
            case 0:
                return R.anim.left_to_right_out;
            case 1:
                return R.anim.right_to_left_out;
            case 2:
                return R.anim.zoomout;
            // case 3:
            // return R.anim.zoom_back_out;
            // case 4:
            // return R.anim.move_down_exit;
            // case 5:
            // return R.anim.move_up_exit;
            // case 6:
            // return R.anim.anticlockwise_out;
            // case 7:
            // return R.anim.clockwise_out;
            // case 8:
            // return R.anim.bottom_left_corner_up_out;
            // case 9:
            // return R.anim.bottom_right_corner_up_out;
            // case 10:
            // return R.anim.top_left_corner_down_out;
            // case 11:
            // return R.anim.top_right_corner_down_out;
        }
        return R.anim.left_to_right_out;
    }


    /**
     * 获取屏幕分辨率 普通屏幕
     */
    private static int[] getNormalDpi(Context context) {
        int dpi[] = new int[2];
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        dpi[0] = dm.widthPixels;
        dpi[1] = dm.heightPixels;
        return dpi;
    }

    /**
     * 获取屏幕分辨率 屏幕上有按键
     */
    private static int[] getRealityDpi(Context context) throws Exception {
        int dpi[] = new int[2];
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        Class c = Class.forName("android.view.Display");
        Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
        method.invoke(display, dm);
        dpi[0] = dm.widthPixels;
        dpi[1] = dm.heightPixels;
        return dpi;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int sp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int px2sp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 获取缓存目录
     *
     * @param context
     * @return
     */
    public static File getCacheDir(Context context) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File[] dirs = ContextCompat.getExternalCacheDirs(context);
            if (!Tools.isEmpty(dirs)) {
                for (int i = 0; i < dirs.length; i++) {
                    Tools.log_i(Tools.class, "getCacheDir", "dirs : " + dirs[i].getAbsolutePath());
                }
                return dirs[0];
            }
        }
        return context.getCacheDir();
    }

    /**
     * 程序是否在前台运行
     */
    public static boolean isAppOnForeground(Context context, String packageName) {
        // Returns a list of application processes that are running on the
        // device
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = am.getRunningAppProcesses();
        if (appProcesses == null)
            return false;

        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            // The name of the process that this object is associated with.
            if (appProcess.processName.equals(packageName) && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取缓存目录路径
     *
     * @param context
     * @return
     */
    public static String getCachePath(Context context) {
        File file = getCacheDir(context);
        return file.getAbsolutePath();
    }

    /**
     * 获取图片缓存目录
     *
     * @param context
     * @return
     */
    public static File getCacheDirImage(Context context) {
        File file = new File(getCacheDir(context), Constants.PATH_IMAGE);
        if (!file.exists())
            file.mkdirs();
        return file;
    }

    /**
     * 获取表情缓存目录
     *
     * @param context
     * @return
     */
    public static File getCacheDirFace(Context context) {
        File file = new File(getCacheDir(context), Constants.PATH_IMAGE_FACE);
        if (!file.exists())
            file.mkdirs();
        return file;
    }

    /**
     * 清除imageView内存
     */
    public static void recycleImg(ImageView imageView) {
        if (imageView == null) {
            return;
        }
        final Drawable drawable = imageView.getDrawable();
        if (drawable != null && drawable instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            if (bitmap != null) {
                if (!bitmap.isRecycled()) {
                    bitmap.recycle();
                }
                bitmap = null;
            }
        }
        imageView.setImageDrawable(null);
    }

    /**
     * 复制文件
     *
     * @param source 源
     * @param target 目的地
     * @throws IOException
     */
    public static void copyFile(File source, File target) throws IOException {
        FileInputStream inputStream = new FileInputStream(source);
        Tools.copyFile(inputStream, target);
    }

    public static void copyFile(InputStream inputStream, File target) throws IOException {
        FileOutputStream outputStream = new FileOutputStream(target);
        byte[] buff = new byte[2048];
        int len = -1;
        while ((len = inputStream.read(buff)) != -1) {
            outputStream.write(buff, 0, len);
        }
        inputStream.close();
        inputStream = null;
        outputStream.flush();
        outputStream.close();
        outputStream = null;
    }

    /**
     * 获取设备唯一表示 - 设备Id
     *
     * @param context
     * @return 设备Id
     */
    public static String getDeviceId(Context context) {
        android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceId();
    }

    /**
     * 获取设备唯一表示 - mac地址
     *
     * @param context
     * @return
     */
    public static String getMacAddress(Context context) {
        android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return wifi.getConnectionInfo().getMacAddress();
    }

    // 获取mac地址
    public static String getMac() {
        String macSerial = null;
        String str = "";
        try {
            Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address ");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            for (; null != str; ) {
                str = input.readLine();
                if (str != null) {
                    macSerial = str.trim();
                    break;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return macSerial;
    }

    /**
     * 获取单个应用最大运行时内存 单位 byte 一般为16M
     *
     * @param context
     * @return
     */
    public static int getMaxRAM(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //单位MB
        int maxMemory = manager.getMemoryClass();
        return maxMemory * 1024 * 1024;
    }
}
