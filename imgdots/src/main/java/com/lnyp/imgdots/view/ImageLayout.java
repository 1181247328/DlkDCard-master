package com.lnyp.imgdots.view;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.lnyp.imgdots.R;
import com.lnyp.imgdots.bean.ForPoint;
import com.lnyp.imgdots.bean.PointSimple;

import java.util.ArrayList;
import java.util.List;

public class ImageLayout extends FrameLayout implements View.OnClickListener {

    ArrayList<PointSimple> points;

    List<ForPoint> forPointList;

    FrameLayout layouPoints;

    ImageView imgBg;

    Context mContext;

    private int dmWidth;

    private int dmHeight;

    private int type;

    private OnForMapListener onForMapListener;

    public ImageLayout(Context context) {
        this(context, null);
    }

    public ImageLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initView(context, attrs);
    }

    public void setOnForMapListenerF(OnForMapListener l) {
        this.onForMapListener = l;
    }


    private void initView(Context context, AttributeSet attrs) {
        mContext = context;
        View imgPointLayout = inflate(context, R.layout.layout_imgview_point, this);
        imgBg = imgPointLayout.findViewById(R.id.imgBg);
        layouPoints = imgPointLayout.findViewById(R.id.layouPoints);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    public void setImgBg(int width, int height, int map) {

        ViewGroup.LayoutParams lp = imgBg.getLayoutParams();
        lp.width = width;
        lp.height = height;

        imgBg.setLayoutParams(lp);

        ViewGroup.LayoutParams lp1 = layouPoints.getLayoutParams();
        lp1.width = width;
        lp1.height = height;

        layouPoints.setLayoutParams(lp1);

        imgBg.setImageResource(map);
        switch (type) {
            case 0:
                addPoints(width, height);
                break;
            case 1:
                addForPoints(width, height);
                break;
        }


    }

    public void setPoints(ArrayList<PointSimple> points, int type) {
        this.points = points;
        this.type = type;
    }

    public void setForPoints(List<ForPoint> points, int type) {
        this.forPointList = points;
        this.type = type;
    }

    private void addPoints(int width, int height) {

        layouPoints.removeAllViews();

        for (int i = 0; i < points.size(); i++) {

            double width_scale = points.get(i).width_scale;
            double height_scale = points.get(i).height_scale;


            LinearLayout view = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.layout_img_point, this, false);
            ImageView imageView = view.findViewById(R.id.imgPoint);
            imageView.setTag(i);

            AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getDrawable();
            animationDrawable.start();

            LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();

            layoutParams.leftMargin = (int) (width * width_scale);
            layoutParams.topMargin = (int) (height * height_scale);

            imageView.setOnClickListener(this);

            layouPoints.addView(view, layoutParams);
        }
    }

    private void addForPoints(int width, int height) {
        layouPoints.removeAllViews();
        for (int i = 0; i < forPointList.size(); i++) {

            double width_scale = forPointList.get(i).getAndroidX();
            double height_scale = forPointList.get(i).getAndroidY();
            LinearLayout view = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.layout_img_point, this, false);
            ImageView imageView = view.findViewById(R.id.imgPoint);
            if (forPointList.get(i).isLocation()) {
                imageView.setImageResource(R.mipmap.for_location);
            } else if (forPointList.get(i).isLoactionMap()) {
                imageView.setImageResource(R.mipmap.test5);
            } else {
                imageView.setImageResource(R.drawable.prod_point_img);
                AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getDrawable();
                animationDrawable.start();
            }
            imageView.setTag(i);

//            AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getDrawable();
//            animationDrawable.start();

            LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();

            layoutParams.leftMargin = (int) (width * width_scale);
            layoutParams.topMargin = (int) (height * height_scale);

            imageView.setOnClickListener(this);

            layouPoints.addView(view, layoutParams);
        }
    }


    @Override
    public void onClick(View view) {
        int pos = (int) view.getTag();

        switch (type) {
            case 0:
                Toast.makeText(getContext(), points.get(pos).spotName, Toast.LENGTH_SHORT).show();
                if (onForMapListener != null) {
                    onForMapListener.onMap(view, points, pos);
                }
                break;
            case 1:
                Toast.makeText(getContext(), forPointList.get(pos).getSpotName(), Toast.LENGTH_SHORT).show();
                if (onForMapListener != null) {
                    onForMapListener.onMap(view, forPointList, pos);
                }
                break;
        }

    }

    public interface OnForMapListener {
        void onMap(View view, ArrayList<PointSimple> points, int position);

        void onMap(View view, List<ForPoint> points, int position);
    }

}
