package com.example.fredrik.sudoku;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.view.MotionEvent;
import android.view.SurfaceView;

/**
 * Created by fredrik on 11.12.16.
 */

public class SudokuSurfaceView extends SurfaceView {

    int width, height, squareWidth, squareHeight;
    float markTextSize = 23;
    float fillTextSize = 100;
    //Color highlightColor = Color.LTGRAY;

    Paint paint = new Paint();

    public SudokuSurfaceView(Context context) {
        super(context);
        this.setBackgroundColor(Color.WHITE);
        this.getHolder().setFixedSize(0, 850);
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
        this.drawMarks(canvas);
//        this.drawFills(canvas);
    }

    private void drawMarks(Canvas canvas) {
        SudokuBoard sudokuBoard = ((SudokuMain) getContext()).sudokuBoard;

        paint.setTextSize(markTextSize);
        int sx, sy, mx, my;
        for (Square square : sudokuBoard.squares) {
            sx = square.i * squareWidth;
            sy = square.j * squareHeight;
            for (int mark : square.marks) {
                mx = sx + ((mark - 1) % 3) * squareWidth/3 + 10;
                my = sy + (mark - 1)/3 * squareHeight/3 + 25;
                canvas.drawText(Integer.toString(mark), mx, my, paint);
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
        int highlight = ((SudokuMain) getContext()).highlightNumber;
        if (highlight == 0) {
            return;
        }
        paint.setColor(Color.LTGRAY);
        paint.setStyle(Paint.Style.FILL);
        for (Square square : ((SudokuMain) getContext()).sudokuBoard.squares) {
            if (square.fill == highlight || square.marks.contains(highlight)) {
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
            int x = (int)touched_x / squareWidth;
            int y = (int)touched_y / squareHeight;

            SudokuMode sudokuMode = ((SudokuMain) getContext()).sudokuMode;
            Square square = ((SudokuMain) getContext()).sudokuBoard.getSquare(x, y);
            int selectedNumber = ((SudokuMain) getContext()).selectedNumber;
            if (selectedNumber != 0) {
                switch (sudokuMode) {
                    case FILL:
                        square.fill = selectedNumber;
                        break;
                    case MARK:
                        if (square.marks.contains(selectedNumber)) {
                            square.marks.remove(Integer.valueOf(selectedNumber));
                        } else {
                            square.marks.add(selectedNumber);
                        }
                        break;
                    default:
                }
            }
        }
        invalidate();
        return true; //processed
    }
}
