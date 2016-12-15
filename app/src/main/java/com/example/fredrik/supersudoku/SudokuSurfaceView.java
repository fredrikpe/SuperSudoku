package com.example.fredrik.supersudoku;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;
import android.view.SurfaceView;

/**
 * Created by fredrik on 11.12.16.
 */

public class SudokuSurfaceView extends SurfaceView implements SudokuBoard.SomeEventListener{

    int width, height, squareWidth, squareHeight;
    float markTextSize = 23;
    float fillTextSize = 100;

    Paint paint = new Paint();

    SudokuMain sudokuMain;

    public SudokuSurfaceView(Context context) {
        super(context);
        sudokuMain = ((MainActivity) getContext()).sudokuMain;

        this.setBackgroundColor(Color.WHITE);
        this.getHolder().setFixedSize(100, 850);

        sudokuMain.sudokuBoard.setSomeEventListener(this);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        width = canvas.getWidth();
        height = canvas.getHeight();
        squareWidth = width / 9;
        squareHeight = height / 9;


        this.drawHighlights(canvas);
        this.drawBoard(canvas);
        this.drawFillsAndMarks(canvas);
    }

    private void drawFillsAndMarks(Canvas canvas) {
        int sx, sy, mx, my;
        for (Square square : sudokuMain.sudokuBoard.squareMap.values()) {
            sx = square.i * squareWidth;
            sy = square.j * squareHeight;
            if (square.fill != 0) {
                if (!square.editable) {
                    paint.setColor(Color.BLACK);
                } else {
                    paint.setColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                }
                paint.setTextSize(fillTextSize);
                sx = square.i * squareWidth + squareWidth / 4;
                sy = (square.j + 1) * squareHeight - 10;
                canvas.drawText(Integer.toString(square.fill), sx, sy, paint);
            } else {
                paint.setColor(Color.BLACK);
                paint.setTextSize(markTextSize);
                for (int mark : square.candidates) {
                    mx = sx + ((mark - 1) % 3) * squareWidth / 3 + 10;
                    my = sy + (mark - 1) / 3 * squareHeight / 3 + 25;
                    canvas.drawText(Integer.toString(mark), mx, my, paint);
                }
            }
        }
    }

    private void drawBoard(Canvas canvas) {
        int startH, startV;
        paint.setColor(Color.BLACK);
        for (int i=0; i<=9; i++) {
            if (i % 3 == 0)
                paint.setStrokeWidth(10);
            else
                paint.setStrokeWidth(6);

            startH = i * squareHeight;
            startV = i * squareWidth;

            canvas.drawLine(0, startH, width, startH, paint);
            canvas.drawLine(startV, 0, startV, height, paint);
        }
    }

    private void drawHighlights(Canvas canvas) {
        int highlight = sudokuMain.highlightNumber;
        if (highlight == 0) {
            return;
        }
        paint.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
        paint.setStyle(Paint.Style.FILL);
        for (Square square : sudokuMain.sudokuBoard.squareMap.values()) {
            if (square.fill == highlight || (square.fill == 0 && square.candidatesContains(highlight))) {
                int x = square.i * squareWidth;
                int y = square.j * squareHeight;
                canvas.drawRect(x, y, x + squareWidth, y + squareHeight, paint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!this.isEnabled()) {
            return true;
        }
        float touched_x = event.getX();
        float touched_y = event.getY();

        if (event.getAction() == MotionEvent.ACTION_UP) {
            int x = (int) touched_x / squareWidth;
            int y = (int) touched_y / squareHeight;

            sudokuMain.setNumber(x, y);
        }
        invalidate();
        return true; //processed
    }

    @Override
    public void onSomeEvent() {
        invalidate();
    }
}
