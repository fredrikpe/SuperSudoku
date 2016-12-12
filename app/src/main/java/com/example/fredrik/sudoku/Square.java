package com.example.fredrik.sudoku;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fredrik on 11.12.16.
 */

public class Square {
    List<Integer> marks;
    int fill;  // int default is 0 in java

    int i;
    int j;

    int box_i;
    int box_j;

    public Square(int i, int j) {
        this.i = i;
        this.j = j;
        this.box_i = i / 3;
        this.box_j = j / 3;

        marks = new ArrayList<>();

        for (int u=1; u<10; u++)
            marks.add(u);
    }
}
