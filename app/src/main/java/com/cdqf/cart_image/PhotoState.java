package com.cdqf.cart_image;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liu on 2017/12/25.
 */

public class PhotoState {

    private String TAG = PhotoState.class.getSimpleName();

    public static PhotoState photoState = new PhotoState();

    private ImageLoader imageLoader = ImageLoader.getInstance();

    private Map<Integer, String> photoMap = new HashMap<Integer, String>();

    //看大图
    private List<String> pictureList = new ArrayList<String>();

    private List<String> pictureHttpList = new ArrayList<String>();

    //裁剪的图片
    private List<Photo> cropBitmapList = new ArrayList<Photo>();

    //须上传的图片
    private List<Photo> commentBitmapList = new ArrayList<Photo>();

    public static PhotoState getPhotoState() {
        return photoState;
    }

    public ImageLoader getImageLoader(Context context) {
        imageLoader.init(getConfiguration(context));
        return imageLoader;
    }

    /**
     * 初始化imageLoad
     *
     * @param context
     * @return
     */
    public ImageLoaderConfiguration getConfiguration(Context context) {
        ImageLoaderConfiguration configuration = getImageLoaderConfing(context, "imageLoaderword/Chace");
        return configuration;
    }

    /**
     * 保存图片的配置
     *
     * @param context
     * @param cache   "imageLoaderworld/Cache"
     */
    public ImageLoaderConfiguration getImageLoaderConfing(Context context, String cache) {
        File cacheDir = StorageUtils.getOwnCacheDirectory(context, cache);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder(context)
                .memoryCacheExtraOptions(480, 800)
                .threadPoolSize(10)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .discCacheSize(50 * 1024 * 1024)
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .discCacheFileCount(100)
                .discCache(new UnlimitedDiskCache(cacheDir))
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                .imageDownloader(new BaseImageDownloader(context, 5 * 1000, 30 * 1000))
                .writeDebugLogs()
                .build();
        return config;
    }

    /**
     * @param loading 加载图片时的图片
     * @param empty   没图片资源时的默认图片
     * @param fail    加载失败时的图片
     * @return
     */
    public DisplayImageOptions getImageLoaderOptions(int loading, int empty, int fail) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(loading)
                .showImageForEmptyUri(empty)
                .showImageOnFail(fail)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new RoundedBitmapDisplayer(5))
                .build();
        return options;
    }

    /**
     * 提示信息
     *
     * @param context
     * @param toast
     */
    public void initToast(Context context, String toast, boolean isShort, int type) {
        Toast initToast = null;
        if (isShort) {
            initToast = Toast.makeText(context, toast, Toast.LENGTH_SHORT);
        } else {
            initToast = Toast.makeText(context, toast, Toast.LENGTH_LONG);
        }

        switch (type) {
            case 0:
                break;
            //显示中间
            case 1:
                initToast.setGravity(Gravity.CENTER, 0, 0);
                initToast.show();
                break;
            //顶部显示
            case 2:
                initToast.setGravity(Gravity.TOP, 0, 0);
                break;
        }
        initToast.show();
    }

    public Map<Integer, String> getPhotoMap() {
        return photoMap;
    }

    public void setPhotoMap(Map<Integer, String> photoMap) {
        this.photoMap = photoMap;
    }

    public List<String> getPictureList() {
        return pictureList;
    }

    public void setPictureList(List<String> pictureList) {
        this.pictureList = pictureList;
    }

    public List<Photo> getCropBitmapList() {
        return cropBitmapList;
    }

    public List<String> getPictureHttpList() {
        return pictureHttpList;
    }

    public void setPictureHttpList(List<String> pictureHttpList) {
        this.pictureHttpList = pictureHttpList;
    }

    public void setCropBitmapList(List<Photo> cropBitmapList) {
        this.cropBitmapList = cropBitmapList;
    }

    public List<Photo> getCommentBitmapList() {
        return commentBitmapList;
    }

    public void setCommentBitmapList(List<Photo> commentBitmapList) {
        this.commentBitmapList = commentBitmapList;
    }
}
