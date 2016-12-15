package com.example.fredrik.supersudoku;

import android.os.AsyncTask;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by fredrik on 12.12.16.
 */

class SudokuAssistantTask extends AsyncTask<SudokuBoard, Integer, Boolean> {

    SudokuBoard sudokuBoard;

    @Override
    protected Boolean doInBackground(SudokuBoard... objects) {
        System.out.println("Doing in background");

        sudokuBoard = objects[0];
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        updateMarks(objects[0]);
        return singleCandidate(objects[0]);
    }

    @Override
    protected void onPostExecute(Boolean result) {
        sudokuBoard.assistantFinished(result);
    }


    private boolean singleCandidate(SudokuBoard sudokuBoard) {
        for (Square square : sudokuBoard.squareMap.values()) {
            if (square.marks.length == 1 && square.fill == 0) {
                if (!square.editable) {
                    System.out.println("Error. Not editable and fill == 0");
                }
                sudokuBoard.setFill(SudokuBoard.key(square.i, square.j), square.marks[0]);
                //System.out.println("Found single candidate " + square.marks[0] + " at " + square.i + ", " + square.j);
                return true;
            }
        }
        return false;
    }

    private void updateMarks(SudokuBoard sudokuBoard) {
        for (Square square : sudokuBoard.squareMap.values()) {
            if (square.editable) {
                Integer key = SudokuBoard.key(square.i, square.j);

                boolean[] validMarks = getValidMarks(sudokuBoard, key);
                for (int mark = 1; mark <= 9; mark++) {
                    if (validMarks[mark - 1] &&!square.containsMark(mark)) {
                        sudokuBoard.setMarkFromAssistant(key, mark);
                    } else if (!validMarks[mark - 1] && square.containsMark(mark)) {
                        sudokuBoard.setMarkFromAssistant(key, mark);
                    }
                }
            }
        }
    }

    private boolean[] getValidMarks(SudokuBoard sudokuBoard, Integer key) {
        boolean[] validMarks = new boolean[] {true, true, true, true, true, true, true, true, true};

        if (sudokuBoard.getConnectedSquares(key).size() != 24) System.out.println("CS != 24, CS = " + sudokuBoard.getConnectedSquares(key).size());
        for(Integer k : sudokuBoard.getConnectedSquares(key)) {
            if (!k.equals(key)) {
                int fill = sudokuBoard.squareMap.get(k).fill;
                if (fill != 0) {
                    validMarks[fill - 1] = false;
                }
            }
        }
        return validMarks;
    }

//    private static List<Integer> getInvalidMarks(Square square, SudokuBoard board) {
//        List<Integer> invalidMarks = new ArrayList<>();
//
//        for(Square s : board.getConnectedSquares(square)) {
//            if (s != square && s.getFill() != 0) {
//                invalidMarks.add(s.getFill());
//            }
//        }
//        return invalidMarks;
//    }
}
