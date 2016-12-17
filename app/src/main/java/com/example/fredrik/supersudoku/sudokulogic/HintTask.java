package com.example.fredrik.supersudoku.sudokulogic;

import android.os.AsyncTask;

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
//
//    void removeSingleColumnRowCandidates(Board board) {
//        for (Integer[] box : board.getBoxContainers()) {
//            columnRowCandidates(board, box, 1);
//        }
//    }
//
//    void removeDoubleColumnRowCandidates(Board board) {
//        for (Integer[] boxPair : board.getBoxPairContainers()) {
//            columnRowCandidates(board, boxPair, 2);
//        }
//    }
//
//    private void columnRowCandidates(Board board, Integer[] container, int size) {
//        List<SquareHolder> columnCandidates = new ArrayList<>();
//        List<SquareHolder> rowCandidates = new ArrayList<>();
//        for (int n : allNumbers) {
//            columnCandidates.add(new SquareHolder(size));
//            rowCandidates.add(new SquareHolder(size));
//        }
//        for (Integer key : container) {
//            Square square = board.squareMap.get(key);
//            for (int candidate : square.candidates) {
//                if (rowCandidates.get(candidate).no_conflicts) {
//                    rowCandidates.get(candidate).insert(square);
//                }
//                if (columnCandidates.get(candidate).no_conflicts) {
//                    columnCandidates.get(candidate).insert(square);
//                }
//            }
//        }
//        for (int i = 0; i < 9; i++) {
//            if (rowCandidates.get(i).no_conflicts) {
//                removeCandidate(i, board.getRows(rowCandidates.get(i).squares), container);
//            }
//            if (columnCandidates.get(i).no_conflicts) {
//                removeCandidate(i, board.getColumns(columnCandidates.get(i).squares), container);
//            }
//        }
//    }
//
//    void removeCandidate(int candidate, List<Square> container, List<Square> excluding) {
//        if (excluding != null) {
//            container.removeAll(excluding);
//        }
//        for (Square square : container) {
//            square.candidates.remove(candidate);
//        }
//    }
//
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
//
//    private class SquareHolder {
//        List<Square> squares;
//        private int max_size;
//        boolean no_conflicts = true;
//
//        SquareHolder(int max_size) {
//            this.max_size = max_size;
//            squares = new ArrayList<>();
//        }
//
//        void insert(Square s) {
//            if (squares.contains(s)) {
//            } else if (squares.size() < max_size) {
//                squares.add(s);
//            } else {
//                no_conflicts = false;
//            }
//        }
//    }
}



















