package com.example.fredrik.supersudoku;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceView;

/**
 * Created by fredrik on 20.12.16.
 */

public class ControlSurfaceView extends SurfaceView {

    Paint paint = new Paint(Paint.LINEAR_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
    Canvas canvas;

    int width, squareWidth;

    final GestureDetector gestureDetector;

    public ControlSurfaceView(Context context) {
        super(context);

        this.setBackgroundColor(ContextCompat.getColor(context, R.color.colorSecondary));

        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            public void onLongPress(MotionEvent e) { onLongClick(e); }
            public boolean onSingleTapConfirmed(MotionEvent e) { return onClick(e); }
        });
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.canvas = canvas;

        width = canvas.getWidth();
        squareWidth = width / 9;

        drawNumberButtons();
        drawControlButtons();
    }

    private void drawNumberButtons() {
        paint.setColor(Color.WHITE);
        paint.setTextSize(70);
        for (int i=1; i<=9; i++) {
            Rect rect = getRect(i - 1, 0);
            drawCenteredText(Integer.toString(i), rect, paint, canvas);
        }
    }

    private void drawControlButtons() {
        paint.setColor(Color.WHITE);
        paint.setTextSize(70);

        drawCenteredText("↶", getRect(0, 1), paint, canvas);
        drawCenteredText("❌", getRect(1, 1), paint, canvas);

        drawCenteredText("✎", getRect(8, 1), paint, canvas);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return true;
    }

    public boolean onClick(MotionEvent e) {
        if (isEnabled()) {
            invalidate();
        }
        return true;
    }

    public void onLongClick(MotionEvent e) {
        if (isEnabled()) {
        }
    }

    private Rect getRect(int i, int j) {
        return new Rect(i*squareWidth, 5 + j*squareWidth, (i + 1)*squareWidth, 5 + (j + 1)*squareWidth);
    }

    static void drawCenteredText(String text, Rect areaRect, Paint mPaint, Canvas mCanvas) {
        RectF bounds = new RectF(areaRect);
        // measure text width
        bounds.right = mPaint.measureText(text, 0, text.length());
        // measure text height
        bounds.bottom = mPaint.descent() - mPaint.ascent();

        bounds.left += (areaRect.width() - bounds.right) / 2.0f;
        bounds.top += (areaRect.height() - bounds.bottom) / 2.0f;

        mCanvas.drawText(text, bounds.left, bounds.top - mPaint.ascent(), mPaint);
    }
}
