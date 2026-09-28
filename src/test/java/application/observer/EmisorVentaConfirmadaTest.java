package application.observer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.example.domain.model.Product;
import com.example.domain.model.Sale;
import com.example.domain.observer.EmisorVentaConfirmada;
import com.example.domain.observer.ObservadorVenta;
import com.example.domain.observer.VentaConfirmada;

class EmisorVentaConfirmadaTest {

    @Test
    void notificaLaVentaConfirmadaAlObservador() {
        EmisorVentaConfirmada emisor =
                new EmisorVentaConfirmada();

        Product product = new Product(
                "P001",
                "Laptop",
                new BigDecimal("1200.00"),
                10);

        Sale sale = new Sale();
        sale.agregarDetalle(product, 2);

        CapturingObserver observer =
                new CapturingObserver();

        emisor.registrarObservador(observer);

        emisor.notificarVentaConfirmada(sale);

        assertEquals(1, observer.notificationCount);
        assertSame(sale, observer.receivedSale);
    }

    @Test
    void notificaATodosLosObservadoresRegistrados() {
        EmisorVentaConfirmada emisor =
                new EmisorVentaConfirmada();

        Product product = new Product(
                "P001",
                "Mouse",
                new BigDecimal("250.00"),
                10);

        Sale sale = new Sale();
        sale.agregarDetalle(product, 1);

        CapturingObserver observer1 =
                new CapturingObserver();

        CapturingObserver observer2 =
                new CapturingObserver();

        emisor.registrarObservador(observer1);
        emisor.registrarObservador(observer2);

        emisor.notificarVentaConfirmada(sale);

        assertEquals(1, observer1.notificationCount);
        assertEquals(1, observer2.notificationCount);

        assertSame(sale, observer1.receivedSale);
        assertSame(sale, observer2.receivedSale);
    }

    private static class CapturingObserver
            implements ObservadorVenta {

        private int notificationCount;
        private Sale receivedSale;

        @Override
        public void actualizar(VentaConfirmada evento) {
            notificationCount++;
            receivedSale = evento.getVenta();
        }
    }
}