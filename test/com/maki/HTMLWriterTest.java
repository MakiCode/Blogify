package com.maki;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;


/**
 * Created by trentonmaki on 7/15/15.
 */
public class HTMLWriterTest {
    @Test
    public void boldInput() {
        String input = "*bold text*";
        String output = "<strong>bold text</strong>";

        HTMLWriter HTMLWriter = new HTMLWriter(new StringReader(input));

        String actualOutput = HTMLWriter.getOutput();

        assertThat(actualOutput, equalTo(output));
    }

    @Test
    public void unmatchedBoldInput() {
        String input = "*bold text* not bold but with a star*";
        String output = "<strong>bold text</strong> not bold but with a star*";

        HTMLWriter HTMLWriter  = new HTMLWriter(new StringReader(input));

        String actualOutput = HTMLWriter.getOutput();

        assertThat(actualOutput, equalTo(output));
    }

}