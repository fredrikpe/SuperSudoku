package com.example.fredrik.supersudoku;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

class SudokuMain {

    private Context context;

    SudokuBoard sudokuBoard;
    SudokuMode sudokuMode;
    int selectedNumber;
    int highlightNumber;

    SudokuMain(Context context) {
        this.context = context;

        sudokuBoard = new SudokuBoard();

        parseSudokuTxtFile();

        newGame();
    }

    void newGame() {
        sudokuBoard.getNewRandomSudoku();
        SudokuAssistant.initiateMarks(sudokuBoard);
    }

    void setNumber(int i, int j) {
        if (selectedNumber != 0) {
            switch (sudokuMode) {
                case FILL:
                    sudokuBoard.setFill(i, j, selectedNumber);
                    break;
                case MARK:
                    sudokuBoard.setMark(i, j, selectedNumber);
                    break;
                default:
            }
        }
    }

    void parseSudokuTxtFile() {
        InputStream is = context.getResources().openRawResource(R.raw.test_sudokus);
        BufferedReader in = new BufferedReader(new InputStreamReader(is));

        String line;
        try {
            while ((line = in.readLine()) != null) {
                sudokuBoard.stringSudokus.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
