package com.example.fredrik.supersudoku.sudokulogic;

import com.example.fredrik.supersudoku.RNG;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by fredrik on 11.12.16.
 */

public class Board {

    /**
     * The main data object. Holds all the squares by key values corresponding to
     * position on board.
     * 0    1   2 ...
     * 9    10  11 ...
     * ...
     */
    public ConcurrentMap<Integer, Square> squareMap;

    private Stack<Move> moves;
    private Stack<Move> userMoves;

    public List<String> stringSudokus;

    private List<Integer> representativeKeys;
    private SomeEventListener listener;
    public void setSomeEventListener(SomeEventListener listener) {
        this.listener = listener;
    }

    public Board() {
        squareMap = new ConcurrentHashMap<>();
        moves = new Stack<>();
        userMoves = new Stack<>();
        representativeKeys = new ArrayList<>();
        stringSudokus = new ArrayList<>();

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                squareMap.put(key(i, j), new Square(0, new int[] {}, new int[] {},true));
            }
            representativeKeys.add(key(i, i/3 + (i%3)*3));
        }
    }

    public void newGame() {
        try {
            getNewRandomSudoku();
        } catch (Exception e) {
            e.printStackTrace();
        }
        moves.clear();
        userMoves.clear();
        changeOccurred();
    }

    public void setCandidateFromUser(Integer key, int candidate) { setCandidate(key, candidate, true); }

    void setCandidateFromAssistant(Integer key, int candidate) { setCandidate(key, candidate, false); }

    public void setFillFromUser(Integer key, int fill) { setFill(key, fill, true); }

    void setFillFromAssistant(Integer key, int fill) { setFill(key, fill, false); }

    public void undo() {
        if (userMoves.size() > 0) {
            Move move = moves.pop();
            Move lastUserMove = userMoves.pop();

            squareMap.put(move.key, move.oldSquare);
            while (move != lastUserMove) {
                move = moves.pop();
                squareMap.put(move.key, move.oldSquare);
            }
        }
        if (listener != null) {
            listener.onSomeEvent();
        }
    }

    /**
     * Called when a change to the board state by the user happens. Initiates the assistant task.
     */
    public void changeOccurred() {
        new AssistantTask().execute(this);
    }

    /**
     * Translates i, j coordinates to a key.
     *
     * @param i row number
     * @param j column number
     * @return  a key to the squareMap
     */
    public static Integer key(int i, int j) { return i * 9 + j; }

    /**
     * Sets or unsets a number in a square.
     *
     * @param key   key of the square
     * @param fill  the number to fill
     */
    private void setFill(Integer key, int fill, boolean fromUser) {
        Square square = squareMap.get(key);
        if (square.editable) {
            makeMove(key, new Square(fill, square.candidates, square.userRemovedCandidates, true), fromUser);
        }
    }

    private void makeMove(Integer key, Square newSquare, boolean fromUser) {
        Move move = new Move(key, squareMap.get(key), newSquare);
        if (fromUser) {
            userMoves.push(move);
        }
        moves.push(move);
        squareMap.put(key, newSquare);
    }

    private void setCandidate(Integer key, int candidate, boolean fromUser) {
        Square square = squareMap.get(key);
        if (square.editable) {
            int[] newUserRemovedCandidates = square.userRemovedCandidates;
            if (fromUser) {
                newUserRemovedCandidates = addRemoveUserRemovedCandidates(square, candidate);
            }
            int[] newCandidates = addRemoveCandidates(square, candidate);
            makeMove(key, new Square(square.fill, newCandidates, newUserRemovedCandidates, true), fromUser);
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
            new AssistantTask().execute(this);
        }
    }

    public interface SomeEventListener {
        void onSomeEvent ();
    }

    private Integer[] getContainer(Integer key, ContainerType type) {
        Integer[] container = new Integer[9];
        //System.out.println("Type = " + type + " key = " + key);
        switch (type) {
            case ROW:
                int start = (key/9) * 9;
                int i = 0;
                for (int k = start; k < start + 9; k++) {
                    container[i++] = k;
                }
                break;
            case COLUMN:
                start = key % 9;
                i = 0;
                for (int k = start; k < start + 81; k+=9) {
                    container[i++] = k;
                }
                break;
            case BOX:
                start = 27*(rowIndex(key)/3) + 3*(columnIndex(key)/3);
                i = 0;
                for (int k = start; k < start + 3; k++) {
                    container[i++] = k;
                    container[i++] = k + 9;
                    container[i++] = k + 18;
                }
                break;
        }
//        for (int k : container) {
//            System.out.print("K = " + k + ", ");
//        }
//        System.out.println("---------");
        return container;
    }

    Integer[] getRow(Integer key) {
        return getContainer(key, ContainerType.ROW);
    }

    Integer[] getColumn(Integer key) { return getContainer(key, ContainerType.COLUMN); }

    Integer[] getBox(Integer key) { return getContainer(key, ContainerType.BOX); }

    Integer[][] getRows(Integer[] keys) {
        Integer[][] rows = new Integer[keys.length][9];
        for (int i=0; i<keys.length; i++) {
            rows[i] = getRow(keys[i]);
        }
        return rows;
    }

    Integer[][] getColumns(Integer[] keys) {
        Integer[][] columns = new Integer[keys.length][9];
        for (int i=0; i<keys.length; i++) {
            columns[i] = getColumn(keys[i]);
        }
        return columns;
    }

    Integer[][] getContainers() {
        Integer[][] containers = new Integer[27][9];
        int i = 0;
        for (Integer key : representativeKeys) {
            containers[i++] = getRow(key);
            containers[i++] = getColumn(key);
            containers[i++] = getBox(key);
        }
        return containers;
    }

    Integer[][] getBoxContainers() {
        Integer[][] boxContainers = new Integer[9][9];
        int i = 0;
        for (Integer key : representativeKeys) {
            boxContainers[i++] = getBox(key);
        }
        return boxContainers;
    }

