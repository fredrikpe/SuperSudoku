package com.example.fredrik.supersudoku;

import com.example.fredrik.supersudoku.sudokulogic.Board;
import com.example.fredrik.supersudoku.sudokulogic.MarkMode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

class SudokuMain {

    private MainActivity mainActivity;

    Board board;
    MarkMode markMode;
    int selectedNumber;
    int highlightNumber;

    boolean useAssistant = true;

    SudokuMain(MainActivity mainActivity) {
        this.mainActivity = mainActivity;

        board = new Board();

        parseSudokuTxtFile();

        newGame();
    }

    void newGame() {
        board.newGame();
    }

    void setNumber(int i, int j) {
        if (selectedNumber != 0) {
            switch (markMode) {
                case NONE:
                    break;
                case FILL:
                    board.setFillFromUser(Board.key(i, j), selectedNumber);
                    break;
                case CANDIDATE:
                    board.setCandidateFromUser(Board.key(i, j), selectedNumber);
                    break;
                default:
            }
            board.changeOccurred();
        }
    }

    void parseSudokuTxtFile() {
        InputStream is = mainActivity.getResources().openRawResource(R.raw.hard_sudokus);
        BufferedReader in = new BufferedReader(new InputStreamReader(is));

        String line;
        try {
            while ((line = in.readLine()) != null) {
                board.stringSudokus.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
