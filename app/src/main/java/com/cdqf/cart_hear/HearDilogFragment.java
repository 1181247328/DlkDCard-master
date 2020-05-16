package com.cdqf.cart_hear;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.cdqf.cart.R;
import com.cdqf.cart_state.CartState;
import com.gcssloop.widget.RCRelativeLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import tech.gaolinfeng.imagecrop.lib.IOUtil;
import tech.gaolinfeng.imagecrop.lib.ImageCropActivity;

import static android.app.Activity.RESULT_OK;

/**
 * 更换头像对话框
 */
public class HearDilogFragment extends DialogFragment {

    private String TAG = HearDilogFragment.class.getSimpleName();

    private View view = null;

    //逻辑层
    private CartState cartState = CartState.getCartState();

    private EventBus eventBus = EventBus.getDefault();

    //相册
    private static final int REQUEST_CODE_SELECT_IMAGE = 1;

    //相机
    private static final int REQUEST_CODE_TAKE_PICTURE = 2;

    private static final int REQUEST_CODE_CROP = 3;

    private boolean cropCircle = true;

    //内容
    @BindView(R.id.lv_personal_dilog_context)
    public ListView lvPersonalDilogContext = null;

    private HearDilogAdapter hearDilogAdapter = null;

    //取消
    @BindView(R.id.rcrl_personal_dilog_cancel)
    public RCRelativeLayout rcrlPersonalDilogCancel = null;

    /**
     * 本地相册
     */
    private void photo() {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        i.setType("image/*");
        i.putExtra("crop", false);
        i.putExtra("return-data", true);
        startActivityForResult(i, REQUEST_CODE_SELECT_IMAGE);
    }

    /**
     * 相机
     */
    private void camera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(FileUtil.PUBLIC_CACHE);
        if (file.exists()) {
            file.delete();
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file)); // set the image file name
        startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);
    }

    private String getCropAreaStr() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        int rectWidth = screenWidth / 2;
        int left = screenWidth / 2 - rectWidth / 2;
        int right = screenWidth / 2 + rectWidth / 2;
        int top = screenHeight / 2 - rectWidth / 2;
        int bottom = screenHeight / 2 + rectWidth / 2;
        return left + ", " + top + ", " + right + ", " + bottom;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = getDialog().getWindow();
        window.setGravity(Gravity.BOTTOM);
        view = inflater.inflate(R.layout.dilog_hear, null);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //初始化前
        initAgo();

        //初始化控件
        initView();

        //适配器
        initAdapter();

        //注册监听器
        initListener();

        //初始化后
        initBack();
        return view;
    }

    /**
     * 初始化前
     */
    private void initAgo() {
        ButterKnife.bind(this, view);
    }

    /**
     * 初始化控件
     */
    private void initView() {
    }

    private void initAdapter() {
        hearDilogAdapter = new HearDilogAdapter(getContext());
        lvPersonalDilogContext.setAdapter(hearDilogAdapter);
    }

    /**
     * 注册监听器
     */
    private void initListener() {
        lvPersonalDilogContext.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    //本地相册
                    case 0:
                        if (Build.VERSION.SDK_INT >= 23) {
                            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 7);
                            } else {
                                photo();
                            }
                        } else {
                            photo();
                        }
                        break;
                    case 1:
                        if (Build.VERSION.SDK_INT >= 23) {
                            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 8);
                            } else {
                                camera();
                            }
                        } else {
                            camera();
                        }
                        break;
                }
            }
        });
    }

    /**
     * 初始化后
     */
    private void initBack() {
        getDialog().setCanceledOnTouchOutside(false);
    }

    @OnClick({R.id.rcrl_personal_dilog_cancel})
    public void onClick(View v) {
        switch (v.getId()) {
            //取消
            case R.id.rcrl_personal_dilog_cancel:
                dismiss();
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        getDialog().getWindow().setLayout(dm.widthPixels - 60, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            //相册
            case REQUEST_CODE_SELECT_IMAGE:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    ContentResolver cr = getContext().getContentResolver();
                    InputStream is = null;
                    FileOutputStream fos = null;
                    boolean writeSucceed = true;
                    try {
                        is = cr.openInputStream(uri);
                        fos = new FileOutputStream(FileUtil.IMG_CACHE1);
                        int read = 0;
                        byte[] buffer = new byte[4096];
                        while ((read = is.read(buffer)) > 0) {
                            fos.write(buffer);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        writeSucceed = false;
                    } finally {
                        IOUtil.closeQuietly(is);
                        IOUtil.closeQuietly(fos);
                    }
                    if (writeSucceed) {
                        Intent intent = ImageCropActivity.createIntent(getActivity(), FileUtil.IMG_CACHE1, FileUtil.IMG_CACHE2, getCropAreaStr(), cropCircle);
                        startActivityForResult(intent, REQUEST_CODE_CROP);
                    } else {
                        Toast.makeText(getActivity(), "无法打开图片文件，您的sd卡是否已满？", Toast.LENGTH_SHORT).show();
                    }
                } else {

                }
                break;
            //相机返回操作
            case REQUEST_CODE_TAKE_PICTURE:
                if (resultCode == RESULT_OK) {
                    Intent intent = ImageCropActivity.createIntent(getActivity(), FileUtil.PUBLIC_CACHE, FileUtil.IMG_CACHE2, getCropAreaStr(), cropCircle);
                    startActivityForResult(intent, REQUEST_CODE_CROP);
                } else {
                    // do nothing
                }
                break;
            //裁剪返回
            case REQUEST_CODE_CROP:
                if (resultCode == RESULT_OK) {
                    Bitmap bitmap = BitmapFactory.decodeFile(FileUtil.IMG_CACHE2);
                    cartState.setHeadBitmap(bitmap);
                    cartState.saveBitmapFile(bitmap, FileUtil.IMG_CACHE4);
                    eventBus.post(new ShelvesImageFind((bitmap)));
                    dismiss();
                }
                break;
        }
    }
}

