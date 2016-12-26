package com.example.fredrik.supersudoku;

import com.example.fredrik.supersudoku.customs.EventListener;
import com.example.fredrik.supersudoku.sudokulogic.Board;
import com.example.fredrik.supersudoku.sudokulogic.MarkMode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

class SudokuMain {

    private MainActivity mainActivity;

    Board board;
    MarkMode markMode = MarkMode.FILL;
    int selectedNumber;

    private List<EventListener> eventListeners;

    SudokuMain(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        eventListeners = new ArrayList<>();

        board = new Board();

        parseSudokuTxtFile();
    }

    void newGame(int difficulty) {
        board.newGame(difficulty);
    }

    void setNumber(int i, int j) {
        switch (markMode) {
            case CLEAR:
                board.clearSquare(Board.key(i, j));
                break;
            case FILL:
                if (selectedNumber != 0) {
                    board.setFillFromUser(Board.key(i, j), selectedNumber);
                }
                break;
            case CANDIDATE:
                if (selectedNumber != 0) {
                    board.setCandidateFromUser(Board.key(i, j), selectedNumber);
                }
                break;
            default:
        }
        board.changeOccurred();
    }

    void parseSudokuTxtFile() {
        List<InputStream> iss = new ArrayList<>();
        iss.add(mainActivity.getResources().openRawResource(R.raw.super_easy_sudokus));
        iss.add(mainActivity.getResources().openRawResource(R.raw.very_easy_sudokus));
        iss.add(mainActivity.getResources().openRawResource(R.raw.easy_sudokus));
        iss.add(mainActivity.getResources().openRawResource(R.raw.medium_sudokus));
        iss.add(mainActivity.getResources().openRawResource(R.raw.hard_sudokus));

        for (int i=0; i<iss.size(); i++) {
            BufferedReader in = new BufferedReader(new InputStreamReader(iss.get(i)));
            String line;
            try {
                board.stringSudokus.add(new ArrayList<>());
                while ((line = in.readLine()) != null) {
                    board.stringSudokus.get(i).add(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
