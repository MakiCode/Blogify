package com.maki;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


/**
 * Created by trentonmaki on 7/15/15.
 */
public class MiscTests {
    @Test
    public void stringReplace() {
        StringBuilder string = new StringBuilder("abcde");
        String expected = "abPPPde";
        assertThat(string.replace(2,3, "PPP").toString(), equalTo(expected));
    }


}
