package domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.example.domain.factory.DefaultSaleFactory;
import com.example.domain.model.Product;
import com.example.domain.model.Sale;
import com.example.domain.model.SaleDetail;

class SaleFactoryTest {

    private final DefaultSaleFactory factory = new DefaultSaleFactory();

    @Test
    void createValidDetail() {
        Product product = new Product("P001", "Laptop", new BigDecimal("1200.00"), 10);

        SaleDetail detail = factory.createSaleDetail(product, 2);

        assertNotNull(detail);
        assertEquals(product, detail.getProduct());
        assertEquals(2, detail.getQuantity());
        assertEquals(0, new BigDecimal("2400.00").compareTo(detail.getSubtotal()));
    }

    @Test
    void rejectNullProduct() {
        assertThrows(IllegalArgumentException.class, () -> factory.createSaleDetail(null, 1));
    }

    @Test
    void rejectZeroQuantity() {
        Product product = new Product("P001", "Laptop", new BigDecimal("1200.00"), 10);
        assertThrows(IllegalArgumentException.class, () -> factory.createSaleDetail(product, 0));
    }

    @Test
    void rejectNegativeQuantity() {
        Product product = new Product("P001", "Laptop", new BigDecimal("1200.00"), 10);
        assertThrows(IllegalArgumentException.class, () -> factory.createSaleDetail(product, -1));
    }

    @Test
    void rejectZeroPrice() {
        Product product = new Product("P001", "Laptop", BigDecimal.ZERO, 10);
        assertThrows(IllegalArgumentException.class, () -> factory.createSaleDetail(product, 1));
    }

    @Test
    void rejectNegativePrice() {
        assertThrows(IllegalArgumentException.class,
                () -> new Product("P001", "Laptop", new BigDecimal("-10.00"), 10));
    }

    @Test
    void createSaleWithOneValidDetail() {
        Product product = new Product("P001", "Laptop", new BigDecimal("1200.00"), 10);
        SaleDetail detail = factory.createSaleDetail(product, 2);

        Sale sale = factory.createSale(List.of(detail));

        assertNotNull(sale);
        assertEquals(1, sale.getDetails().size());
        assertEquals(0, new BigDecimal("2400.00").compareTo(sale.getSubtotal()));
        assertEquals(0, new BigDecimal("2784.00").compareTo(sale.getTotal()));
    }

    @Test
    void createSaleWithMultipleValidDetails() {
        Product laptop = new Product("P001", "Laptop", new BigDecimal("1200.00"), 10);
        Product mouse = new Product("P002", "Mouse", new BigDecimal("250.00"), 10);

        Sale sale = factory.createSale(List.of(
                factory.createSaleDetail(laptop, 2),
                factory.createSaleDetail(mouse, 3)
        ));

        assertEquals(2, sale.getDetails().size());
        assertEquals(0, new BigDecimal("3150.00").compareTo(sale.getSubtotal()));
        assertEquals(0, new BigDecimal("3654.00").compareTo(sale.getTotal()));
    }

    @Test
    void rejectNullDetailList() {
        assertThrows(IllegalArgumentException.class, () -> factory.createSale(null));
    }

    @Test
    void rejectEmptyDetailList() {
        assertThrows(IllegalArgumentException.class, () -> factory.createSale(new ArrayList<>()));
    }

    @Test
    void rejectNullDetail() {
        List<SaleDetail> details = new ArrayList<>();
        details.add(null);

        assertThrows(IllegalArgumentException.class, () -> factory.createSale(details));
    }

    @Test
    void verifyTotalMatchesSumOfSubtotals() {
        Product laptop = new Product("P001", "Laptop", new BigDecimal("1200.00"), 10);
        Product mouse = new Product("P002", "Mouse", new BigDecimal("250.00"), 10);

        SaleDetail laptopDetail = factory.createSaleDetail(laptop, 2);
        SaleDetail mouseDetail = factory.createSaleDetail(mouse, 3);
        Sale sale = factory.createSale(List.of(laptopDetail, mouseDetail));

        BigDecimal combinedSubtotal = laptopDetail.getSubtotal().add(mouseDetail.getSubtotal());
        assertEquals(0, combinedSubtotal.compareTo(sale.getSubtotal()));
    }
}
