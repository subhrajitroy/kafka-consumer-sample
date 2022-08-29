package com.roy.examples.exceptions;

public class FatalException extends RuntimeException{
    public FatalException(){
        super("This is an unrecoverable exception");
    }
}
