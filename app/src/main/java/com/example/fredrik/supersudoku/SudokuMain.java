package com.example.fredrik.supersudoku;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

class SudokuMain {

    private MainActivity mainActivity;

    SudokuBoard sudokuBoard;
    SudokuMode sudokuMode;
    SudokuAssistant sudokuAssistant;
    int selectedNumber;
    int highlightNumber;

    boolean useAssistant = true;

    SudokuMain(MainActivity mainActivity) {
        this.mainActivity = mainActivity;

        sudokuBoard = new SudokuBoard();
        sudokuAssistant = new SudokuAssistant(sudokuBoard);

        parseSudokuTxtFile();

        newGame();
    }

    void newGame() {
        sudokuBoard.getNewRandomSudoku();
        sudokuAssistant.interrupt();
        sudokuAssistant = new SudokuAssistant(sudokuBoard);
        sudokuAssistant.start();
        sudokuBoard.changeOccured = true;
        sudokuBoard.notifyChange();
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
        InputStream is = mainActivity.getResources().openRawResource(R.raw.test_sudokus);
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
