package com.example.domain.observer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.example.domain.model.Sale;

public class SaleConfirmedPublisher {

    private final List<SaleObserver> observers = new ArrayList<>();

    public void registerObserver(SaleObserver observer) {
        observers.add(Objects.requireNonNull(
                observer,
                "El observador de venta es obligatorio."));
    }

    public void publishSaleConfirmed(Sale sale) {
        SaleConfirmed event = new SaleConfirmed(
                Objects.requireNonNull(
                        sale,
                        "La venta es obligatoria."));

        for (SaleObserver observer : observers) {
            observer.update(event);
        }
    }
}