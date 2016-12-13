package com.example.fredrik.supersudoku;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by fredrik on 11.12.16.
 */

class Square {
    List<Integer> marks;
    private int fill;  // int default is 0 in java

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

        marks = new PrivateArrayList<>();
    }

    boolean setFill(int fill) {
        if (!fixed) {
            if (this.fill != fill) {
                this.fill = fill;
                return true;
            }
        }
        return false;
    }

    int getFill() {
        return fill;
    }

    private class PrivateArrayList<E> extends ArrayList<E> {

        PrivateArrayList() { super(); }

        @Override
        public E get(int index) {
            return super.get(index);
        }

        @Override
        public E set(int index, E element) {
            if (!fixed) {
                return super.set(index, element);
            }
            return null;
        }

        @Override
        public boolean add(E e) {
            if (!fixed) {
                return super.add(e);
            }
            return false;
        }

        @Override
        public void add(int index, E element) {
            if (!fixed) {
                super.add(element);
            }
        }

        @Override
        public E remove(int index) {
            if (!fixed) {
                return super.remove(index);
            }
            return null;
        }

        @Override
        public boolean remove(Object e) {
            if (!fixed) {
                return super.remove(e);
            }
            return false;
        }

        @Override
        public boolean removeAll(Collection<?> list) {
            if (!fixed) {
                return super.removeAll(list);
            }
            return false;
        }
    }
}
