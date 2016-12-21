package com.example.fredrik.supersudoku;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceView;

import com.example.fredrik.supersudoku.asdflaksd.EventListener;
import com.example.fredrik.supersudoku.sudokulogic.Hint;
import com.example.fredrik.supersudoku.sudokulogic.MarkMode;

/**
 * Created by fredrik on 20.12.16.
 */

public class ControlSurfaceView extends SurfaceView implements EventListener {

    Paint paint = new Paint(Paint.LINEAR_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
    Paint tmpPaint = new Paint(Paint.LINEAR_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
    Canvas canvas;
    Bitmap tmpBitmap;
    Canvas tmpCanvas;

    int width, squareWidth;
    SudokuMain sudokuMain;
    Hint hint;
    SudokuSurfaceView sudokuSurfaceView;

    final GestureDetector gestureDetector;

    public ControlSurfaceView(Context context, SudokuSurfaceView ssv) {
        super(context);
        this.sudokuSurfaceView =  ssv;
        sudokuMain = ((MainActivity) getContext()).sudokuMain;

        sudokuMain.board.addEventListener(this);

        this.setBackgroundColor(ContextCompat.getColor(context, R.color.colorSecondary));

        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            public void onLongPress(MotionEvent e) { onLongClick(e); }
            public boolean onSingleTapConfirmed(MotionEvent e) { return onClick(e); }
            public boolean onDown(MotionEvent e) { return onDownClick(e); }

        });
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        tmpBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        tmpCanvas = new Canvas(tmpBitmap);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.canvas = canvas;

        width = canvas.getWidth();
        squareWidth = width / 7;

        canvas.drawBitmap(tmpBitmap, 0, 0, tmpPaint);

        drawNumberButtons();
        drawControlButtons();
        hint = null;
    }

    private void drawNumberButtons() {
        paint.setTextSize(70);
        for (int i=1; i<=9; i++) {
            Rect rect = getRect((i - 1) % 3 + 2, (i - 1)/3);
            if (i == sudokuMain.selectedNumber) {
                paint.setColor(ContextCompat.getColor(getContext(), R.color.colorSecondaryDark));
                paint.setStyle(Paint.Style.FILL);
                drawCenteredCircle(rect, paint, canvas);
            }
            paint.setColor(Color.WHITE);
            drawCenteredText(Integer.toString(i), rect, paint, canvas);
        }
    }

    private void drawControlButtons() {
        paint.setColor(Color.WHITE);
        paint.setTextSize(70);

        drawCenteredText("↶", getRect(1, 0), paint, canvas);

        if (sudokuMain.markMode == MarkMode.CLEAR) {
            paint.setColor(ContextCompat.getColor(getContext(), R.color.colorSecondaryDark));
            drawCenteredCircle(getRect(1, 1), paint, canvas);
            paint.setColor(Color.WHITE);
        }
        drawCenteredText("❌", getRect(1, 1), paint, canvas);

        drawCenteredText("?", getRect(5, 1), paint, canvas);

        if (sudokuMain.markMode == MarkMode.CANDIDATE) {
            paint.setColor(ContextCompat.getColor(getContext(), R.color.colorSecondaryDark));
            drawCenteredCircle(getRect(5, 0), paint, canvas);
            paint.setColor(Color.WHITE);
        }
        drawCenteredText("✎", getRect(5, 0), paint, canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return true;
    }

    public boolean onClick(MotionEvent e) {
        if (isEnabled()) {
            tmpCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            int i = (int) e.getY() / squareWidth;
            int j = (int) e.getX() / squareWidth;

            if (j > 1 && j < 5) {
                // Number pressed
                int number = 3*i + j - 1;
                sudokuMain.selectedNumber = sudokuMain.selectedNumber == number ? 0 : number;
            } else if (j == 1 || j == 5) {
                // Control clicked
                onClickControl(i, j);
            }
            invalidate();
            sudokuSurfaceView.invalidate();
        }
        return true;
    }

    private void onClickControl(int i, int j) {
        if (j == 1) {
            if (i == 0) {
                sudokuMain.board.undo();
            } else if (i == 1) {
                sudokuMain.markMode = sudokuMain.markMode != MarkMode.CLEAR ? MarkMode.CLEAR : MarkMode.FILL;
            }
        } else if (j == 5) {
            if (i == 0) {
                sudokuMain.markMode = sudokuMain.markMode != MarkMode.CANDIDATE ? MarkMode.CANDIDATE : MarkMode.FILL;
            } else if (i == 1) {
                sudokuMain.board.hint();
            }
        }
    }

    public void onLongClick(MotionEvent e) {
        tmpCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        if (isEnabled()) {
        }
    }

    public boolean onDownClick(MotionEvent e) {
//        int j = (int) e.getY() / squareWidth;
//        int i = (int) e.getX() / squareWidth;
//        tmpPaint.setColor(Color.WHITE);
//        tmpPaint.setAlpha(80);
//        drawCenteredCircle(getRect(i, j), tmpPaint, tmpCanvas);
        return true;
    }

    private Rect getRect(int i, int j) {
        return new Rect(i*squareWidth, j*squareWidth, (i + 1)*squareWidth, (j + 1)*squareWidth);
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

    static void drawCenteredCircle(Rect areaRect, Paint mPaint, Canvas mCanvas) {
        RectF rectf = new RectF(areaRect);
        mCanvas.drawRoundRect(rectf, 30, 30, mPaint);
    }

    @Override
    public void onChangeEvent() {
    }

    @Override
    public void onHintFoundEvent(Hint hint) {
        this.hint = hint;
        sudokuMain.markMode = hint.remove_candidate ? MarkMode.CANDIDATE : MarkMode.FILL;
        sudokuMain.selectedNumber = hint.number;
        invalidate();
    }
}
