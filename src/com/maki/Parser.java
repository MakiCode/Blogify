package com.maki;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Created by trentonmaki on 7/15/15.
 */
public class Parser {
    private Function<ParserInput, Optional<List<String>>> function;

    private Parser(Function<ParserInput, Optional<List<String>>> function) {
        this.function = function;
    }

    /**
     * Return a parser that reads the character specified, then advances input by one. Fails if the character read does
     * not equal compareChar
     *
     * @param compareChar
     * @return
     */
    public static Parser literal(char compareChar) {
        return new Parser((input -> {
            if (input.hasNext()) {
                char inputChar = input.read();
                if (inputChar == compareChar) {
                    input.advance();
                    List<String> results = new ArrayList<>();
                    results.add(String.valueOf(compareChar)); //doesn't matter which one we use
                    return Optional.of(results);
                } else {
                    return Optional.empty();
                }
            } else {
                return Optional.empty();
            }
        }
        ));
    }

    /**
     * Runs the first parser and returns the results, unless it fails. In that case it runs the second parser and
     * returns the results of that parser
     *
     * @param parser1
     * @param parser2
     * @return
     */
    public static Parser or(Parser parser1, Parser parser2) {
        return new Parser((input) -> {
            Optional<List<String>> result1 = parser1.parse(input);
            if (result1.isPresent()) {
                return result1;
            }

            //Logical Or optimization,
            // a || b == if(a) { a } else { b }
            return parser2.parse(input);
        });
    }

    /**
     * Runs the given parser, then merges all the resulting list into one item. Good for grouping results
     *
     * @param parser
     * @return
     */
    public static Parser merge(Parser parser) {
        return new Parser((input) -> {
            if (!input.hasNext()) {
                return Optional.empty();
            }
            int position = input.getIndex();

            Optional<List<String>> parserResult = parser.parse(input);
            if (!parserResult.isPresent()) {
                input.setIndex(position);
                return Optional.empty();
            }

            Optional<List<String>> methodResult = Optional.of(new ArrayList<>());
            methodResult.get().add(Utility.turnListToString(parserResult.get()).toString());
            return methodResult;
        });
    }

    /**
     * Runs the the given parsers in series, one after the other. If any one of the parsers fails
     * it rewinds the input and fails
     *
     * @param parser
     * @param parsers
     * @return
     */
    public static Parser and(Parser parser, Parser... parsers) {
        return new Parser((input) -> {
            if (!input.hasNext()) {
                return Optional.empty();
            }
            int position = input.getIndex();

            Optional<List<String>> result1 = parser.parse(input);
            if (!result1.isPresent()) {
                input.setIndex(position);
                return Optional.empty();
            }

            List<String> results = new ArrayList<>();
            results.add(Utility.turnListToString(result1.get()).toString());

            if (parsers != null) {
                for (Parser p : parsers) {
                    Optional<List<String>> result = p.parse(input);
                    if (!result.isPresent()) {
                        input.setIndex(position);
                        return Optional.empty();
                    }
                    ;
                    results.add(Utility.turnListToString(result.get()).toString());
                }
            }

            return Optional.of(results);
        });
    }

    /**
     * Matches any single character
     * @return
     */
    public static Parser any() {
        return new Parser(input -> {
            if (input.hasNext()) {
                char inputChar = input.read();
                input.advance();
                List<String> result = new ArrayList<>();
                result.add(String.valueOf(inputChar));
                return Optional.of(result);
            } else {
                return Optional.empty();
            }
        });
    }

    /**
     * The logical opposite of literal
     * @param c
     * @return
     */
    public static Parser anyExcept(char c) {
        return new Parser((input -> {
            if (input.hasNext()) {
                char inputChar = input.read();
                if (inputChar == c) {
                    return Optional.empty();
                }
                input.advance();
                List<String> result = new ArrayList<>();
                result.add(String.valueOf(inputChar));
                return Optional.of(result);
            } else {
                return Optional.empty();
            }
        }));
    }

  /*
     * Repeat the parsing N times then return the output.
     *
     * @param parser
     * @param N      must be greater than 0
     * @return
     */
    public static Parser repeat(Parser parser, int N) {
        if (N < 0) {
            throw new IndexOutOfBoundsException("N must be less");
        }

        return new Parser(input -> {
            Optional<List<String>> output = Optional.of(new ArrayList<>());
            Optional<List<String>> parserOutput;

            for (int i = 0; i < N; i++) {
                parserOutput = parser.parse(input);
                if (!parserOutput.isPresent()) {
                    output = Optional.empty();
                    break;
                }
                output.get().addAll(parserOutput.get());
            }
            return output;
        });
    }

    /**
     * Keeps parsing with the given parser until the parser fails. The given parser must parse at least once
     *
     * @param parser
     * @return
     */
    public static Parser repeatUntil(Parser parser) {
        return new Parser(input -> {
            Optional<List<String>> output = Optional.of(new ArrayList<>());
            Optional<List<String>> parserOutput = parser.parse(input);
            while (parserOutput.isPresent()) {
                output.get().addAll(parserOutput.get());
                parserOutput = parser.parse(input);
            }
            if (output.get().size() == 0) {
                return Optional.empty();
            }
            return output;
        });
    }

    public static Parser fail() {
        return new Parser(parserInput -> {
           return Optional.empty();
        });
    }

    public Parser chain(Function<List<String>, Parser> function) {
        Parser that = this;
        return new Parser(parserInput -> {
           Optional<List<String>> result = that.parse(parserInput);
            System.out.println(result);
            if(result.isPresent()) {
                Parser parser = function.apply(result.get());
                return parser.parse(parserInput);
            } else {
              return Optional.empty();
            }
        });
    }
    /**
     * Take the string, and parse it with the defined rule(s)
     *
     * @param input
     * @return
     */
    public ParserResult parse(String input) {
        ParserInput parserInput = new StringInput(input);
        Optional<List<String>> result = this.function.apply(parserInput);
        return new ParserResult(result, parserInput);
    }

    private Optional<List<String>> parse(ParserInput input) {
        return function.apply(input);
    }
}

//TODO check out https://github.com/jneen/parsimmon for a refrence implementation