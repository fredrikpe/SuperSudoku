package com.example.fredrik.supersudoku.sudokulogic;

public abstract class Hint {
    public final Integer[] container;
    public final int number;
    public final Integer key;

    protected Hint(Integer[] container, int number, Integer key) {
        this.container = container;
        this.number = number;
        this.key = key;
    }
}

class UniqueCandidate extends Hint {
    // redbox the square, diffhighlight the container, highlight the number.

    UniqueCandidate(Integer[] container, int number, Integer key) {
        super(container, number, key);
    }
}

class BoxElimination extends Hint {
    // Should regular highlight the number in question. And different highlight the row/column.
    // And box the square.

    BoxElimination(Integer[] container, int number, Integer key) {
        super(container, number, key);
    }
}

class BoxPairElimination extends Hint {
    // Should regular highlight the number in question. And different highlight the row/column.
    // And box the square.

    BoxPairElimination(Integer[] container, int number, Integer key) {
        super(container, number, key);
    }

}

class NakedSubset extends Hint {
    // DiffHighlight the container, bluebox the subset and redbox the elimination square.

    NakedSubset(Integer[] container, int number, Integer key) {
        super(container, number, key);
    }
}
