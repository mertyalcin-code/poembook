package com.poembook.poembook.core.exception.entities;

public class EmailNotFoundException extends Exception {
    public EmailNotFoundException(String message) {
        super(message); // Exception classını çağırdım.
    }
}
