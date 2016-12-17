package com.example.fredrik.supersudoku.sudokulogic;

import android.os.AsyncTask;

import com.example.fredrik.supersudoku.asdflaksd.Array;

import java.util.Map;

/**
 * Created by fredrik on 12.12.16.
 */

class AssistantTask extends AsyncTask<Board, Integer, Boolean> {

    private Board board;

    @Override
    protected Boolean doInBackground(Board... objects) {
        board = objects[0];
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
        board.assistantFinished(result);
    }

    /**
     * Finds and fills squares with single candidates in the given board.
     *
     * @param   board the sudoku board to search through
     * @return              true if a single candidate was found
     */
    private boolean singleCandidate(Board board) {
        for (Map.Entry<Integer, Square> entry : board.squareMap.entrySet()) {
            Square square = entry.getValue();
            if (square.candidates.length == 1 && square.fill == 0) {
                if (!square.editable) {
                    System.out.println("Error. Not editable and fill == 0");
                }
                board.setFillFromAssistant(entry.getKey(), square.candidates[0]);
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
     * @param board   the sudoku board to update
     */
    private void updateCandidates(Board board) {
        for (Map.Entry<Integer, Square> entry : board.squareMap.entrySet()) {
            Square square = entry.getValue();
            if (square.editable) {
                Integer key = entry.getKey();

                boolean[] validCandidates = getValidCandidates(board, key);
                for (int candidate = 1; candidate <= 9; candidate++) {
                    if (validCandidates[candidate - 1] && !Array.contains(square.candidates, candidate)) {
                        board.setCandidateFromAssistant(key, candidate);
                    } else if (!validCandidates[candidate - 1] && Array.contains(square.candidates, candidate)) {
                        board.setCandidateFromAssistant(key, candidate);
                    }
                }
            }
        }
    }

    /**
     * Gives a candidate - 1 indexed list of valid candidates for a given square in a sudoku board.
     *
     * @param board   the sudoku board to search
     * @param key           the key of the square
     * @return              a boolean list of possible candidates
     */
    private boolean[] getValidCandidates(Board board, Integer key) {
        boolean[] validCandidates = new boolean[] {true, true, true, true, true, true, true, true, true};

        //if (board.getConnectedSquares(key).length != 24) System.out.println("CS != 24, CS = " + board.getConnectedSquares(key).length);
        for(Integer k : board.getConnectedSquares(key)) {
            if (!k.equals(key)) {
                int fill = board.squareMap.get(k).fill;
                if (fill != 0) {
                    validCandidates[fill - 1] = false;
                }
            }
        }
        return validCandidates;
    }
}
