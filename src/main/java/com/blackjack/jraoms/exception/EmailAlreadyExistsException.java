package com.blackjack.jraoms.exception;

public class EmailAlreadyExistsException extends RuntimeException{
    public EmailAlreadyExistsException(String message){
        super(message);
    }
}