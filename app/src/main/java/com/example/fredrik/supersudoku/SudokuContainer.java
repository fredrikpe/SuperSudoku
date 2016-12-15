package com.example.fredrik.supersudoku;

public class SudokuContainer {

    final Integer[] keys;
    final ContainerType type;

    SudokuContainer(ContainerType type, Integer[] keys) {
        this.type = type;
        this.keys = keys;
    }
}

enum ContainerType {
    ROW,
    COLUMN,
    BOX
}
