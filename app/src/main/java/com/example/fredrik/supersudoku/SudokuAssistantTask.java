package com.example.fredrik.supersudoku;

import android.os.AsyncTask;

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
        updateCandidates(objects[0]);
        return singleCandidate(objects[0]);
    }

    /**
     * Executed when doInBackground completes.
     *
     * @param result
     */
    @Override
    protected void onPostExecute(Boolean result) {
        sudokuBoard.assistantFinished(result);
    }

    /**
     * Finds and fills squares with single candidates in the given sudokuBoard.
     *
     * @param   sudokuBoard the sudoku board to search through
     * @return              true if a single candidate was found
     */
    private boolean singleCandidate(SudokuBoard sudokuBoard) {
        for (Square square : sudokuBoard.squareMap.values()) {
            if (square.candidates.length == 1 && square.fill == 0) {
                if (!square.editable) {
                    System.out.println("Error. Not editable and fill == 0");
                }
                sudokuBoard.setFill(SudokuBoard.key(square.i, square.j), square.candidates[0]);
                //System.out.println("Found single candidate " + square.candidates[0] + " at " + square.i + ", " + square.j);
                return true;
            }
        }
        return false;
    }

    /**
     * Updates candidates for all squares in the sudoku board.
     * User removed candidates are not added.
     *
     * @param sudokuBoard   the sudoku board to update
     */
    private void updateCandidates(SudokuBoard sudokuBoard) {
        for (Square square : sudokuBoard.squareMap.values()) {
            if (square.editable) {
                Integer key = SudokuBoard.key(square.i, square.j);

                boolean[] validCandidates = getValidCandidates(sudokuBoard, key);
                for (int candidate = 1; candidate <= 9; candidate++) {
                    if (validCandidates[candidate - 1] &&!square.candidatesContains(candidate)) {
                        sudokuBoard.setCandidateFromAssistant(key, candidate);
                    } else if (!validCandidates[candidate - 1] && square.candidatesContains(candidate)) {
                        sudokuBoard.setCandidateFromAssistant(key, candidate);
                    }
                }
            }
        }
    }

    /**
     * Check the validity of the sudoku board, i.e, if it contains any collisions
     * or if some squares have no valid candidates.
     *
     * @param sudokuBoard
     * @return              true if the board is in a valid state
     */
    private boolean validityCheck(SudokuBoard sudokuBoard) {
        return true;
    }

    /**
     * Gives a candidate - 1 indexed list of valid candidates for a given square in a sudoku board.
     *
     * @param sudokuBoard   the sudoku board to search
     * @param key           the key of the square
     * @return              a boolean list of possible candidates
     */
    private boolean[] getValidCandidates(SudokuBoard sudokuBoard, Integer key) {
        boolean[] validCandidates = new boolean[] {true, true, true, true, true, true, true, true, true};

        if (sudokuBoard.getConnectedSquares(key).size() != 24) System.out.println("CS != 24, CS = " + sudokuBoard.getConnectedSquares(key).size());
        for(Integer k : sudokuBoard.getConnectedSquares(key)) {
            if (!k.equals(key)) {
                int fill = sudokuBoard.squareMap.get(k).fill;
                if (fill != 0) {
                    validCandidates[fill - 1] = false;
                }
            }
        }
        return validCandidates;
    }
}
