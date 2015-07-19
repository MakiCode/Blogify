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
        //TODO
        Parser parser = merge(and(literal('a'), literal('b')));
        ParserResult result = parser.parse("abcd");
        List<String> resultList = result.getParsed().get();

        assertThat(resultList.size(), equalTo(1));
        assertThat(resultList.get(0), equalTo("ab"));
    }

    @Test
    public void mergeFail() {
        //TODO
        Parser parser = merge(and(literal('a'), literal('b')));
        ParserResult result = parser.parse("acd");
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
    public void andMultiSuccess() {
        Parser parser = and(literal('a'), literal('b'), literal('c'), literal('d'));
        ParserResult result = parser.parse("abcdAAA");
        List<String> resultList = result.getParsed().get();

        assertThat(resultList.size(), equalTo(4));
        assertThat(resultList.get(0), equalTo("a"));
        assertThat(resultList.get(1), equalTo("b"));
        assertThat(resultList.get(2), equalTo("c"));
        assertThat(resultList.get(3), equalTo("d"));
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
        ParserResult result1 = parser.parse("abc");
        ParserResult result2 = parser.parse("\u8900as");
        ParserResult result3 = parser.parse("*sdf");

        assertThat(result1.getParsed().get().size(), equalTo(1));
        assertThat(result1.getParsed().get().get(0), equalTo("a"));
        assertThat(result2.getParsed().get().size(), equalTo(1));
        assertThat(result2.getParsed().get().get(0), equalTo("\u8900"));
        assertThat(result3.getParsed().get().size(), equalTo(1));
        assertThat(result3.getParsed().get().get(0), equalTo("*"));
    }

    @Test
    public void testRepeatUntilFail() {
        Parser parser = repeatUntilFail(literal('a'));
        ParserResult result = parser.parse("aaabcasd*asd");
        List<String> resultList = result.getParsed().get();

        assertThat(resultList.size(), equalTo(3));
        assertThat(resultList.get(0), equalTo("a"));
        assertThat(resultList.get(1), equalTo("a"));
        assertThat(resultList.get(2), equalTo("a"));
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

        assertThat(resultList.size(), equalTo(2));
        assertThat(resultList.get(0), equalTo("a"));
        assertThat(resultList.get(1), equalTo("a"));
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

    //    @Test
//    public void testRepeatUnless() {
//        Parser parser = repeat(literal('b'));
//        ParserResult result = parser.parse("bbadsda");
//
//        assertThat(result.getParsed().isPresent(), equalTo(false));
//    }
//
//    @Test
//    public void testRepeatUnless2() {
//        Parser parser = repeatUnless(and(literal('a'), literal('b')));
//        ParserResult result = parser.parse()
//    }
//
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
        Parser parseBold = merge(and(any(), parseUntilStar, any()));
        Parser findSecondStar = ;
         //This needs to succeed if there are no stars left in the input, and fail if there are stars left in the input
        Parser parser = and(
                                parseUntilStar,
                                or(
                                        findSecondStar,
                                        parseBold
                                )
                        );

        //State machine with three states:
        //Reading random text
        //Hit a one *
        //Hit a second *

        //passUntilStar we need something that will fail once it hits a fail, but also has the function
        //of repeat

        //What I want is a parser that goes until the second star, but leaves the input at the first star
//        Parser  parser = repeatUntilFail(or(merge(parseUntilStar), parseBold));


        ParserResult result = parser.parse("ABBB*ABC*ASDAS*");
        List<String> resultList = result.getParsed().get();
        System.out.print("List: ");
        for (String item : resultList) {
            System.out.print(item + ", ");
        }
        System.out.print("rest: '" + result.getRest().get() + "'");
        assertThat(resultList.size(), equalTo(2));
        assertThat(resultList.get(0), equalTo("ABBB"));
        assertThat(resultList.get(1), equalTo("*ABC*"));
        assertThat(resultList.get(2), equalTo("ASDAS*")); //So... this works. But I need to test it on multiple
        //star strings (it should work fine) and then I need to find some way to add the rest() into the results
        //Also, converting strings to tokens?????? TODO the stuff mentioned and I need to modification make a
        // modification to the repeat(), merging and non merging kinds. I think I need a separate function which does
        // merging. But I need to make sure that this function doesn't have the problems that repeatUntilFail() has
        // (successful parsing without advancing input)

    }

}
