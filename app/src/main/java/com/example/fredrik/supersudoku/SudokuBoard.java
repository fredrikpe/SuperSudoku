package com.example.fredrik.supersudoku;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by fredrik on 11.12.16.
 */

class SudokuBoard {

    ConcurrentMap<Integer, Square> squareMap;

    final SudokuContainer[] rowContainers;
    final SudokuContainer[] columnContainers;
    final SudokuContainer[] boxContainers;

    List<Integer> representativeKeys;

    private TwoKeyInterface sameRowOperator = (k, l) -> k/9 == l/9;
    private TwoKeyInterface sameColumnOperator = (k, l) -> k%9 == l%9;
    private TwoKeyInterface sameBoxOperator = (k, l) -> (k/9)/3 == (l/9)/3 && (k%9)/3 == (l%9)/3;

    List<String> stringSudokus;


    private SomeEventListener listener;

    public void setSomeEventListener(SomeEventListener listener) {
        this.listener = listener;
    }

    SudokuBoard() {
        squareMap = new ConcurrentHashMap<>();
        representativeKeys = new ArrayList<>();

        stringSudokus = new ArrayList<>();

        rowContainers = new SudokuContainer[9];
        columnContainers = new SudokuContainer[9];
        boxContainers = new SudokuContainer[9];

        for (int i = 0; i < 9; i++) {
            rowContainers[i] = new SudokuContainer(ContainerType.ROW, new Integer[] {});
            for (int j = 0; j < 9; j++) {
                squareMap.put(key(i, j), new Square(i, j, 0, new int[] {}, new int[] {},true));
            }
            representativeKeys.add(key(i, i/3 + (i%3)*3));
        }
    }

    /**
     * Adds or removes a candidate from a given square (if allowed). User removed
     * candidates are saved.
     *
     * @param key       key of the square
     * @param candidate the candidate to add/remove
     */
    void setCandidateFromUser(Integer key, int candidate) { setCandidate(key, candidate, true); }

    /**
     * Adds or removes a candidate from a given square (if allowed).
     *
     * @param key       key of the square
     * @param candidate the candidate to add/remove
     */
    void setCandidateFromAssistant(Integer key, int candidate) { setCandidate(key, candidate, false); }

    /**
     * Sets or unsets a number in a square.
     *
     * @param key   key of the square
     * @param fill  the number to fill
     */
    void setFill(Integer key, int fill) {
        Square square = squareMap.get(key);
        if (square.editable) {
            squareMap.put(key, new Square(square.i, square.j, fill, square.candidates, square.userRemovedCandidates, true));
        }
    }

    /**
     * Called when a change to the board state by the user happens. Initiates the assistant task.
     */
    void changeOccurred() {
        new SudokuAssistantTask().execute(this);
    }

    /**
     * Translates i, j coordinates to a key.
     *
     * @param i row number
     * @param j column number
     * @return  a key to the squareMap
     */
    static Integer key(int i, int j) { return i * 9 + j; }

    private void setCandidate(Integer key, int candidate, boolean fromUser) {
        Square square = squareMap.get(key);
        if (square.editable) {
            int[] newUserRemovedCandidates = square.userRemovedCandidates;
            if (fromUser) {
                newUserRemovedCandidates = addRemoveUserRemovedCandidates(square, candidate);
            }
            int[] newCandidates = addRemoveCandidates(square, candidate);
            squareMap.put(key, new Square(square.i, square.j, square.fill, newCandidates, newUserRemovedCandidates, true));
        }
    }

    /**
     * Called by the assistant task when it finishes.
     *
     * @param result    true if assistant made changes to the board.
     */
    void assistantFinished(Boolean result) {
        if (listener != null) {
            listener.onSomeEvent();
        }
        if (result) {
            new SudokuAssistantTask().execute(this);
        }
    }

    interface SomeEventListener {
        void onSomeEvent ();
    }

    interface TwoKeyInterface {
        boolean operator(Integer k, Integer l);
    }

    private Integer[] getContainer(Integer key, TwoKeyInterface op, String name) {
        Integer[] container = new ArrayList<>();
        for (ConcurrentMap.Entry<Integer, Square> entry : squareMap.entrySet()) {
            if (op.operator(key, entry.getKey())) {
                // System.out.println("Key " + key + " and " + entry.getKey() + " are on the same " + name);
                container.add(entry.getKey());
            }
        }
        return container;
    }

    Integer[] getRow(Integer key) {
        return getContainer(key, sameRowOperator, "row");
    }

    Integer[] getColumn(Integer key) {
        return getContainer(key, sameColumnOperator, "column");
    }

    Integer[] getBox(Integer key) {
        return getContainer(key, sameBoxOperator, "box");
    }

    Integer[] getRows(Integer[] keys) {
        Integer[] rows = new ArrayList<>();
        for (Integer key : keys) {
            rows.addAll(getColumn(key));
        }
        return rows;
    }

    Integer[] getColumns(Integer[] keys) {
        Integer[] columns = new ArrayList<>();
        for (Integer key : keys) {
            columns.addAll(getColumn(key));
        }
        return columns;
    }

    List<Integer[]> getContainers() {
        List<Integer[]> containers = new ArrayList<>();
        for (Integer key : representativeKeys) {
            containers.add(getRow(key));
            containers.add(getColumn(key));
            containers.add(getBox(key));
        }
        return containers;
    }

    List<Integer[]> getBoxContainers() {
        List<Integer[]> boxContainers = new ArrayList<>();
        for (Integer key : representativeKeys) {
            boxContainers.add(getBox(key));
        }
        return boxContainers;
    }

    List<Integer[]> getBoxPairContainers() {
        List<Integer[]> boxPairContainers = new ArrayList<>();
        for (Integer[] box1 : getBoxContainers()) {
            for (Integer[] box2 : getBoxContainers()) {
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

    Integer[] getConnectedSquares(Integer key) {
        Integer[] connectedSquares = getRow(key);
        connectedSquares.addAll(getColumn(key));
        connectedSquares.addAll(getBox(key));
        connectedSquares.removeAll(Collections.singletonList(key));
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
                squareMap.put(Integer.valueOf(i), new Square(rowIndex(i), columnIndex(i), fill, new int[] {}, new int[] {}, fill == 0));
            }
        }
    }

    private static Integer rowIndex(Integer key) { return key / 9; }
    private static Integer columnIndex(Integer key) { return key % 9; }

    private int[] addRemoveCandidates(Square square, int candidate) {
        for (int m : square.candidates) {
            if (m == candidate) return removeCandidate(square.candidates, candidate);
        }
        if (!square.userRemovedCandidatesContains(candidate)) {
            return addCandidate(square.candidates, candidate);
        }
        return square.candidates;
    }

    private int[] addRemoveUserRemovedCandidates(Square square, int candidates) {
        for (int m : square.userRemovedCandidates) {
            if (m == candidates) return removeCandidate(square.userRemovedCandidates, candidates);
        }
        return addCandidate(square.userRemovedCandidates, candidates);
    }

    private int[] removeCandidate(int[] candidates, int candidate) {
        int[] newCandidates = new int[candidates.length - 1];
        int i = 0;
        for (int m : candidates) {
            if (m != candidate) {
                newCandidates[i++] = m;
            }
        }
        return newCandidates;
    }

    private int[] addCandidate(int[] candidates, int candidate) {
        int[] newCandidates = new int[candidates.length + 1];
        int i = 0;
        for (int m : candidates) {
            newCandidates[i++] = m;
        }
        newCandidates[i] = candidate;
        return newCandidates;
    }
}

