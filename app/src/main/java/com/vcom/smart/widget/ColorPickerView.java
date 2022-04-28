package com.vcom.smart.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.DrawFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class ColorPickerView extends View implements GestureDetector.OnGestureListener {
    PointF centerPoint;
    Bitmap colorWheelBitmap;
    Paint colorWheelPaint;
    PointF displayBlockPoint;
    Paint displayPaint;
    Bitmap indicatorBitmap;
    Paint indicatorPaint;
    PointF indicatorPoint;
    float indicatorRadius;
    long lastChangedTime;
    private OnColorChangedListener listener;
    private GestureDetector mDetector;
    private Matrix matrix = new Matrix();
    PaintFlagsDrawFilter paintFlagsDrawFilter;
    int switchBtnWidth = 0;

    public ColorPickerView(Context context) {
        super(context);
        this.init(context);
    }

    public ColorPickerView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.init(context);
    }

    public ColorPickerView(Context context, AttributeSet attributeSet, int n2) {
        super(context, attributeSet, n2);
        this.init(context);
    }

    private Bitmap createRotatingWheelBitmap(int n2) {
        float f3 = 0.25f * (float) n2;
        float f4 = f3 / 2.0f;
        float f5 = (float) n2 - f4;
        RectF rectF = new RectF(f4, f4, f5, f5);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStrokeWidth(f3);
        paint.setStyle(Paint.Style.STROKE);
        paint.setFilterBitmap(true);
        float f6 = (float) n2 * 0.5f;
        Bitmap bitmap = Bitmap.createBitmap((int) n2, (int) n2, (Bitmap.Config) Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        SweepGradient sweepGradient = this.getColorSweepGradient((float) n2);
        int[] arrn = new int[]{Color.parseColor("#A0FFFFFF"), Color.parseColor("#B0FFFFFF"), Color.parseColor("#2BFFFFFF"), Color.parseColor("#08FFFFFF")};
        RadialGradient radialGradient = new RadialGradient(f6, f6, f6, arrn, new float[]{0.0f, 0.55f, 0.85f, 1.0f}, Shader.TileMode.MIRROR);
        paint.setShader((Shader) new ComposeShader((Shader) sweepGradient, (Shader) radialGradient, PorterDuff.Mode.SRC_ATOP));
        canvas.setDrawFilter((DrawFilter) new PaintFlagsDrawFilter(0, 3));
        canvas.drawArc(rectF, 0.0f, 360.0f, false, paint);
        return bitmap;
    }

    private SweepGradient getColorSweepGradient(float f2) {
        int[] arrn = new int[13];
        float[] arrf = new float[]{0.0f, 1.0f, 1.0f};
        for (int i2 = 0; i2 < 13; ++i2) {
            arrf[0] = (180 + i2 * 30) % 360;
            arrn[i2] = Color.HSVToColor((float[]) arrf);
        }
        arrn[12] = arrn[0];
        float f3 = f2 / 2.0f;
        return new SweepGradient(f3, f3, arrn, null);
    }

    private float mDegree(MotionEvent motionEvent) {
        float f2 = motionEvent.getX() - this.centerPoint.x;
        return (float) (180.0 + Math.toDegrees((double) Math.atan2((double) (motionEvent.getY() - this.centerPoint.y), (double) f2)));
    }

    Bitmap createIndicatorBitmap(int n2) {
        float f2 = n2 / 2;
        float f3 = f2 * 0.75f;
        int[] arrn = new int[]{Color.parseColor("#B0000000"), Color.parseColor("#00000000")};
        RadialGradient radialGradient = new RadialGradient(f2, f2, f3, arrn, new float[]{0.0f, 1.0f}, Shader.TileMode.MIRROR);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setFilterBitmap(true);
        paint.setShader((Shader) radialGradient);
        Bitmap bitmap = Bitmap.createBitmap((int) n2, (int) n2, (Bitmap.Config) Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawCircle(f2, f2, f3, paint);
        Paint paint2 = new Paint();
        paint2.setAntiAlias(true);
        paint2.setDither(true);
        paint2.setColor(-1);
        paint2.setStyle(Paint.Style.FILL);
        canvas.drawCircle(f2, f2, 0.5f * f2, paint2);
        return bitmap;
    }

    void drawColorWheel(Canvas canvas) {
        if (this.colorWheelBitmap == null) {
            this.colorWheelBitmap = this.createRotatingWheelBitmap((int) (0.875f * (float) this.getWidth()));
            Matrix matrix = this.matrix;
            float f2 = this.indicatorRadius;
            matrix.setTranslate(f2 * 0.5f, f2 * 0.5f);
        }
        canvas.drawBitmap(this.colorWheelBitmap, this.matrix, this.colorWheelPaint);
    }

    void drawDisplayBlock(Canvas canvas) {
        canvas.drawCircle(this.displayBlockPoint.x, this.displayBlockPoint.y, this.displayBlockPoint.y, this.displayPaint);
    }

    void drawIndicator(Canvas canvas) {
        if (this.indicatorBitmap == null) {
            this.indicatorBitmap = this.createIndicatorBitmap((int) (2.0f * this.indicatorRadius));
        }
        canvas.drawBitmap(this.indicatorBitmap, this.indicatorPoint.x - this.indicatorRadius, this.indicatorPoint.y - this.indicatorRadius, this.indicatorPaint);
    }

    boolean handleTouch(MotionEvent motionEvent, boolean bl) {
        float f2;
        float f3 = 0.875f * (float) this.getWidth();
        float f4 = 0.5f * f3;
        float f5 = f3 * 0.25f;
        float f6 = motionEvent.getX() - this.centerPoint.x;
        double d2 = Math.sqrt((double) (f6 * f6 + (f2 = motionEvent.getY() - this.centerPoint.y) * f2));
        if (d2 <= (double) f4 && d2 >= (double) f5) {
            this.indicatorPoint.x = motionEvent.getX();
            this.indicatorPoint.y = motionEvent.getY();
            float[] arrf = new float[]{0.0f, 1.0f, 1.0f};
            arrf[0] = this.mDegree(motionEvent);
            int n2 = Color.HSVToColor((float[]) arrf);
            this.displayPaint.setColor(n2);
            if (!bl || System.currentTimeMillis() - this.lastChangedTime > 100L) {
                OnColorChangedListener onColorChangedListener = this.listener;
                if (onColorChangedListener != null) {
                    onColorChangedListener.onChanged(n2);
                }
                this.lastChangedTime = System.currentTimeMillis();
            }
            this.invalidate();
            return true;
        }
        return false;
    }

    void init(Context context) {
        Paint paint;
        this.setLayerType(LAYER_TYPE_HARDWARE, null);
        this.paintFlagsDrawFilter = new PaintFlagsDrawFilter(0, 3);
        this.mDetector = new GestureDetector(context,  this);
        this.colorWheelPaint = new Paint();
        this.indicatorPaint = new Paint();
        this.centerPoint = new PointF(0.0f, 0.0f);
        this.indicatorPoint = new PointF(0.0f, 0.0f);
        this.displayBlockPoint = new PointF(0.0f, 0.0f);
        this.displayPaint = paint = new Paint();
        paint.setAntiAlias(true);
        this.displayPaint.setColor(-65536);
    }

    public boolean onDown(MotionEvent motionEvent) {
        return this.handleTouch(motionEvent, false);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.setDrawFilter(this.paintFlagsDrawFilter);
        this.drawColorWheel(canvas);
        this.drawIndicator(canvas);
        this.drawDisplayBlock(canvas);
    }

    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f2, float f3) {
        return this.handleTouch(motionEvent2, false);
    }

    public void onLongPress(MotionEvent motionEvent) {
    }

    protected void onMeasure(int n2, int n3) {
        int n4;
        int n5 = MeasureSpec.getSize((int) n2);
        if (n5 < (n4 = MeasureSpec.getSize((int) n3))) {
            this.setMeasuredDimension(n5, n5);
            return;
        }
        this.setMeasuredDimension(n4, n4);
    }

    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f2, float f3) {
        return this.handleTouch(motionEvent2, true);
    }

    public void onShowPress(MotionEvent motionEvent) {
    }

    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return this.handleTouch(motionEvent, false);
    }

    protected void onSizeChanged(int n2, int n3, int n4, int n5) {
        super.onSizeChanged(n2, n3, n4, n5);
        PointF pointF = this.centerPoint;
        float f2 = n2;
        pointF.x = f2 * 0.5f;
        this.centerPoint.y = 0.5f * (float) n3;
        this.indicatorRadius = 0.125f * f2;
        this.displayBlockPoint.y = 0.05f * f2;
        PointF pointF2 = this.displayBlockPoint;
        pointF2.x = f2 - pointF2.y;
        this.switchBtnWidth = (int) (0.3 * (double) n2);
        float f3 = f2 * 0.328f;
        PointF pointF3 = this.indicatorPoint;
        double d2 = this.centerPoint.x;
        pointF3.x = (float) (d2 + (double) f3 * Math.cos((double) 3.141592653589793));
        this.indicatorPoint.y = (float) ((double) this.centerPoint.y + (double) f3 * Math.sin((double) 3.141592653589793));
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        return this.mDetector.onTouchEvent(motionEvent);
    }

    public void setColor(int n2) {
        float[] arrf = new float[]{0.0f, 1.0f, 1.0f};
        Color.colorToHSV(n2, arrf);
        float f2 = 0.328f * (float) this.getWidth();
        float f3 = 180.0f + arrf[0];
        PointF pointF = this.indicatorPoint;
        double d2 = this.centerPoint.x;
        pointF.x = (float) (d2 + (double) f2 * Math.cos(Math.toRadians(f3)));
        this.indicatorPoint.y = (float) ((double) this.centerPoint.y + (double) f2 * Math.sin(Math.toRadians(f3)));
        this.displayPaint.setColor(n2);
        this.invalidate();
    }

    public int getColor(){
        return displayPaint.getColor();
    }

    public void setListener(OnColorChangedListener onColorChangedListener) {
        this.listener = onColorChangedListener;
    }

    public static interface OnColorChangedListener {
        public void onChanged(int color);
    }

}

