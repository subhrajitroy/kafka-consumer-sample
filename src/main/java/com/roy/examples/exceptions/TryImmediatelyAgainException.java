package com.roy.examples.exceptions;

public class TryImmediatelyAgainException extends RuntimeException{
    public TryImmediatelyAgainException(){
        super("Handling class can immediately try again in a blocking fashion");
    }
}
