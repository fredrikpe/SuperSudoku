package com.example.fredrik.supersudoku;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Created by fredrik on 11.12.16.
 */

class Square {
    private int fill;  // int default is 0 in java

    Marks marks;

    boolean fixed = false;

    int i;
    int j;

    int box_i;
    int box_j;

    Square(int i, int j) {
        this.i = i;
        this.j = j;
        this.box_i = i / 3;
        this.box_j = j / 3;

        marks = new Marks();
    }

    synchronized boolean setFill(int fill) {
        if (!fixed) {
            if (this.fill != fill) {
                this.fill = fill;
                return true;
            }
        }
        return false;
    }

    synchronized int getFill() {
        return fill;
    }


    class Marks {

        List<Integer> marks;
        List<Integer> userRemovedMarks;

        Iterator<Integer> marksIterator;
        Iterator<Integer> userRemovedMarksIterator;

        Marks() {
            marks = new ArrayList<>();
            userRemovedMarks = new ArrayList<>();
            marksIterator = marks.iterator();
            userRemovedMarksIterator = userRemovedMarks.iterator();
        }

        synchronized int size() {
            return marks.size();
        }

        synchronized boolean contains(Integer i) {
            return marks.contains(i);
        }

        synchronized Integer get(int index) {
            return marks.get(index);
        }

        synchronized public boolean add(Integer e) {
            return !fixed && !userRemovedMarks.contains(e) && marks.add(e);
        }

//        synchronized public boolean addAll(Collection<? extends Integer> collection) {
//            return !fixed && marks.addAll(collection);
//        }

//        synchronized Integer remove(int index) {
//            if (!fixed) {
//                Integer i = marks.remove(index);
//                if (i != null) {
//                    userRemovedMarks.add(i);
//                    return i;
//                }
//            }
//            return null;
//        }

        synchronized boolean remove(Integer i) {
            if (!fixed) {
                if (marks.remove(i)) {
                    userRemovedMarks.add(i);
                    return true;
                }
            }
            return false;
        }

        synchronized Iterable<Integer> iterable() {
            return marks;
        }
    }
}
