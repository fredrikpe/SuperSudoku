package com.example.fredrik.supersudoku.customs;

import com.example.fredrik.supersudoku.sudokulogic.Hint;

public interface EventListener {
    void onChangeEvent();
    void onHintFoundEvent(Hint hint);
}
