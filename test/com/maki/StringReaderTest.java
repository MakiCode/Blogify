package com.maki;

import org.junit.Test;

import static org.junit.Assert.*;

import static org.hamcrest.Matchers.*;


/**
 * Created by trentonmaki on 7/15/15.
 */
public class StringReaderTest {

    @Test
    public void getInput() {
        StringReader reader = new StringReader("ABC");

        assertThat("ABC", equalTo(reader.getInput()));
    }

}