package com.application.drawable;

import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.util.Log;

/**
 * Created by davide on 03/04/14.
 */
public class TextDrawable extends Drawable {

    private static final String TAG = "ggg";
    private final String text;
    private final Paint textPaint;
    private final Paint circlePaint;
    private final int circleSize;

    public TextDrawable(String text, int color, int size) {

        this.text = text;

        this.textPaint = new Paint();
        textPaint .setColor(Color.BLACK);
        textPaint .setTextSize(50);


//        Rect bounds = new Rect();
//        textPaint.getTextBounds(text, 0, text.length(), bounds);


        this.circleSize = size;
        this.circlePaint = new Paint();
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(color);
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

    }

    @Override
    public void draw(Canvas canvas) {
        Paint.FontMetrics fm = new Paint.FontMetrics();
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.getFontMetrics(fm);
/*        textPaint .setAntiAlias(true);
        textPaint .setFakeBoldText(true);
        textPaint .setShadowLayer(6f, 0, 0, Color.BLACK);*/

        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(circleSize, circleSize, circleSize, circlePaint);
        Log.d(TAG, "text" + text);
        canvas.drawText(text, 0, 0, textPaint);

    }

    @Override
    public void setAlpha(int alpha) {
        textPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        textPaint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}