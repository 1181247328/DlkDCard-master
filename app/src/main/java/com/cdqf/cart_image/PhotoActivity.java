package com.cdqf.cart_image;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cdqf.cart.R;
import com.cdqf.cart_state.BaseActivity;
import com.cdqf.cart_state.StatusBarCompat;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * 相册
 * Created by XinAiXiaoWen on 2017/3/30.
 */

public class PhotoActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    //输出日志
    private String TAG = PhotoActivity.class.getSimpleName();

    private PhotoState photoState = PhotoState.getPhotoState();

    public static PhotoActivity photoActivity = null;

    private EventBus photoEventBus = EventBus.getDefault();

    private List<String> pictureHttpList = new ArrayList<String>();

    //上下文
    private Context photoContext = null;

    //返回
    private RelativeLayout rlSeabedPhotoReturn = null;

    //已经选择图片
    private TextView tvSeabedPhotoSelectnumber = null;

    //确定
    private TextView tvSeabedPhotoMake = null;

    //图片list
    private GridView gvSeabedPhotoList = null;

    //图片适配器
    private PhotoAdapter photoAdapter = null;

    private List<String> photoList = null;

    private Cursor cursor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去掉标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //API19以下用于沉侵式菜单栏
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        //加载布局
        setContentView(R.layout.activity_photo);

        //API>=20以上用于沉侵式菜单栏
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            //沉侵
            StatusBarCompat.compat(this, getResources().getColor(R.color.black));
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 7);
        } else {
            init();
        }
    }

    private void init() {

        //初始化前
        initPhotoAgo();

        //初始化控件
        initPhotoView();

        //注册监听器
        initPhotoListener();

        //适配器
        initPhotoAdapter();

        //初始化后
        initPhotoBack();
    }

    /**
     * 初始化前
     */
    private void initPhotoAgo() {
        photoContext = this;
        photoActivity = this;
        photoEventBus.register(this);
        photoList = new ArrayList<String>();
        cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        getAbulm();
    }

    /**
     * 初始化控件
     */
    private void initPhotoView() {
        //返回
        rlSeabedPhotoReturn = this.findViewById(R.id.rl_seabed_photo_return);
        //已经选择照片
        tvSeabedPhotoSelectnumber = this.findViewById(R.id.tv_seabed_photo_selectnumber);
        //确定
        tvSeabedPhotoMake = this.findViewById(R.id.tv_seabed_photo_make);
        //图片list
        gvSeabedPhotoList = this.findViewById(R.id.gv_seabed_photo_list);
    }

    /**
     * 注册监听器
     */
    private void initPhotoListener() {
        //返回
        rlSeabedPhotoReturn.setOnClickListener(this);
        //确定
        tvSeabedPhotoMake.setOnClickListener(this);
        gvSeabedPhotoList.setOnItemClickListener(this);
    }

    private void initPhotoAdapter() {
        photoAdapter = new PhotoAdapter(photoContext, photoList, gvSeabedPhotoList, 9);
        gvSeabedPhotoList.setAdapter(photoAdapter);
    }

    /**
     * 初始化后
     */
    private void initPhotoBack() {
        gvSeabedPhotoList.setOnScrollListener(new PauseOnScrollListener(photoState.getImageLoader(photoContext), true, false));
    }

    /**
     * 扫描图片
     */
    private void getAbulm() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<String> list = new ArrayList<String>();
                while (cursor.moveToNext()) {
                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    list.add(path);
                    Log.e(TAG, "---" + path);
                }
                for (int i = list.size() - 1; i >= 0; i--) {
                    photoList.add(list.get(i));
                    photoState.getPictureList().add(list.get(i));
                }
                cursor.close();
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //返回
            case R.id.rl_seabed_photo_return:
                photoState.getCropBitmapList().clear();
                photoState.getPhotoMap().clear();
//                photoState.getPictureHttpList().clear();
                finish();
                break;
            //确定
            case R.id.tv_seabed_photo_make:
                if (photoState.getPhotoMap().size() > 0) {
                    pictureHttpList.clear();
                    pictureHttpList.addAll(photoState.getPictureHttpList());
                    photoEventBus.post(new PhotoFind(pictureHttpList));
//                    Intent intent = new Intent(photoContext, PhotoSelectActivity.class);
//                    startActivity(intent);
                    photoState.getPictureHttpList().clear();
                    finish();
                } else {
                    photoState.initToast(photoContext, "至少编辑一张图片", true, 0);
                }
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.e(TAG, "---" + position);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 7:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    init();
                } else {
                    photoState.initToast(photoContext, "权限申请取消,无法正常使用功能", true, 0);
                    photoState.getCropBitmapList().clear();
                    finish();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        photoState.getPhotoMap().clear();
        photoState.getCropBitmapList().clear();
//        photoState.getPictureHttpList().clear();
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG, "---启动---");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e(TAG, "---恢复---");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "---暂停---");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "---停止---");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "---重启---");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "---销毁---");
        photoState.getPhotoMap().clear();
        photoState.getCropBitmapList().clear();
        photoEventBus.unregister(this);
    }

    public void onEventMainThread(PhotoSelectNumberFind p) {
        tvSeabedPhotoSelectnumber.setText("(" + p.position + "/9)");
    }
}
