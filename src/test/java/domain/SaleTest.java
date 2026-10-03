package domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.example.domain.model.Product;
import com.example.domain.model.Sale;
import com.example.domain.model.SaleDetail;

class SaleTest {

    @Test
    void addDetailsAndCalculateExactTotal() {
        Product product1 = new Product("P001", "Laptop", new BigDecimal("1200.00"), 10);
        Product product2 = new Product("P002", "Mouse", new BigDecimal("250.00"), 5);
        Sale sale = new Sale();

        sale.addDetail(product1, 2);
        sale.addDetail(product2, 3);

        assertEquals(2, sale.getDetails().size());
        assertEquals(0, new BigDecimal("2400.00").compareTo(sale.getDetails().get(0).getSubtotal()));
        assertEquals(0, new BigDecimal("750.00").compareTo(sale.getDetails().get(1).getSubtotal()));
        assertEquals(0, new BigDecimal("3150.00").compareTo(sale.getSubtotal()));
        assertEquals(0, new BigDecimal("3654.00").compareTo(sale.getTotal()));
    }

    @Test
    void rejectZeroOrInvalidQuantity() {
        Product product = new Product("P003", "Teclado", new BigDecimal("400.00"), 5);
        Sale sale = new Sale();

        assertThrows(IllegalArgumentException.class, () -> sale.addDetail(product, 0));
        assertThrows(IllegalArgumentException.class, () -> sale.addDetail(product, -2));
    }

    @Test
    void rejectQuantityExceedingStock() {
        Product product = new Product("P006", "Tablet", new BigDecimal("3000.00"), 3);
        Sale sale = new Sale();

        assertThrows(IllegalArgumentException.class, () -> sale.addDetail(product, 10));
    }

    @Test
    void preserveListWhenInsertionFails() {
        Product product = new Product("P004", "Monitor", new BigDecimal("3500.00"), 5);
        Sale sale = new Sale();
        sale.addDetail(product, 1);

        assertThrows(IllegalArgumentException.class, () -> sale.addDetail(product, 0));

        assertEquals(1, sale.getDetails().size());
        assertEquals(0, new BigDecimal("3500.00").compareTo(sale.getSubtotal()));
        assertEquals(0, new BigDecimal("4060.00").compareTo(sale.getTotal()));
    }

    @Test
    void preserveHistoricalPriceWhenCatalogChanges() {
        Product product = new Product("P005", "Impresora", new BigDecimal("600.00"), 5);
        Sale sale = new Sale();

        sale.addDetail(product, 2);
        SaleDetail detail = sale.getDetails().get(0);

        product.setPrice(new BigDecimal("900.00"));

        assertEquals(0, new BigDecimal("1200.00").compareTo(detail.getSubtotal()));
        assertEquals(0, new BigDecimal("1200.00").compareTo(sale.getSubtotal()));
        assertEquals(0, new BigDecimal("1392.00").compareTo(sale.getTotal()));
    }

    @Test
    void preserveTotalWhenMultipleProductPricesChange() {
        Product product1 = new Product("P007", "Monitor", new BigDecimal("3500.00"), 5);
        Product product2 = new Product("P008", "Teclado", new BigDecimal("400.00"), 5);
        Sale sale = new Sale();

        sale.addDetail(product1, 1);
        sale.addDetail(product2, 2);

        assertEquals(0, new BigDecimal("4300.00").compareTo(sale.getSubtotal()));
        assertEquals(0, new BigDecimal("4988.00").compareTo(sale.getTotal()));

        product1.setPrice(new BigDecimal("4500.00"));
        product2.setPrice(new BigDecimal("600.00"));

        assertEquals(0, new BigDecimal("4300.00").compareTo(sale.getSubtotal()));
        assertEquals(0, new BigDecimal("4988.00").compareTo(sale.getTotal()));
    }
}
