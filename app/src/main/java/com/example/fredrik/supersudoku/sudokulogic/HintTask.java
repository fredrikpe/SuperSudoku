package com.example.fredrik.supersudoku.sudokulogic;

import android.os.AsyncTask;
import android.support.annotation.Nullable;

import com.example.fredrik.supersudoku.asdflaksd.Array;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fredrik on 12.12.16.
 */

public class HintTask extends AsyncTask<Board, Integer, Hint> {

    static int[] allNumbers = {1, 2, 3, 4, 5, 6, 7, 8, 9};
    private Board board;

    @Override
    protected Hint doInBackground(Board... boards) {
        board = boards[0];
        Hint hint = uniqueCandidate();
        if (hint == null) {
           hint = removeSingleColumnRowCandidates();
        }
//        if (hint == null) {
//            hint = removeDoubleColumnRowCandidates();
//        }
        return hint;
    }

    @Override
    protected void onPostExecute(Hint result) {
        board.hintTaskFinished(result);
    }

    public Hint uniqueCandidate() {
        int[] numOfCandidates;
        Integer[] candidateKeys;

        for (Integer[] container : board.getContainers()) {
            numOfCandidates = new int[9];
            candidateKeys = new Integer[9];
            for (int n : allNumbers) {
                numOfCandidates[n-1] = 0;
                candidateKeys[n-1] = 0;
            }

            for (Integer key : container) {
                Square square = board.squareMap.get(key);
                if (square.editable && square.fill == 0) {
                    for (int candidate : square.candidates) {
                        numOfCandidates[candidate - 1]++;
                        candidateKeys[candidate - 1] = key;
                    }
                }
            }
            for (int i = 0; i < 9; i++) {
                if (numOfCandidates[i] == 1) {
                    // Unique candidate found
                    System.out.println("Found unique candidate! " + (i + 1) + ", key = " + candidateKeys[i]);
                    return new UniqueCandidate(container, i + 1, candidateKeys[i]);
                }
//                  else if (candidates.get(i) == 0) {
//                    // Illegal board state!
//                }
            }
        }
        return null;
    }

    @Nullable
    private Hint removeSingleColumnRowCandidates() {
        for (Integer[] box : board.getBoxContainers()) {
            Hint hint = columnRowCandidates(board, box);
            if (hint != null) return hint;
        }
        return null;
    }


    // This needs changing to columnRowCandidates method to handle two boxes, two row/c olumns
//    @Nullable
//    private Hint removeDoubleColumnRowCandidates() {
//        for (Integer[] boxPair : board.getBoxPairContainers()) {
//            Hint hint = columnRowCandidates(board, boxPair);
//            if (hint != null) return hint;
//        }
//        return null;
//    }

    private Hint columnRowCandidates(Board board, Integer[] container) {
        Integer[][] rowColumnCandidates = new Integer[2][9];

        for (Integer key : container) {
            Square square = board.squareMap.get(key);
            if (square.editable) {
                for (int candidate : square.candidates) {
                    for (ContainerType type : ContainerType.values()) {
                        if (type == ContainerType.BOX) continue;
                        if (rowColumnCandidates[type.ordinal()][candidate - 1] == null) {
                            rowColumnCandidates[type.ordinal()][candidate - 1] = key;
                        } else {
                            if (!Board.containerIndex(rowColumnCandidates[type.ordinal()][candidate - 1], type).equals(Board.containerIndex(key, type))) {
                                rowColumnCandidates[type.ordinal()][candidate - 1] = -1;
                            }
                        }
                    }
                }
            }
        }
        for (int i = 0; i < 9; i++) {
            for (ContainerType type : ContainerType.values()) {
                if (type == ContainerType.BOX) continue;
                if ((rowColumnCandidates[type.ordinal()][i] != null) && (rowColumnCandidates[type.ordinal()][i] != -1)) {
                    // Found single row in box
                    // Check rest of row if candidate exists.
                    int candidate = i + 1;
                    Integer containerKey = rowColumnCandidates[type.ordinal()][i];
                    Integer[] rowColumnContainer = board.getContainer(containerKey, type);

                    for (Integer key : rowColumnContainer) {
                        Square square = board.squareMap.get(key);
                        if (!Array.contains(rowColumnContainer, key)) {
                            if (square.editable && square.fill == 0 &&  Array.contains(square.candidates, candidate)) {
                                // Found hint
                                return new BoxElimination(rowColumnContainer, candidate, key);
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

//    void nakedSubset(Board board) {
//        for (List<Square> container : board.getContainers()) {
//            for (int i = 2; i < 8; i++) {
//                // Checks for naked subset in increasing order. A subset of eight implies a unique candidate.
//                outerLoop:
//                for (Square square : container) {
//                    if (square.candidates.size() > i)
//                        continue;
//                    for (Square square2 : container) {
//                        if (square2 == square || square2.candidates.size() > i)
//                            continue;
//                        for (int candidate : square2.candidates.iterable()) {
//                            if (!square.candidates.contains(candidate))
//                                continue outerLoop;
//                        }
//                    }
//                }
//            }
//        }
//    }
}



















