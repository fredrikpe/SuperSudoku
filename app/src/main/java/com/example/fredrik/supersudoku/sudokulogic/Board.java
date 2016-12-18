package com.example.fredrik.supersudoku.sudokulogic;

import com.example.fredrik.supersudoku.RNG;
import com.example.fredrik.supersudoku.asdflaksd.Array;
import com.example.fredrik.supersudoku.asdflaksd.EventListener;

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
     * The main data object. Holds all the keys by key values corresponding to
     * position on board.
     * 0    1   2 ...
     * 9    10  11 ...
     * ...
     */
    public ConcurrentMap<Integer, Square> squareMap;

    public Hint hint;

    private Stack<Move> moves;
    private Stack<Move> userMoves;

    public List<String> stringSudokus;

    private List<Integer> representativeKeys;
    private List<EventListener> eventListeners;

    public Board() {
        squareMap = new ConcurrentHashMap<>();
        moves = new Stack<>();
        userMoves = new Stack<>();
        representativeKeys = new ArrayList<>();
        stringSudokus = new ArrayList<>();
        eventListeners = new ArrayList<>();

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                squareMap.put(key(i, j), new Square(0, new int[] {}, new int[] {},true));
            }
            representativeKeys.add(key(i, i/3 + (i%3)*3));
        }

        for ( int key : representativeKeys)
            System.out.println(key);
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
        for (EventListener listener : eventListeners) {
            listener.onChangeEvent();
        }
    }

    public void hint() {
        new HintTask().execute(this);
    }

    void hintTaskFinished(Hint result) {
        hint = result;
        if (hint != null) {
            System.out.println("Hint. Number = " + hint.number +  ", key = " + hint.key);
            System.out.println("Container:");
            for (Integer key : hint.container) {
                System.out.print(key + ", ");
            }
            System.out.println("-------");

            for (EventListener listener : eventListeners) {
                listener.onHintFoundEvent(hint.number);
            }
        }
    }

    /**
     * Called when a change to the board state by the user happens. Initiates the assistant task.
     */
    public void changeOccurred() {
        new AssistantTask().execute(this);
    }

    public void addEventListener(EventListener listener) {
        eventListeners.add(listener);
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
        for (EventListener listener : eventListeners) {
            listener.onChangeEvent();
        }
        if (result) {
            new AssistantTask().execute(this);
        }
    }

    Integer[] getContainer(Integer key, ContainerType type) {
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

    Integer[][] getBoxPairContainers() {
        Integer[][] boxPairContainers = new Integer[18][18];
        Integer[] box1, box2;
        int c = 0;
        for (Integer key1 : representativeKeys) {
            box1 = getBox(key1);
            for (Integer key2 : representativeKeys) {
                box2 = getBox(key2);
                if (!key1.equals(key2)) {
                    if ((key1 / 9) / 3 == (key2 / 9) / 3 || (key1 % 9) / 3 == (key2 % 9) / 3) {
                        // Same box row or box column
                        for (int i = 0; i < 9; i++) {
                            boxPairContainers[c][i] = box1[i];
                            boxPairContainers[c][i + 9] = box2[i];
                        }
                    }
                }
            }
        }
        for (Integer[] pair : boxPairContainers) {
            for (Integer key : pair) {
                assert key != null;
            }
        }
        return boxPairContainers;
    }

    /**
     * Returns all connected keys.
     *
     * @param key   the key to the square
     * @return      an array of connected keys
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
    public static Integer containerIndex(Integer key, ContainerType type) {
        switch (type) {
            case ROW: return rowIndex(key);
            case COLUMN: return columnIndex(key);
            default: throw new IllegalArgumentException("Boxes don't have container indexes.");
        }
    }

    private int[] addRemoveCandidates(Square square, int candidate) {
        for (int m : square.candidates) {
            if (m == candidate) return removeCandidate(square.candidates, candidate);
        }
        if (!Array.contains(square.userRemovedCandidates, candidate)) {
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

