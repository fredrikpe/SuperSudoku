package com.example.fredrik.sudoku;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by fredrik on 12.12.16.
 */

public class SudokuAssistant {

    void singleCandidate(SudokuBoard board) {
        for (Square square : board.squares) {
            if (square.marks.size() == 1) {
                square.fill = square.marks.get(0);
                // Do something to square.marks?
            }
        }
    }

    void addMarks(SudokuBoard board) {
        for (Square square : board.squares) {
            square.marks = validNumbers(square, board);
        }
    }

    ArrayList<Integer> validNumbers(Square square, SudokuBoard board) {
        ArrayList<Integer> validNumbers = new ArrayList<Integer>();
        validNumbers.addAll(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));

        for(Square s : board.getConnectedSquares(square)) {
            if (s != square && s.fill != 0) {
                validNumbers.remove(new Integer(s.fill));
            }
        }
        return validNumbers;
    }
}
