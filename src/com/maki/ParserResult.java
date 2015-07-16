package com.maki;

import java.util.List;
import java.util.Optional;

/**
 * Created by trentonmaki on 7/15/15.
 */
public class ParserResult {
    private Optional<List<String>> parsed;

    private ParserInput rest;

    public ParserResult(Optional<List<String>> parsed, ParserInput rest) {
        this.parsed = parsed;
        this.rest = rest;
    }

    public Optional<List<String>> getParsed() {
        return parsed;
    }

    public Optional<?> getRest() {
        return rest.rest();
    }
}
