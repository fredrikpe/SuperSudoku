package com.example.fredrik.supersudoku;

/**
 * Created by fredrik on 11.12.16.
 */

final class Square {
    final int fill;

    final int i;
    final int j;

    final int[] marks;

    final boolean editable;

    Square(int i, int j, int fill, int[] marks, boolean fixed) {
        this.i = i;
        this.j = j;
        this.fill = fill;
        this.marks = marks;
        this.editable = fixed;
    }

    boolean containsMark(int mark) {
        for (int i : marks) {
            if (i == mark) return true;
        }
        return false;
    }
}
