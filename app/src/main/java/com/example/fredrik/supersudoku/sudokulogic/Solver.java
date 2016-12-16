package com.example.fredrik.supersudoku.sudokulogic;

/**
 * Created by fredrik on 12.12.16.
 */

public class Solver {
//
//    static int[] allNumbers = {1, 2, 3, 4, 5, 6, 7, 8, 9};
//
//    void uniqueCandidate(Board board) {
//        List<Integer> candidates = new ArrayList<>();
//        List<Square> candidateSquares = new ArrayList<>();
//
//        for (List<Square> container : board.getContainers()) {
//            candidates.clear();
//            candidateSquares.clear();
//            for (int n : allNumbers) {
//                candidates.add(0);
//                candidateSquares.add(null);
//            }
//            for (Square square : container) {
//                for (int mark : square.candidates.iterable()) {
//                    candidates.set(mark, candidates.get(mark) + 1);
//                    candidateSquares.set(mark, square);
//                }
//            }
//            for (int i = 0; i < 9; i++) {
//                if (candidates.get(i) == 1) {
//                    // Unique candidate found
//                    candidateSquares.get(i).setFill(i);
//                } else if (candidates.get(i) == 0) {
//                    // Illegal board state!
//                }
//            }
//        }
//    }
//
//    void removeSingleColumnRowCandidates(Board board) {
//        for (List<Square> box : board.getBoxContainers()) {
//            columnRowCandidates(board, box, 1);
//        }
//    }
//
//    void removeDoubleColumnRowCandidates(Board board) {
//        for (List<Square> boxPair : board.getBoxPairContainers()) {
//            columnRowCandidates(board, boxPair, 2);
//        }
//    }
//
//    private void columnRowCandidates(Board board, List<Square> container, int size) {
//        List<SquareHolder> columnCandidates = new ArrayList<>();
//        List<SquareHolder> rowCandidates = new ArrayList<>();
//        for (int n : allNumbers) {
//            columnCandidates.add(new SquareHolder(size));
//            rowCandidates.add(new SquareHolder(size));
//        }
//        for (Square square : container) {
//            for (int mark : square.candidates) {
//                if (rowCandidates.get(mark).no_conflicts) {
//                    rowCandidates.get(mark).insert(square);
//                }
//                if (columnCandidates.get(mark).no_conflicts) {
//                    columnCandidates.get(mark).insert(square);
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
//                        for (int mark : square2.candidates.iterable()) {
//                            if (!square.candidates.contains(mark))
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



















