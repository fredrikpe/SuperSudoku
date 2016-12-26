package com.example.fredrik.supersudoku.sudokulogic;

/**
 * Created by fredrik on 11.12.16.
 */

public final class Square {
    public final int fill;

    public final int[] candidates;
    final int[] userRemovedCandidates;

    public final boolean editable;

    Square(int fill, int[] marks, int[] userRemovedMarks, boolean editable) {
        this.fill = fill;
        this.candidates = marks;
        this.userRemovedCandidates = userRemovedMarks;
        this.editable = editable;
    }
}

