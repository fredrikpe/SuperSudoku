package com.example.fredrik.supersudoku;

enum SudokuMode {
    NONE,
    FILL,
    CANDIDATE;

    static SudokuMode parseText(String text) {
        switch (text) {
            case "Fill":
                return FILL;
            case "Candidate":
                return CANDIDATE;
            default:
                throw new IllegalArgumentException("Illegal mode. Wrong button text?");
        }
    }
}
