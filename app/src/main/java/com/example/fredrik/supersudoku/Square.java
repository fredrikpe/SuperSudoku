package com.example.fredrik.supersudoku;

/**
 * Created by fredrik on 11.12.16.
 */

final class Square {
    final int fill;

    final int i;
    final int j;

    final int[] candidates;
    final int[] userRemovedCandidates;

    final boolean editable;

    Square(int i, int j, int fill, int[] marks, int[] userRemovedMarks, boolean editable) {
        this.i = i;
        this.j = j;
        this.fill = fill;
        this.candidates = marks;
        this.userRemovedCandidates = userRemovedMarks;
        this.editable = editable;
    }

    boolean candidatesContains(int mark) { return contains(candidates, mark); }

    boolean userRemovedCandidatesContains(int mark) { return contains(userRemovedCandidates, mark); }

    private boolean contains(int[] ms, int m) {
        for (int i : ms) {
            if (i == m) return true;
        }
        return false;
    }
}
