package com.example.fredrik.supersudoku.sudokulogic;

public enum MarkMode {
    NONE,
    FILL,
    CANDIDATE,
    CLEAR;

    public static MarkMode parseText(String text) {
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
