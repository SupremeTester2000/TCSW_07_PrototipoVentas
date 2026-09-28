package com.example.domain.observer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.example.domain.model.Sale;

public class EmisorVentaConfirmada {

    private final List<ObservadorVenta> observadores = new ArrayList<>();

    public void registrarObservador(ObservadorVenta observador) {
        observadores.add(Objects.requireNonNull(
                observador,
                "El observador de venta es obligatorio."));
    }

    public void notificarVentaConfirmada(Sale venta) {
        VentaConfirmada evento = new VentaConfirmada(
                Objects.requireNonNull(
                        venta,
                        "La venta es obligatoria."));

        for (ObservadorVenta observador : observadores) {
            observador.actualizar(evento);
        }
    }
}