package com.example.fredrik.supersudoku;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by fredrik on 12.12.16.
 */

class SudokuAssistant implements Runnable{

    private Thread thread;
    String threadName;

    SudokuBoard sudokuBoard;

    SudokuAssistant(SudokuBoard board) {
        this.sudokuBoard = board;
        threadName = "SudokuAssistantThread";
    }

    @Override
    public void run() {
        System.out.println("Starting while loop.");
        while (!thread.isInterrupted()) {
            do {
                updateMarks();
            } while (singleCandidate());

            sudokuBoard.changeOccured = false;

            while (!sudokuBoard.changeOccured) {
                synchronized (sudokuBoard.changeMonitor) {
                    try {
                        sudokuBoard.changeMonitor.wait();
                    } catch (InterruptedException e) {
                        System.out.println("Was interrupted");
                    }
                }
            }
        }
    }

    void start () {
        if (thread == null) {
            thread = new Thread (this, threadName);
            thread.start();
        }
    }

    void interrupt() {
        if (thread != null) {
            thread.interrupt();
        }
    }

    boolean singleCandidate() {
        for (Square square : sudokuBoard.squareMap.values()) {
            if (square.marks.length == 1 && square.fill == 0) {
                if (!square.editable) {
                    System.out.println("Error. Not editable and fill == 0");
                }
                sudokuBoard.setFill(SudokuBoard.key(square.i, square.j), square.marks[0]);
                System.out.println("Found single candidate " + square.marks[0] + " at " + square.i + ", " + square.j);
                return true;
            }
        }
        return false;
    }

    void updateMarks() {
        for (Square square : sudokuBoard.squareMap.values()) {
            if (square.editable) {
                Integer key = SudokuBoard.key(square.i, square.j);
                for (Integer mark : getValidMarks(key)) {
                    if (!square.containsMark(mark)) {
                        sudokuBoard.setMark(key, mark);
                    }
                }
            }
        }
    }

//    static void removeIllegalMarks(SudokuBoard board) {
//        for (Square square : board.squares) {
//            square.marks.removeAll(getInvalidMarks(square, board));
//        }
//    }

    private List<Integer> getValidMarks(Integer key) {
        List<Integer> validMarks = new ArrayList<>();
        validMarks.addAll(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
        if (sudokuBoard.getConnectedSquares(key).size() != 24) System.out.println("CS != 24, CS = " + sudokuBoard.getConnectedSquares(key).size());
        for(Integer k : sudokuBoard.getConnectedSquares(key)) {
            if (!k.equals(key)) {
                int fill = sudokuBoard.squareMap.get(k).fill;
                if (fill != 0) {
                    validMarks.remove(Integer.valueOf(fill));
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
