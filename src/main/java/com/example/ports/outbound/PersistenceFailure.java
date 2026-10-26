package com.example.ports.outbound;

public class PersistenceFailure extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public PersistenceFailure(String message, Throwable cause) {
        super(message, cause);
    }
}