package com.example.fredrik.supersudoku.customs;

public class Array {

    public static <T> boolean contains(final T[] array, final T v) {
        if (v == null) {
            for (final T e : array)
                if (e == null)
                    return true;
        } else {
            for (final T e : array)
                if (e == v || v.equals(e))
                    return true;
        }
        return false;
    }

    public static boolean contains(final int[] array, final int v) {
        for (final int e : array)
            if (e == v)
                return true;
        return false;
    }
}
