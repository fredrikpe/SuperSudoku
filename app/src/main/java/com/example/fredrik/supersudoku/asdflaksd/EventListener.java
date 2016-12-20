package com.example.fredrik.supersudoku.asdflaksd;

import com.example.fredrik.supersudoku.sudokulogic.Hint;

public interface EventListener {
    void onChangeEvent();
    void onHintFoundEvent(Hint hint);
}
