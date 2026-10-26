package com.example.adapters.outbound.persistence;

import java.util.function.Supplier;

import com.example.ports.outbound.TransactionPort;

public final class HibernateTransactionAdapter implements TransactionPort {

    private final HibernateSessionManager sessionManager;

    public HibernateTransactionAdapter(HibernateSessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public <T> T execute(Supplier<T> operation) {
        return sessionManager.inTransaction(session -> operation.get());
    }
}