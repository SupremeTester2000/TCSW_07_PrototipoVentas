package com.example.ports.outbound;

import java.util.function.Supplier;

public interface TransactionPort {

    <T> T execute(Supplier<T> operation);

    default void execute(Runnable operation) {
        execute(() -> {
            operation.run();
            return null;
        });
    }
}