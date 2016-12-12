package com.example.fredrik.sudoku;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

public class SudokuMain extends AppCompatActivity {

    LinearLayout mainLayout;
    SudokuSurfaceView sudokuSurfaceView;

    ToggleButton fillToggleButton;
    ToggleButton markToggleButton;
    ToggleButton[] modeToggleButtons;

    ToggleButton toggleButton1;
    ToggleButton toggleButton2;
    ToggleButton toggleButton3;
    ToggleButton toggleButton4;
    ToggleButton toggleButton5;
    ToggleButton toggleButton6;
    ToggleButton toggleButton7;
    ToggleButton toggleButton8;
    ToggleButton toggleButton9;
    ToggleButton[] numberToggleButtons;

    SudokuBoard sudokuBoard;
    SudokuMode sudokuMode;
    int selectedNumber;
    int highlightNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sudoku_main);

        mainLayout = (LinearLayout) findViewById(R.id.mainLayout);

        sudokuSurfaceView = new SudokuSurfaceView(this);

        mainLayout.addView(sudokuSurfaceView, 0);

        initToggleButtons();

        sudokuBoard = new SudokuBoard();
    }

    private void initToggleButtons() {
        fillToggleButton = (ToggleButton) findViewById(R.id.fillToggleButton);
        markToggleButton = (ToggleButton) findViewById(R.id.markToggleButton);

        toggleButton1 = (ToggleButton) findViewById(R.id.toggleButton1);
        toggleButton2 = (ToggleButton) findViewById(R.id.toggleButton2);
        toggleButton3 = (ToggleButton) findViewById(R.id.toggleButton3);
        toggleButton4 = (ToggleButton) findViewById(R.id.toggleButton4);
        toggleButton5 = (ToggleButton) findViewById(R.id.toggleButton5);
        toggleButton6 = (ToggleButton) findViewById(R.id.toggleButton6);
        toggleButton7 = (ToggleButton) findViewById(R.id.toggleButton7);
        toggleButton8 = (ToggleButton) findViewById(R.id.toggleButton8);
        toggleButton9 = (ToggleButton) findViewById(R.id.toggleButton9);

        modeToggleButtons = new ToggleButton[]{fillToggleButton, markToggleButton};
        numberToggleButtons = new ToggleButton[]{toggleButton1, toggleButton2,
                toggleButton3, toggleButton4, toggleButton5, toggleButton6, toggleButton7,
                toggleButton8, toggleButton9};
        makeToggleButtonsExclusive();
    }

    private void makeToggleButtonsExclusive() {
        CompoundButton.OnCheckedChangeListener modeChangeChecker = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sudokuMode = SudokuMode.parseText(buttonView.getText().toString());
                    for (ToggleButton tb : modeToggleButtons) {
                        if (tb != buttonView)
                            tb.setChecked(false);
                    }
                }
            }
        };
        for (ToggleButton tb : modeToggleButtons) {
            tb.setOnCheckedChangeListener(modeChangeChecker);
        }
        CompoundButton.OnCheckedChangeListener numberChangeChecker = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    selectedNumber = Integer.parseInt(buttonView.getText().toString());
                    for (ToggleButton tb : numberToggleButtons) {
                        if (tb != buttonView)
                            tb.setChecked(false);
                    }
                }
            }
        };
        for (ToggleButton tb : numberToggleButtons) {
            tb.setOnCheckedChangeListener(numberChangeChecker);
            // TODO: try using lambda
            tb.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    highlightNumber = Integer.parseInt(((ToggleButton) v).getTextOff().toString());
                    return false;
                }
            });
        }
    }
}

