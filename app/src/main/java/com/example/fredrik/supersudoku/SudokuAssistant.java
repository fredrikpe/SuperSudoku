package com.example.fredrik.supersudoku;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by fredrik on 12.12.16.
 */

class SudokuAssistant {

    static void singleCandidate(SudokuBoard board) {
        for (Square square : board.squares) {
            if (square.marks.size() == 1) {
                square.setFill(square.marks.get(0));
                // Do something to square.marks?
            }
        }
    }

    static void initiateMarks(SudokuBoard board) {
        for (Square square : board.squares) {
            square.marks = getValidMarks(square, board);
        }
    }

    static void updateMarks(SudokuBoard board) {
        for (Square square : board.squares) {
            square.marks.removeAll(getInvalidMarks(square, board));
        }
    }

    private static List<Integer> getValidMarks(Square square, SudokuBoard board) {
        List<Integer> validMarks = new ArrayList<>();
        validMarks.addAll(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));

        for(Square s : board.getConnectedSquares(square)) {
            if (s != square && s.getFill() != 0) {
                validMarks.remove(Integer.valueOf(s.getFill()));
            }
        }
        return validMarks;
    }

    private static List<Integer> getInvalidMarks(Square square, SudokuBoard board) {
        List<Integer> invalidMarks = new ArrayList<>();

        for(Square s : board.getConnectedSquares(square)) {
            if (s != square && s.getFill() != 0) {
                invalidMarks.add(s.getFill());
            }
        }
        return invalidMarks;
    }
}
