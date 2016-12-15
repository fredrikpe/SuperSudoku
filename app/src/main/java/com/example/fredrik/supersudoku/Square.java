package com.example.fredrik.supersudoku;

/**
 * Created by fredrik on 11.12.16.
 */

final class Square {
    final int fill;

    final int i;
    final int j;

    final int[] marks;
    final int[] userRemovedMarks;

    final boolean editable;

    Square(int i, int j, int fill, int[] marks, int[] userRemovedMarks, boolean fixed) {
        this.i = i;
        this.j = j;
        this.fill = fill;
        this.marks = marks;
        this.userRemovedMarks = userRemovedMarks;
        this.editable = fixed;
    }

    boolean containsMark(int mark) { return contains(marks, mark); }

    boolean containsUserRemovedMark(int mark) { return contains(userRemovedMarks, mark); }

    private boolean contains(int[] ms, int m) {
        for (int i : ms) {
            if (i == m) return true;
        }
        return false;
    }
}
