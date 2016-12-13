package com.example.fredrik.supersudoku;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by fredrik on 12.12.16.
 */

class SudokuAssistant implements Runnable {

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
        for (Square square : sudokuBoard.getSquares()) {
            if (square.marks.size() == 1 && square.getFill() == 0) {
                if (square.setFill(square.marks.get(0))) {
                    System.out.println("succsefully changed square");
                } else {
                    System.out.println("square was fixed");
                }
                // Do something to square.marks?
                System.out.println(square.marks.get(0));
                return true;
            }
        }
        return false;
    }

    void updateMarks() {
        System.out.println("Updating marks");
        for (Square square : sudokuBoard.getSquares()) {
            for(Integer mark : getValidMarks(square)) {
                if (!square.marks.contains(mark)) {
                    square.marks.add(mark);
                }
            }
        }
    }

//    static void removeIllegalMarks(SudokuBoard board) {
//        for (Square square : board.squares) {
//            square.marks.removeAll(getInvalidMarks(square, board));
//        }
//    }

    private List<Integer> getValidMarks(Square square) {
        List<Integer> validMarks = new ArrayList<>();
        validMarks.addAll(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
        if (sudokuBoard.getConnectedSquares(square).size() != 21) System.out.println("CS != 21, CS = " + sudokuBoard.getConnectedSquares(square).size());
        for(Square s : sudokuBoard.getConnectedSquares(square)) {
            int fill = s.getFill();
            if (s != square && fill != 0) {
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
