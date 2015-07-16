package com.maki;

import java.util.Optional;

/**
 * Created by trentonmaki on 7/15/15.
 */
public interface ParserInput {
    char read();

    void advance();

    void setIndex(int index);

    int getIndex();

    boolean hasNext();

    Optional<?> rest();
}
