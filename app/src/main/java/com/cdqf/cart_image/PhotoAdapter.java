package com.cdqf.cart_image;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.GridView;

import com.cdqf.cart.R;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.HashMap;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * 相册适配器
 * Created by XinAiXiaoWen on 2017/3/30.
 */

public class PhotoAdapter extends BaseAdapter {

    private String TAG = PhotoAdapter.class.getSimpleName();

    private Context photoContext = null;

    private EventBus photoEventBus = EventBus.getDefault();

    private PhotoState photoState = PhotoState.getPhotoState();

    private ImageLoader imageLoader = ImageLoader.getInstance();

    private Point point = new Point();

    private GridView gridView = null;

    private List<String> photoList = null;

    private Class<?> activity = null;

    private int number = 0;

    private HashMap<Integer, Boolean> mSelectMap = new HashMap<Integer, Boolean>();

    private HashMap<CompoundButton, Integer> mPhotoMap = new HashMap<CompoundButton, Integer>();

    public PhotoAdapter(Context photoContext, List<String> photoList, GridView gridView, int number) {
        this.photoContext = photoContext;
        this.photoList = photoList;
        this.gridView = gridView;
        this.number = number;
        imageLoader = photoState.getImageLoader(photoContext);
    }

    /**
     * @param photoContext 上下文
     * @param photoList    图片
     * @param gridView     适配器
     * @param activiity    大图跳转
     * @param number       上传数量
     */
    public PhotoAdapter(Context photoContext, List<String> photoList, GridView gridView, Class<?> activiity, int number) {
        this.photoContext = photoContext;
        this.photoList = photoList;
        this.gridView = gridView;
        this.activity = activity;
        this.number = number;
        imageLoader = photoState.getImageLoader(photoContext);
    }

    /**
     * 给CheckBox加点击动画，利用开源库nineoldandroids设置动画
     *
     * @param view
     */
    private void addAnimation(View view) {
        float[] vaules = new float[]{0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f, 1.1f, 1.2f, 1.3f, 1.25f, 1.2f, 1.15f, 1.1f, 1.0f};
        AnimatorSet set = new AnimatorSet();
        set.playTogether(ObjectAnimator.ofFloat(view, "scaleX", vaules),
                ObjectAnimator.ofFloat(view, "scaleY", vaules));
        set.setDuration(150);
        set.start();
    }

    @Override
    public int getCount() {
        return photoList.size();
    }

    @Override
    public Object getItem(int position) {
        return photoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final PhotoViewHolder photoViewHolder;
        String path = photoList.get(position);
        if (convertView == null) {
            //加载布局
            convertView = LayoutInflater.from(photoContext).inflate(R.layout.item_photo, null);
            photoViewHolder = new PhotoViewHolder();
            //图片
            photoViewHolder.ivSeabedPhotoItemImage = convertView.findViewById(R.id.iv_seabed_photo_item_image);
            //选择
            photoViewHolder.cbSeabedPhotoItemSelect = convertView.findViewById(R.id.cb_seabed_photo_item_select);

            convertView.setTag(photoViewHolder);
        } else {
            photoViewHolder = (PhotoViewHolder) convertView.getTag();
            photoViewHolder.ivSeabedPhotoItemImage.setImageResource(R.mipmap.not_loaded);
        }

        mPhotoMap.put(photoViewHolder.cbSeabedPhotoItemSelect, position);

        photoViewHolder.ivSeabedPhotoItemImage.setTag(path);

        photoViewHolder.cbSeabedPhotoItemSelect.setOnCheckedChangeListener(new OnPhoneCheckedChangeListener(position));

        photoViewHolder.cbSeabedPhotoItemSelect.setChecked(mSelectMap.get(position) != null);

        imageLoader.displayImage(
                "file://" + path,
                photoViewHolder.ivSeabedPhotoItemImage,
                photoState.getImageLoaderOptions(R.mipmap.not_loaded, R.mipmap.not_loaded, R.mipmap.not_loaded)
        );

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "---" + position);
                if (activity != null) {
                    Intent intent = new Intent(photoContext, activity);
                    intent.putExtra("position", position);
                    photoContext.startActivity(intent);
                }
            }
        });
        return convertView;
    }

    class OnPhoneCheckedChangeListener implements CompoundButton.OnCheckedChangeListener {

        private int position;

        public OnPhoneCheckedChangeListener(int position) {
            this.position = position;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            Log.e(TAG, "---" + position + "---" + isChecked);
            if (isChecked) {
                photoState.getPictureHttpList().add(photoList.get(position));
                photoState.getPhotoMap().put(position, photoList.get(position));
                photoEventBus.post(new PhotoSelectNumberFind(photoState.getPhotoMap().size()));
                if (photoState.getPhotoMap().size() > number) {
                    photoState.getPhotoMap().remove(position);
                    photoEventBus.post(new PhotoSelectNumberFind(number));
                    photoState.initToast(photoContext, "最多上传" + number + "张", true, 0);
                    buttonView.setChecked(false);
                } else {
                    if (!mSelectMap.containsKey(position) || !mSelectMap.get(position)) {
                        addAnimation(buttonView);
                        mSelectMap.put(mPhotoMap.get(buttonView), true);
                    }
                }
            } else {
                mSelectMap.remove(mPhotoMap.get(buttonView));
                photoState.getPhotoMap().remove(position);
                photoState.getPictureHttpList().remove(photoList.get(position));
                photoEventBus.post(new PhotoSelectNumberFind(photoState.getPhotoMap().size()));
            }
        }
    }
}
