package com.maki;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by trentonmaki on 7/15/15.
 */
public class Parser {
    private ParserFunction function;
    private boolean collapsable;

    @FunctionalInterface
    private interface ParserFunction {
        /**
         * Parse the input and return the results or Optional.empty() if failed. Methods that use input.read()
         * must use input.hasNext() to ensure that they have something to read. Any method which uses input.advance()
         * must take care to reverse any advancements if future processing reveals that parsing to be illegal (see and()
         * ) for an example of this
         *
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
        );
    }

    /**
     * Or runs the first parser and returns it's results, unless it fails. In that case, it runs the second parser and
     * returns the results of that parser
     * @param parser1
     * @param parser2
     * @return
     */
    public static Parser or(Parser parser1, Parser parser2) {
        return new Parser((input)-> {
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
     * Runs the two parsers in series, first parser1, then parser2. If it fails, it rewinds the input.
     *
     * @param parser1
     * @param parser2
     * @return
     */
    public static Parser and(Parser parser1, Parser parser2) {
        return new Parser((input, result)-> {
            int position = input.getIndex();
            Optional<List<String>> result1 = parser1.parse(input);

            if (!result1.isPresent()) {
                input.setIndex(position); //Rewind input if failed
                return Optional.empty();
            }

            Optional<List<String>> result2 = parser2.parse(input);
            if (!result2.isPresent()) {
                input.setIndex(position); //Rewind input if failed
                return Optional.empty();
            }

            return Optional.of(result1.get() + result2.get());
        });
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
                return Optional.of(String.valueOf(inputChar));
            } else {
                return Optional.empty();
            }
        });
    }

    /**
     *
     */

    /**
     * Repeat the parsing N times then return the output.
     *
     * @param parser
     * @param N must be greater than 0
     * @return
     */
    public static Parser repeat(Parser parser, int N) {
        if (N < 0) {
            throw new IndexOutOfBoundsException("N must be less");
        }

        return new Parser(input -> {
            StringBuilder output = new StringBuilder("");
            Optional<String> parserOutput;

            for (int i = 0; i < N; i++) {
                parserOutput = parser.parse(input);
                if (!parserOutput.isPresent()) {
                    output = new StringBuilder(""); //because, in this method we use the empty string to signify a
                    // failure. And we do that because someone was worried about efficiency and decided to use
                    // a StringBuilder
                    break;
                }
                output.append(parserOutput.get());
            }

            String outputString = output.toString();
            if (outputString.equals("")) {
                return Optional.empty();
            }
            return Optional.of(outputString);
        });
    }

    /**
     * Keeps parsing with the given parser until the parser fails. This is the only parser that can legally return an
     * empty string
     * @param parser
     * @return
     */
    public static Parser repeatUntilFail(Parser parser) {
        return new Parser(input -> {
            StringBuilder output = new StringBuilder("");
            Optional<String> parserOutput = parser.parse(input);
            while (parserOutput.isPresent()) {
                output.append(parserOutput.get());
                parserOutput = parser.parse(input);
            }
            return Optional.of(output.toString());
        });
    }

    public ParserResult parse(String input) {
        ParserInput parserInput = new StringInput(input);
        Optional<List<String>> result = this.function.parse(parserInput);
        return new ParserResult(result, parserInput);
    }

    private Optional<List<String>> parse(ParserInput input) {
        return function.parse(input, new ArrayList<String>());
    }
}
