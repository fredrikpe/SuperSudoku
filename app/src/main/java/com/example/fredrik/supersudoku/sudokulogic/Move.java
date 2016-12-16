package com.example.fredrik.supersudoku.sudokulogic;

/**
 * Created by fredrik on 16.12.16.
 */

public class Move {

    public final Integer key;
    public final Square oldSquare;
    public final Square newSquare;

    public Move(Integer key, Square oldSquare, Square newSquare) {
        this.key = key;
        this.oldSquare = oldSquare;
        this.newSquare = newSquare;
    }
}
