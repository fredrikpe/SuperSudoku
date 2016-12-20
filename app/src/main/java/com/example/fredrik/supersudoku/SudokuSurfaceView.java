package com.example.fredrik.supersudoku;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewConfiguration;

import com.example.fredrik.supersudoku.asdflaksd.Array;
import com.example.fredrik.supersudoku.asdflaksd.EventListener;
import com.example.fredrik.supersudoku.sudokulogic.Hint;
import com.example.fredrik.supersudoku.sudokulogic.Square;
import com.example.fredrik.supersudoku.sudokulogic.Board;

import java.util.Map;
import java.util.stream.IntStream;

/**
 * Created by fredrik on 11.12.16.
 */

public class SudokuSurfaceView extends SurfaceView implements EventListener {

    int width, height, squareWidth, squareHeight;
    float markTextSize = 23;
    float fillTextSize = 100;

    Paint paint = new Paint();
    Canvas canvas;

    final GestureDetector gestureDetector;

    SudokuMain sudokuMain;
    int[] squareColors;

    SharedPreferences sharedPreferences;

    public SudokuSurfaceView(Context context) {
        super(context);
        sudokuMain = ((MainActivity) getContext()).sudokuMain;
        squareColors = new int[81];
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        this.setBackgroundColor(Color.WHITE);
        this.getHolder().setFixedSize(100, 850);

        sudokuMain.board.addEventListener(this);

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
        height = canvas.getHeight();
        squareWidth = width / 9;
        squareHeight = height / 9;


        this.drawColoredSquares();
        this.drawBoard();
        this.drawFillsAndMarks();
        sudokuMain.board.hint = null;
    }


    private void drawFillsAndMarks() {
        int sx, sy, mx, my;
        for (Map.Entry<Integer, Square> entry : sudokuMain.board.squareMap.entrySet()) {
            Square square = entry.getValue();

            sx = squareXPos(entry.getKey());
            sy = squareYPos(entry.getKey());

            if (square.fill != 0) {
                if (!square.editable) {
                    paint.setColor(Color.BLACK);
                } else {
                    int color = ContextCompat.getColor(getContext(), R.color.colorAccent);
                    paint.setColor(color);
                }
                paint.setTextSize(fillTextSize);
                sx = sx + squareWidth / 4;
                sy = sy + squareHeight - 10;
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

    private void drawBoard() {
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

    private void drawColoredSquares() {
        int highlight = sudokuMain.highlightNumber;
        if (sudokuMain.board.hint != null) {
            highlight = sudokuMain.board.hint.number;
        }

        int color;
        for (Map.Entry<Integer, Square> entry : sudokuMain.board.squareMap.entrySet()) {
            if (!drawHighlight(entry.getValue(), entry.getKey(), highlight)) {
                color = getColor(squareColors[entry.getKey()]);
                drawColoredSquare(entry.getKey(), color);
            }
        }
    }

    private boolean drawHighlight(Square square, Integer key, int highlight) {
        if (MainActivity.sharedPreferences.getBoolean("highlights", true)) {
            // Enabled in highlights
            if (highlight != 0 && square.fill == highlight ||
                    (square.fill == 0 && Array.contains(square.candidates, highlight)) &&
                            squareColors[key] == 0) {
                // Number is highlighted and not colored
                int color = ContextCompat.getColor(getContext(), R.color.colorPrimary);
                drawColoredSquare(key, color);
                return true;
            }
        }
        return false;
    }

    private void drawColoredSquare(Integer key, int color) {
        paint.setStyle(Paint.Style.FILL);
        int x = squareXPos(key);
        int y = squareYPos(key);

        if (sudokuMain.board.hint != null) {
            if (Array.contains(sudokuMain.board.hint.container, key)) {
                color = darkenColor(color);
            }
        }
        paint.setColor(color);
        canvas.drawRect(x, y, x + squareWidth, y + squareHeight, paint);

        if (sudokuMain.board.hint != null) {
            if (key.equals(sudokuMain.board.hint.key)) {
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(10);
                paint.setColor(ContextCompat.getColor(getContext(), R.color.colorRedbox));
                canvas.drawRect(x + 2, y + 2, x + squareWidth - 2, y + squareHeight - 2, paint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return true;
    }

    public boolean onClick(MotionEvent e) {
        if (isEnabled()) {
            int i = (int) e.getY() / squareHeight;
            int j = (int) e.getX() / squareWidth;
            sudokuMain.setNumber(i, j);
            invalidate();
        }
        return true;
    }

    public void onLongClick(MotionEvent e) {
        if (isEnabled()) {
            int i = (int) e.getY() / squareHeight;
            int j = (int) e.getX() / squareWidth;
            selectColoring(i, j);
        }
    }

    private boolean selectColoring(int i, int j) {
        CharSequence colors[] = new CharSequence[] {"Red", "Green", "Yellow", "Remove color", "Remove all colors"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Color square");
        builder.setItems(colors, (dialog, which) -> {
            if (which == 4) {
                squareColors = new int[81];
            } else if (which == 3) {
                squareColors[Board.key(i, j)] = 0;
            } else {
                squareColors[Board.key(i, j)] = which + 1;
            }
            invalidate();
        });
        builder.show();
        return true;
    }

    private int getColor(int i) {
        switch (i) {
            case 0: return Color.WHITE;
            case 1: return Color.RED;
            case 2: return Color.GREEN;
            case 3: return Color.YELLOW;
        }
        return Color.WHITE;
    }

    private int darkenColor(int color) {
        float r = (color & 0xFF0000) >> 16;
        float g = (color & 0xFF00) >> 8;
        float b = (color & 0xFF);

        double scalar = 0.8;
        return Color.rgb((int)(r * scalar), (int)(g * scalar), (int)(b * scalar));
    }

    @Override
    public void onChangeEvent() {
        invalidate();
    }

    @Override
    public void onHintFoundEvent(int number) {
        invalidate();
    }

    private int squareYPos(Integer key) { return Board.rowIndex(key) * squareHeight; }
    private int squareXPos(Integer key) { return Board.columnIndex(key) * squareWidth; }
}
