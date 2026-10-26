package com.example.adapters.outbound.persistence;

import java.util.function.Function;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.example.ports.outbound.PersistenceFailure;

public final class HibernateSessionManager {

    private final SessionFactory sessionFactory;
    private final ThreadLocal<Session> currentSession = new ThreadLocal<>();

    public HibernateSessionManager(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public <T> T inTransaction(Function<Session, T> operation) {
        Session activeSession = currentSession.get();
        if (activeSession != null) {
            return operation.apply(activeSession);
        }

        try (Session session = sessionFactory.openSession()) {
            return executeWithSession(session, operation);
        } catch (HibernateException exception) {
            throw new PersistenceFailure("No fue posible abrir o cerrar la sesión de persistencia.",
                    exception);
        }
    }

    private <T> T executeWithSession(Session session, Function<Session, T> operation) {
        Transaction transaction = session.beginTransaction();
        currentSession.set(session);
        try {
            T result = operation.apply(session);
            transaction.commit();
            return result;
        } catch (HibernateException exception) {
            rollback(transaction, exception);
            throw new PersistenceFailure("No fue posible completar la operación de persistencia.",
                    exception);
        } catch (RuntimeException exception) {
            rollback(transaction, exception);
            throw exception;
        } finally {
            currentSession.remove();
        }
    }

    private void rollback(Transaction transaction, Throwable originalFailure) {
        if (!transaction.isActive()) {
            return;
        }
        try {
            transaction.rollback();
        } catch (HibernateException rollbackFailure) {
            originalFailure.addSuppressed(rollbackFailure);
        }
    }
}