package application.observer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.example.domain.model.Product;
import com.example.domain.model.Sale;
import com.example.domain.observer.SaleConfirmedPublisher;
import com.example.domain.observer.SaleObserver;
import com.example.domain.observer.SaleConfirmed;

class SaleConfirmedPublisherTest {

    @Test
    void notifyObserverOfConfirmedSale() {
        SaleConfirmedPublisher publisher =
                new SaleConfirmedPublisher();

        Product product = new Product(
                "P001",
                "Laptop",
                new BigDecimal("1200.00"),
                10);

        Sale sale = new Sale();
        sale.addDetail(product, 2);

        CapturingObserver observer =
                new CapturingObserver();

        publisher.registerObserver(observer);

        publisher.publishSaleConfirmed(sale);

        assertEquals(1, observer.notificationCount);
        assertSame(sale, observer.receivedSale);
    }

    @Test
    void notifyAllRegisteredObservers() {
        SaleConfirmedPublisher publisher =
                new SaleConfirmedPublisher();

        Product product = new Product(
                "P001",
                "Mouse",
                new BigDecimal("250.00"),
                10);

        Sale sale = new Sale();
        sale.addDetail(product, 1);

        CapturingObserver observer1 =
                new CapturingObserver();

        CapturingObserver observer2 =
                new CapturingObserver();

        publisher.registerObserver(observer1);
        publisher.registerObserver(observer2);

        publisher.publishSaleConfirmed(sale);

        assertEquals(1, observer1.notificationCount);
        assertEquals(1, observer2.notificationCount);

        assertSame(sale, observer1.receivedSale);
        assertSame(sale, observer2.receivedSale);
    }

    private static class CapturingObserver
            implements SaleObserver {

        private int notificationCount;
        private Sale receivedSale;

        @Override
        public void update(SaleConfirmed event) {
            notificationCount++;
            receivedSale = event.getSale();
        }
    }
}