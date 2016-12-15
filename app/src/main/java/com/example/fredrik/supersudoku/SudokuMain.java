package com.example.fredrik.supersudoku;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

class SudokuMain {

    private MainActivity mainActivity;

    SudokuBoard sudokuBoard;
    SudokuMode sudokuMode;
    int selectedNumber;
    int highlightNumber;

    boolean useAssistant = true;

    SudokuMain(MainActivity mainActivity) {
        this.mainActivity = mainActivity;

        sudokuBoard = new SudokuBoard();

        parseSudokuTxtFile();

        newGame();
    }

    void newGame() {
        try {
            sudokuBoard.getNewRandomSudoku();
        } catch (Exception e) {
            e.printStackTrace();
        }
        sudokuBoard.changeOccurred();
    }

    void setNumber(int i, int j) {
        if (selectedNumber != 0) {
            switch (sudokuMode) {
                case FILL:
                    sudokuBoard.setFill(SudokuBoard.key(i, j), selectedNumber);
                    break;
                case MARK:
                    sudokuBoard.setMarkFromUser(SudokuBoard.key(i, j), selectedNumber);
                    break;
                default:
            }
            sudokuBoard.changeOccurred();
        }
    }

    void parseSudokuTxtFile() {
        InputStream is = mainActivity.getResources().openRawResource(R.raw.hard_sudokus);
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
