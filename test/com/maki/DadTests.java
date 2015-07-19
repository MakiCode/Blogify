package com.maki;

import org.junit.Test;

import java.util.Enumeration;
import java.util.StringTokenizer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;


/**
 * Created by trentonmaki on 7/15/15.
 */
public class DadTests {

    @Test
    public void somethingStupidToStart() {
        String firstPass = "This is a `document` that allows me to tokenize my input";

        StringTokenizer st = new StringTokenizer(firstPass);
        while (st.hasMoreElements()) {
            Object element = st.nextElement();
            if (element.toString().startsWith("`")) {
                String thing = String.class.cast(element);
                thing = thing.substring(1, thing.length() -1);
                element = "*" + thing + "*";
            }
            System.out.println("object is: " + element);
        }
    }

}
