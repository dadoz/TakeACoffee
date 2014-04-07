package com.application.drawable;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.widget.ImageView;

/**
 * Created by davide on 04/04/14.
 */
public class TextIconCustom  extends ImageView {
    private static final String TAG = "imageView";
    private Paint textPaint, circlePaint;
    private int textSize, textColor, circleColor, circleSize;
    private String textContent;

    public TextIconCustom(Context context, int textSize, int textColor, String textContent, int circleSize, int circleColor) {
        super(context);
        this.textColor = textColor;
        this.textContent = textContent;
        this.textSize = textSize;
        this.circleColor = circleColor;
        this.circleSize = circleSize;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        circlePaint = new Paint();
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(circleColor);
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(circleSize, circleSize, circleSize, circlePaint);


        textPaint = new Paint();
        // draw some text using FILL style
        textPaint.setStyle(Paint.Style.FILL);
        //turn antialiasing on
        textPaint.setColor(textColor);
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(textSize);
//            canvas.drawText(textContent, 50, 100, textPaint);
        canvas.drawText(textContent, circleSize - (circleSize / 4f), circleSize + (circleSize / 4f), textPaint);
//            x, y + (mRect.height() / 2f)
    }

}
