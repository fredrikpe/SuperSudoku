package com.example.fredrik.supersudoku.asdflaksd;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.Button;

import com.example.fredrik.supersudoku.MainActivity;
import com.example.fredrik.supersudoku.R;

/**
 * Created by fred on 12/25/16.
 */

public class PadButton extends Button {

    public int color = ContextCompat.getColor(getContext(), R.color.colorSecondary);

    public PadButton(Context context) {
        super(context);
    }

    public PadButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PadButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        getBackground().setColorFilter(color, PorterDuff.Mode.MULTIPLY);
    }
}

