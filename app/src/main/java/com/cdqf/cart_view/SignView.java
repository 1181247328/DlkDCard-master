package com.cdqf.cart_view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.cdqf.cart.R;

/**
 * 屏幕写字
 */
public class SignView extends View {
    private String TAG = SignView.class.getSimpleName();
    private Paint paint;
    private Canvas cacheCanvas;
    private Bitmap cachebBitmap;
    private Context context = null;
    private Path path;
    static final int BACKGROUND_COLOR = Color.WHITE;
    static final int BRUSH_COLOR = Color.BLACK;
    private boolean isSign = false;

    public SignView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        // initView(context);
        // TODO Auto-generated constructor stub
    }

    public SignView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        // initView(context);
        // TODO Auto-generated constructor stub
    }

    public SignView(Context context) {
        super(context);
        // initView(context);
        // TODO Auto-generated constructor stub
        this.context = context;
    }

    public void initView(Context context) {

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(4);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(ContextCompat.getColor(context, R.color.tab_main_text_icon));
        path = new Path();
        cachebBitmap = Bitmap.createBitmap(
                MeasureSpec.getSize(widthMeasureSpec),
                MeasureSpec.getSize(heightMeasureSpec), Bitmap.Config.ARGB_8888);
        cacheCanvas = new Canvas(cachebBitmap);
        cacheCanvas.drawColor(Color.TRANSPARENT);
    }

    public Bitmap getCachebBitmap() {
        return cachebBitmap;
    }

    public void clear() {
        if (cacheCanvas != null) {
            isSign = false;
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            cacheCanvas.drawPaint(paint);
            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStrokeWidth(4);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(ContextCompat.getColor(context, R.color.tab_main_text_icon));
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // canvas.drawColor(BRUSH_COLOR);
        canvas.drawBitmap(cachebBitmap, 0, 0, null);
        canvas.drawPath(path, paint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        int curW = cachebBitmap != null ? cachebBitmap.getWidth() : 0;
        int curH = cachebBitmap != null ? cachebBitmap.getHeight() : 0;
        if (curW >= w && curH >= h) {
            return;
        }

        if (curW < w)
            curW = w;
        if (curH < h)
            curH = h;

        Bitmap newBitmap = Bitmap.createBitmap(curW, curH,
                Bitmap.Config.ARGB_8888);
        Canvas newCanvas = new Canvas();
        newCanvas.setBitmap(newBitmap);
        if (cachebBitmap != null) {
            newCanvas.drawBitmap(cachebBitmap, 0, 0, null);
        }
        cachebBitmap = newBitmap;
        cacheCanvas = newCanvas;
    }

    private float cur_x, cur_y;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.e(TAG, "---正在签字中---");
        isSign = true;
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                if (isListener != null) {
                    isListener.sign();
                }
                cur_x = x;
                cur_y = y;
                path.moveTo(cur_x, cur_y);
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                path.quadTo(cur_x, cur_y, x, y);
                cur_x = x;
                cur_y = y;
                break;
            }

            case MotionEvent.ACTION_UP: {
                cacheCanvas.drawPath(path, paint);
                path.reset();
                break;
            }
        }

        invalidate();

        return true;
    }

    public interface isSignListener {
        void sign();
    }

    isSignListener isListener;

    public void setIsListener(isSignListener isListener) {
        this.isListener = isListener;
    }

    public boolean isSign() {
        return isSign;
    }
}

