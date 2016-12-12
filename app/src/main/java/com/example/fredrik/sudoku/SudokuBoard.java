package com.example.fredrik.sudoku;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by fredrik on 11.12.16.
 */

public class SudokuBoard {

    List<Square> squares;
    List<Square> representativeSquares;

    TwoSquareInterface sameRowOperator = (s, t) -> s.i == t.i;
    TwoSquareInterface sameColumnOperator = (s, t) -> s.j == t.j;
    TwoSquareInterface sameBoxOperator = (s, t) -> s.box_i == t.box_i && s.box_j == t.box_j;

    SudokuBoard() {
        squares = new ArrayList<>();
        representativeSquares = new ArrayList<>();

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                squares.add(new Square(i, j));
            }
            representativeSquares.add(new Square(i, i/3 + (i%3)*3));
        }
    }

    Square getSquare(int i, int j) {
        Square s = squares.get(i * 9 + j);
        assert s.i == i && s.j == j;
        return s;
    }

    interface TwoSquareInterface {
        boolean operator(Square s, Square t);
    }

    private List<Square> getContainer(Square s, TwoSquareInterface op) {
        List<Square> container = new ArrayList<>();
        for (Square t : squares) {
            if (op.operator(s, t)) {
                container.add(t);
            }
        }
        return container;
    }

    List<Square> getRow(Square s) {
        return getContainer(s, sameRowOperator);
    }

    List<Square> getColumn(Square s) {
        return getContainer(s, sameColumnOperator);
    }

    List<Square> getBox(Square s) {
        return getContainer(s, sameBoxOperator);
    }

    List<Square> getRows(List<Square> squares) {
        List<Square> rows = new ArrayList<>();
        for (Square s : squares) {
            rows.addAll(getRow(s));
        }
        return rows;
    }

    List<Square> getColumns(List<Square> squares) {
        List<Square> columns = new ArrayList<>();
        for (Square s : squares) {
            columns.addAll(getColumn(s));
        }
        return columns;
    }

    List<List<Square>> getContainers() {
        List<List<Square>> containers = new ArrayList<>();
        for (Square s : representativeSquares) {
            containers.add(getRow(s));
            containers.add(getColumn(s));
            containers.add(getBox(s));
        }
        return containers;
    }

    List<List<Square>> getBoxContainers() {
        List<List<Square>> boxContainers = new ArrayList<>();
        for (Square s : representativeSquares) {
            boxContainers.add(getBox(s));
        }
        return boxContainers;
    }

    List<List<Square>> getBoxPairContainers() {
        List<List<Square>> boxPairContainers = new ArrayList<>();

        for (List<Square> box1 : getBoxContainers()) {
            for (List<Square> box2 : getBoxContainers()) {
                if (box1 != box2) {
                    if (box1.get(0).box_i == box2.get(0).box_i || box1.get(0).box_j == box2.get(0).box_j) {
                        boxPairContainers.add(box1);
                        boxPairContainers.get(boxPairContainers.size() - 1).addAll(box2);
                    }
                }
            }
        }
        return boxPairContainers;
    }

    List<Square> getConnectedSquares(Square square) {
        List<Square> squareList = new ArrayList<>();
        squareList.add(square);
        List<Square> connectedSquares = getRows(squareList);
        connectedSquares.addAll(getColumns(squareList));
        connectedSquares.addAll(getBox(square));
        connectedSquares.removeAll(Arrays.asList(square));
        return connectedSquares;
    }
}
