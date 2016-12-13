package com.example.fredrik.supersudoku;

import java.util.Random;

class RNG {

    private static final Random rand = new Random();

    public RNG() {}

    static int randInt(int min, int max) {
        return rand.nextInt((max - min) + 1) + min;
    }
}
