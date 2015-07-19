package com.maki;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by trentonmaki on 7/15/15.
 */
public class Parser {
    private ParserFunction function;

    @FunctionalInterface
    private interface ParserFunction {
        /**
         * Parse the input merge return the results or Optional.empty() if failed. Methods that use input.read()
         * must use input.hasNext() to ensure that they have something to read. Any method which uses input.advance()
         * must take care to reverse any advancements if future processing reveals that parsing to be illegal (see merge()
         * ) for an example of this
         *
         * @param input
         * @return
         */
        Optional<List<String>> parse(ParserInput input);
    }

    private Parser(ParserFunction function) {
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
     * Or runs the first parser merge returns it's results, unless it fails. In that case, it runs the second parser merge
     * returns the results of that parser
     *
     * @param parser1
     * @param parser2
     * @return
     */
    public static Parser or(Parser parser1, Parser parser2) {
        return new Parser((input) -> {
            Optional<List<String>> result1 = parser1.parse(input);
//            int position = input.getIndex();
            if (result1.isPresent()) {
                return result1;
            }

//            input.setIndex(position);
            //Logical Or optimization,
            // a || b == if(a) { a } else { b }
            return parser2.parse(input);
        });
    }

    /**
     * Runs the two parsers in series, first parser1, then parser2, and merges the result. If either parser fails, it rewinds the input.
     *
     * @param parser1
     * @param parsers
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
     * Runs the two parsers in series, first parser1, then parser2.. If it fails, it rewinds the input.
     * Ths does not merge the result
     *
     * @param parser1
     * @param parsers
     * @return
     */
    public static Parser and(Parser parser1, Parser... parsers) {
        return new Parser((input) -> {
            if (!input.hasNext()) {
                return Optional.empty();
            }
            int position = input.getIndex();

            Optional<List<String>> result1 = parser1.parse(input);
            if (!result1.isPresent()) {
                input.setIndex(position);
                return Optional.empty();
            }

            List<String> results = new ArrayList<>();
            results.add(Utility.turnListToString(result1.get()).toString());

            if (parsers != null) {
                for (Parser parser : parsers) {
                    Optional<List<String>> result = parser.parse(input);
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


    private static boolean rewindIfNotPresent(ParserInput input, int position, Optional<?> optional) {
        if (!optional.isPresent()) {
            //Rewind input if failed
            return true;
        }
        return false;
    }


    /**
     * reads any character, then advances input by one
     *
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

    /**
     *
     */

    /**
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
    public static Parser repeatUntilFail(Parser parser) {
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

    public ParserResult parse(String input) {
        ParserInput parserInput = new StringInput(input);
        Optional<List<String>> result = this.function.parse(parserInput);
        return new ParserResult(result, parserInput);
    }

    private Optional<List<String>> parse(ParserInput input) {
        return function.parse(input);
    }
}