//    List<Integer[]> getBoxPairContainers() {
//        List<Integer[]> boxPairContainers = new ArrayList<>();
//        for (Integer[] box1 : getBoxContainers()) {
//            for (Integer[] box2 : getBoxContainers()) {
//                if (box1 != box2) {
//                    if (rowIndex(box1.get(0)) / 3 == rowIndex(box1.get(0)) / 3
//                            || columnIndex(box1.get(0)) / 3 == columnIndex(box1.get(0)) / 3) {
//                        boxPairContainers.add(box1);
//                        boxPairContainers.get(boxPairContainers.size() - 1).addAll(box2);
//                    }
//                }
//            }
//        }
//        return boxPairContainers;
//    }

    /**
     * Returns all connected squares.
     *
     * @param key   the key to the square
     * @return      an array of connected squares
     */
    Integer[] getConnectedSquares(Integer key) {
        Integer[] connectedSquares = new Integer[27];
        Integer[] row = getRow(key);
        Integer[] column = getColumn(key);
        Integer[] box = getBox(key);
        int i = 0;
        for (int j=0; j<9; j++) {
            connectedSquares[i++] = row[j];
            connectedSquares[i++] = column[j];
            connectedSquares[i++] = box[j];
        }
        return connectedSquares;
    }

    public void getNewRandomSudoku() throws Exception {
        if (stringSudokus != null && stringSudokus.size() > 0) {
            int r  = RNG.randInt(0, stringSudokus.size() - 1);
            String sudokuString = stringSudokus.get(r);
            if (sudokuString.length() != 81) {
                throw new Exception("Sudoku string not 81 chars long!");
            }
            squareMap.clear();
            for (int key = 0; key < 81; key++) {
                int fill = Character.getNumericValue(sudokuString.charAt(key));
                squareMap.put(key, new Square(fill, new int[] {}, new int[] {}, fill == 0));
            }
        }
    }

    public static Integer rowIndex(Integer key) { return key / 9; }
    public static Integer columnIndex(Integer key) { return key % 9; }

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

