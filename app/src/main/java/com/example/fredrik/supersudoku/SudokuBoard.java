package com.example.fredrik.supersudoku;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by fredrik on 11.12.16.
 */

class SudokuBoard {

    ConcurrentMap<Integer, Square> squareMap;

    List<Integer> representativeKeys;

    private TwoKeyInterface sameRowOperator = (k, l) -> k/9 == l/9;
    private TwoKeyInterface sameColumnOperator = (k, l) -> k%9 == l%9;
    private TwoKeyInterface sameBoxOperator = (k, l) -> (k/9)/3 == (l/9)/3 && (k%9)/3 == (l%9)/3;

    List<String> stringSudokus;

    final Object changeMonitor = new Object();
    boolean changeOccured;
    private SomeEventListener listener;

    public void setSomeEventListener (SomeEventListener listener) {
        this.listener = listener;
    }

    static Integer key(int i, int j) { return i * 9 + j; }
    static Integer rowIndex(Integer key) { return key / 9; }
    static Integer columnIndex(Integer key) { return key % 9; }

    SudokuBoard() {
        squareMap = new ConcurrentHashMap<>();
        representativeKeys = new ArrayList<>();

        stringSudokus = new ArrayList<>();

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                squareMap.put(key(i, j), new Square(i, j, 0, new int[] {}, true));
            }
            representativeKeys.add(key(i, i/3 + (i%3)*3));
        }
    }

    Square getSquare(int i, int j) {
        return squareMap.get(key(i, j));
    }

    synchronized void setMark(Integer key, int mark) {
        Square square = squareMap.get(key);
        if (square.editable) {
            if (square.containsMark(mark)) {
                int[] newMarks = removeMark(square.marks, mark);
                squareMap.put(key, new Square(square.i, square.j, square.fill, newMarks, true));
            } else {
                int[] newMarks = addMark(square.marks, mark);
                squareMap.put(key, new Square(square.i, square.j, square.fill, newMarks, true));
            }
            changeOccured = true;
        } else {
            changeOccured = false;
        }
        notifyChange();
    }

    synchronized void setFill(Integer key, int fill) {
        Square square = squareMap.get(key);
        if (square.editable) {
            squareMap.put(key, new Square(square.i, square.j, fill, square.marks, true));
            changeOccured = true;
        } else {
            changeOccured = false;
        }
        notifyChange();
    }

    void notifyChange() {
        if (changeOccured) {
            if (listener != null) listener.onSomeEvent();
        }
        // TODO: Move into if statement above
        synchronized (changeMonitor) {
            changeMonitor.notifyAll();
        }
    }

    interface SomeEventListener {
        void onSomeEvent ();
    }

    interface TwoKeyInterface {
        boolean operator(Integer k, Integer l);
    }

    private List<Integer> getContainer(Integer key, TwoKeyInterface op, String name) {
        List<Integer> container = new ArrayList<>();
        for (ConcurrentMap.Entry<Integer, Square> entry : squareMap.entrySet()) {
            if (op.operator(key, entry.getKey())) {
                // System.out.println("Key " + key + " and " + entry.getKey() + " are on the same " + name);
                container.add(entry.getKey());
            }
        }
        return container;
    }

    List<Integer> getRow(Integer key) {
        return getContainer(key, sameRowOperator, "row");
    }

    List<Integer> getColumn(Integer key) {
        return getContainer(key, sameColumnOperator, "column");
    }

    List<Integer> getBox(Integer key) {
        return getContainer(key, sameBoxOperator, "box");
    }

    List<Integer> getRows(List<Integer> keys) {
        List<Integer> rows = new ArrayList<>();
        for (Integer key : keys) {
            rows.addAll(getColumn(key));
        }
        return rows;
    }

    List<Integer> getColumns(List<Integer> keys) {
        List<Integer> columns = new ArrayList<>();
        for (Integer key : keys) {
            columns.addAll(getColumn(key));
        }
        return columns;
    }

    List<List<Integer>> getContainers() {
        List<List<Integer>> containers = new ArrayList<>();
        for (Integer key : representativeKeys) {
            containers.add(getRow(key));
            containers.add(getColumn(key));
            containers.add(getBox(key));
        }
        return containers;
    }

    List<List<Integer>> getBoxContainers() {
        List<List<Integer>> boxContainers = new ArrayList<>();
        for (Integer key : representativeKeys) {
            boxContainers.add(getBox(key));
        }
        return boxContainers;
    }

    List<List<Integer>> getBoxPairContainers() {
        List<List<Integer>> boxPairContainers = new ArrayList<>();
        for (List<Integer> box1 : getBoxContainers()) {
            for (List<Integer> box2 : getBoxContainers()) {
                if (box1 != box2) {
                    if (rowIndex(box1.get(0)) / 3 == rowIndex(box1.get(0)) / 3
                            || columnIndex(box1.get(0)) / 3 == columnIndex(box1.get(0)) / 3) {
                        boxPairContainers.add(box1);
                        boxPairContainers.get(boxPairContainers.size() - 1).addAll(box2);
                    }
                }
            }
        }
        return boxPairContainers;
    }

    List<Integer> getConnectedSquares(Integer key) {
        List<Integer> connectedSquares = getRow(key);
        connectedSquares.addAll(getColumn(key));
        connectedSquares.addAll(getBox(key));
        connectedSquares.removeAll(Arrays.asList(key));
        return connectedSquares;
    }

    void getNewRandomSudoku() throws Exception {
        if (stringSudokus != null && stringSudokus.size() > 0) {
            int r  = RNG.randInt(0, stringSudokus.size() - 1);
            String sudokuString = stringSudokus.get(r);
            if (sudokuString.length() != 81) {
                throw new Exception("Sudoku string not 81 chars long!");
            }
            squareMap.clear();
            for (int i = 0; i < 81; i++) {
                int fill = Character.getNumericValue(sudokuString.charAt(i));
                squareMap.put(Integer.valueOf(i), new Square(rowIndex(i), columnIndex(i), fill, new int[] {}, fill == 0));
            }
        }
    }

    private int[] removeMark(int[] marks, int mark) {
        int[] newMarks = new int[marks.length - 1];
        int i = 0;
        for (int m : marks) {
            if (m != mark) {
                newMarks[i++] = m;
            }
        }
        return newMarks;
    }

    private int[] addMark(int[] marks, int mark) {
        int[] newMarks = new int[marks.length + 1];
        int i = 0;
        for (int m : marks) {
            newMarks[i++] = m;
        }
        newMarks[i] = mark;
        return newMarks;
    }
}

