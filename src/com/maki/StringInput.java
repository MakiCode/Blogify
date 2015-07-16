package com.maki;

import java.util.Optional;

/**
 * Basically, a simple character by character buffer
 */
public class StringInput implements ParserInput {
    private String theString;

    private int index = 0;
    public StringInput(String theString) {
        this.theString = theString;
    }

    //TODO make this safe! We don't want index out of bounds errors
    @Override
    public char read() {
        return theString.charAt(index);
    }
    @Override
    public void advance() {
        index += 1;
    }

    public void setIndex(int index) {
        if(index < 0 || index > theString.length() - 1) {
            throw new IndexOutOfBoundsException("Need to set the index between 0 and " + (theString.length() - 1));
        }
        this.index = index;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public boolean hasNext() {
        return index < theString.length();
    }

    @Override
    public Optional<String> rest() {
        return Optional.of(theString.substring(getIndex()));
    }
}
