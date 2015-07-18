package com.maki;

import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static com.maki.Parser.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Created by trentonmaki on 7/15/15.
 */
public class ParserTest {

    @Test
    public void literalTest() {
        Parser parser = literal('a');
        ParserResult result = parser.parse("aabaa");
        List<String> resultList = result.getParsed().get();

        assertThat(resultList.size(), equalTo(1));
        assertThat(resultList.get(0), equalTo("a"));
    }

    @Test
    public void literalFailTest() {
        Parser parser = literal('a');
        ParserResult result = parser.parse("bbaa");
        Optional<List<String>> resultOptional = result.getParsed();

        assertThat(resultOptional.isPresent(), equalTo(false));
    }

    @Test
    public void orTestA() {
        Parser parser = or(literal('a'), literal('b'));
        ParserResult result = parser.parse("a");
        List<String> resultList = result.getParsed().get();

        assertThat(resultList.size(), equalTo(1));
        assertThat(resultList.get(0), equalTo("a"));
    }

    @Test
    public void orTestB() {
        Parser parser = or(literal('a'), literal('b'));
        ParserResult result = parser.parse("b");
        List<String> resultList = result.getParsed().get();

        assertThat(resultList.size(), equalTo(1));
        assertThat(resultList.get(0), equalTo("b"));
    }

    @Test
    public void orTestFail() {
        Parser parser = or(literal('a'), literal('b'));
        ParserResult result = parser.parse("c");
        Optional<List<String>> resultOptional = result.getParsed();

        assertThat(resultOptional.isPresent(), equalTo(false));
    }

    @Test
    public void mergeSuccess() {
        Parser parser = merge(literal('a'), literal('b'));
        ParserResult result = parser.parse("abcd");
        List<String> resultList = result.getParsed().get();

        assertThat(resultList.size(), equalTo(1));
        assertThat(resultList.get(0), equalTo("ab"));
    }

    @Test
    public void mergeFailA() {
        Parser parser = merge(literal('a'), literal('b'));
        ParserResult result = parser.parse("acd");
        Optional<List<String>> resultOptional = result.getParsed();

        assertThat(resultOptional.isPresent(), equalTo(false));
    }
    @Test
    public void mergeFailB() {
        Parser parser = merge(literal('a'), literal('b'));
        ParserResult result = parser.parse("bcd");
        Optional<List<String>> resultOptional = result.getParsed();

        assertThat(resultOptional.isPresent(), equalTo(false));
    }
    @Test
    public void mergeFailC() {
        Parser parser = merge(literal('a'), literal('b'));
        ParserResult result = parser.parse("bad");
        Optional<List<String>> resultOptional = result.getParsed();

        assertThat(resultOptional.isPresent(), equalTo(false));
    }


    //--------------------
    @Test
    public void andSuccess() {
        Parser parser = and(literal('a'), literal('b'));
        ParserResult result = parser.parse("abcd");
        List<String> resultList = result.getParsed().get();

        assertThat(resultList.size(), equalTo(2));
        assertThat(resultList.get(0), equalTo("a"));
        assertThat(resultList.get(1), equalTo("b"));
    }

    @Test
    public void andFailA() {
        Parser parser = and(literal('a'), literal('b'));
        ParserResult result = parser.parse("acd");
        Optional<List<String>> resultOptional = result.getParsed();

        assertThat(resultOptional.isPresent(), equalTo(false));
    }
    @Test
    public void andFailB() {
        Parser parser = and(literal('a'), literal('b'));
        ParserResult result = parser.parse("bcd");
        Optional<List<String>> resultOptional = result.getParsed();

        assertThat(resultOptional.isPresent(), equalTo(false));
    }
    @Test
    public void andFailC() {
        Parser parser = and(literal('a'), literal('b'));
        ParserResult result = parser.parse("bad");
        Optional<List<String>> resultOptional = result.getParsed();

        assertThat(resultOptional.isPresent(), equalTo(false));
    }
    //--------------------

    @Test
    public void testAny() {
        Parser parser = any();
        //TODO Think about how to test?
    }

    @Test
    public void testRepeatUntilFail() {
        Parser parser = repeatUntilFail(literal('a'));
        ParserResult result = parser.parse("aaabcasd*asd");
        List<String> resultList = result.getParsed().get();

        assertThat(resultList.size(), equalTo(1));
        assertThat(resultList.get(0), equalTo("aaa"));
    }

    @Test
    public void testRepeatUntilFailInstaFail() {
        Parser parser = repeatUntilFail(literal('a'));
        ParserResult result = parser.parse("baaabcasd*asd");
        Optional<List<String>> resultOptional = result.getParsed();
        assertThat(resultOptional.isPresent(), equalTo(false));
    }

    @Test
    public void testRepeat() {
        Parser parser = repeat(literal('a'), 2);


        ParserResult result = parser.parse("aaabcasd*asd");
        List<String> resultList = result.getParsed().get();

        assertThat(resultList.size(), equalTo(1));
        assertThat(resultList.get(0), equalTo("aa"));
    }

    @Test
    public void testRepeatFail() {
        Parser parser = repeat(literal('a'), 2);
        ParserResult result = parser.parse("ababcasd*asd");
        Optional<List<String>> resultOptional = result.getParsed();


        assertThat(resultOptional.isPresent(), equalTo(false));
    }

    @Test
    public void testAnyExcept() {
        Parser parser = anyExcept('a');
        ParserResult result = parser.parse("baa");
        List<String> resultList = result.getParsed().get();

        assertThat(resultList.size(), equalTo(1));
        assertThat(resultList.get(0), equalTo("b"));
    }

    @Test
    public void testAnyExceptFail() {
        Parser parser = anyExcept('a');
        ParserResult result = parser.parse("ababcasd*asd");
        Optional<List<String>> resultOptional = result.getParsed();


        assertThat(resultOptional.isPresent(), equalTo(false));
    }

    @Test
    public void testRealParsing() {
        //What we want to parse:
        //"ABBB*ABC*ASDAS*"
        //Should be broken into:
        //["ABBB", "*ABC*", "ASDAS*"]

        //Steps:
        //This surrounding text parser can be simplified into:
        //repeat(findFirstInstanceOfSurroundedText)
        //findFirstInstanceOfSurroundedText can be turned into
        //


        Parser parseUntilStar = repeatUntilFail(anyExcept('*'));


        Parser parser = repeatUntilFail(and(parseUntilStar, merge(any(), parseUntilStar)));
        //Repeat until fail is unsafe because it can succeed without advancing the input, which messes with our
        // assumptions. I need to decide which to go with merge then change everything to work with it.
        ParserResult result = parser.parse("ABBB*ABC*ASDAS*");
        List<String> resultList = result.getParsed().get();
        System.out.print("List: ");
        for (String item : resultList) {
            System.out.print(item + ", ");
        }
        assertThat(resultList.size(), equalTo(3));
        assertThat(resultList.get(0), equalTo("ABB"));
        assertThat(resultList.get(1), equalTo("*ABC*"));
        assertThat(resultList.get(2), equalTo("ASDAS*"));

    }

}
