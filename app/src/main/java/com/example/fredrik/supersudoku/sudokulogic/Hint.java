package com.example.fredrik.supersudoku.sudokulogic;

public abstract class Hint {
    public final Integer[] container;
    public final int number;
    public final Integer key;
    public final boolean remove_candidate;

    protected Hint(Integer[] container, int number, Integer key, boolean rc) {
        this.container = container;
        this.number = number;
        this.key = key;
        this.remove_candidate = rc;
    }

    abstract String string();
}

class SingleCandidate extends Hint {
    // redbox the square, highlight the number.

    SingleCandidate(Integer[] container, int number, Integer key) {
        super(container, number, key, false);
    }

    @Override
    String string() {
        return "Consider the square at (" + Board.rowIndex(key) + ", " + Board.columnIndex(key) +
                "). It has only " + number + " as a possible candidate.";
    }
}

class UniqueCandidate extends Hint {
    // redbox the square, diffhighlight the container, highlight the number.

    UniqueCandidate(Integer[] container, int number, Integer key) {
        super(container, number, key, false);
    }

    @Override
    String string() {
        return "Consider the square at (" + Board.rowIndex(key) + ", " + Board.columnIndex(key) +
                "). It is the only square in the grayed out container with " + number +
                " as a possible candidate.";
    }
}

class BoxElimination extends Hint {
    // Should regular highlight the number in question. And different highlight the row/column.
    // And box the square.

    BoxElimination(Integer[] container, int number, Integer key) {
        super(container, number, key, true);
    }

    @Override
    String string() {
        return "Consider the square at (" + Board.rowIndex(key) + ", " + Board.columnIndex(key) +
                "). " + number + " can only be put in one row/column in a neighbouring box, so " +
                " it can be removed as a candidate.";
    }
}

class BoxPairElimination extends Hint {
    // Should regular highlight the number in question. And different highlight the row/column.
    // And box the square.

    BoxPairElimination(Integer[] container, int number, Integer key) {
        super(container, number, key, true);
    }

    @Override
    String string() {
        return "Consider the square at (" + Board.rowIndex(key) + ", " + Board.columnIndex(key) +
                "). " + number + " can only be put in two rows/columns in the neighbouring boxes, so " +
                " it can be removed as a candidate in this box's corresponding rows/columns.";
    }
}

class NakedSubset extends Hint {
    // DiffHighlight the container, bluebox the subset and redbox the elimination square.

    final int[] subset;

    NakedSubset(Integer[] container, int number, Integer key, int[] subset) {
        super(container, number, key, true);
        this.subset = subset;
    }

    @Override
    String string() {
        String ss = "";
        for (int i : subset)
            ss += i + ", ";

        return "Consider the grayed out container. The numbers " + ss + "are confined to exactly " +
                subset.length + " squares, so they can be removed as candidates from the " +
                "remaining squares in the container.";
    }
}
