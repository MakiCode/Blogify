package com.maki;

import java.util.Arrays;
import java.util.List;

/**
 * Created by trentonmaki on 7/16/15.
 */
public class Utility {

    protected static void concatenateBuilder(StringBuilder builder1, StringBuilder builder2) {
        builder1.ensureCapacity(builder1.length() + builder2.length());
        for (int i = 0; i < builder2.length(); i++)
        {
            builder1.append(builder2.charAt(i));
        }
    }

    protected static StringBuilder turnListToString(List<String> list) {
        StringBuilder builder = new StringBuilder("");
        for (String result : list) {
            builder.append(result);
        }
        return builder;
    }
}
