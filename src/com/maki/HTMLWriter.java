package com.maki;

/**
 * Created by trentonmaki on 7/15/15.
 */
public class HTMLWriter {

    private StringReader reader;

    public HTMLWriter(StringReader stringReader) {
        this.reader = stringReader;
    }

    public String getOutput() {
        StringBuilder input = new StringBuilder(reader.getInput());
        boolean first = true;
        for(int index = input.indexOf("*");
                index > -1;
                index = input.indexOf("*", index + 1)) {
            if(first) {
                input.replace(index, index + 1, "<strong>");
            } else {
                input.replace(index, index + 1, "</strong>");
            }
            first = !first;
        }
        return input.toString();
    }
}
