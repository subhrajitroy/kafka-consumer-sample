package com.roy.examples.exceptions;

public class TryAfterSometimeException extends RuntimeException{

    public TryAfterSometimeException(){
        super("Handling class can try after sometime in a non-blocking fashion");
    }
}
