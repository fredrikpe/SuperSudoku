package com.example.fredrik.sudoku;

enum SudokuMode {
    NONE,
    FILL,
    MARK;

    static SudokuMode parseText(String text) {
        switch (text) {
            case "Fill":
                return FILL;
            case "Mark":
                return MARK;
            default:
                throw new IllegalArgumentException("Illegal mode. Wrong button text?");
        }
    }
}
