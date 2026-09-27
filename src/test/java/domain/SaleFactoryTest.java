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
    void crearDetalleValido() {
        Product product = new Product("P001", "Laptop", new BigDecimal("1200.00"), 10);

        SaleDetail detail = factory.createSaleDetail(product, 2);

        assertNotNull(detail);
        assertEquals(product, detail.getProducto());
        assertEquals(2, detail.getCantidad());
        assertEquals(0, new BigDecimal("2400.00").compareTo(detail.getSubtotal()));
    }

    @Test
    void rechazaProductoNulo() {
        assertThrows(IllegalArgumentException.class, () -> factory.createSaleDetail(null, 1));
    }

    @Test
    void rechazaCantidadCero() {
        Product product = new Product("P001", "Laptop", new BigDecimal("1200.00"), 10);
        assertThrows(IllegalArgumentException.class, () -> factory.createSaleDetail(product, 0));
    }

    @Test
    void rechazaCantidadNegativa() {
        Product product = new Product("P001", "Laptop", new BigDecimal("1200.00"), 10);
        assertThrows(IllegalArgumentException.class, () -> factory.createSaleDetail(product, -1));
    }

    @Test
    void rechazaPrecioCero() {
        Product product = new Product("P001", "Laptop", BigDecimal.ZERO, 10);
        assertThrows(IllegalArgumentException.class, () -> factory.createSaleDetail(product, 1));
    }

    @Test
    void rechazaPrecioNegativo() {
        assertThrows(IllegalArgumentException.class,
                () -> new Product("P001", "Laptop", new BigDecimal("-10.00"), 10));
    }

    @Test
    void crearVentaConUnDetalleValido() {
        Product product = new Product("P001", "Laptop", new BigDecimal("1200.00"), 10);
        SaleDetail detail = factory.createSaleDetail(product, 2);

        Sale sale = factory.createSale(List.of(detail));

        assertNotNull(sale);
        assertEquals(1, sale.getDetalles().size());
        assertEquals(0, new BigDecimal("2400.00").compareTo(sale.getSubtotal()));
        assertEquals(0, new BigDecimal("2784.00").compareTo(sale.getTotal()));
    }

    @Test
    void crearVentaConVariosDetallesValidos() {
        Product laptop = new Product("P001", "Laptop", new BigDecimal("1200.00"), 10);
        Product mouse = new Product("P002", "Mouse", new BigDecimal("250.00"), 10);

        Sale sale = factory.createSale(List.of(
                factory.createSaleDetail(laptop, 2),
                factory.createSaleDetail(mouse, 3)
        ));

        assertEquals(2, sale.getDetalles().size());
        assertEquals(0, new BigDecimal("3150.00").compareTo(sale.getSubtotal()));
        assertEquals(0, new BigDecimal("3654.00").compareTo(sale.getTotal()));
    }

    @Test
    void rechazaListaDeDetallesNula() {
        assertThrows(IllegalArgumentException.class, () -> factory.createSale(null));
    }

    @Test
    void rechazaListaDeDetallesVacia() {
        assertThrows(IllegalArgumentException.class, () -> factory.createSale(new ArrayList<>()));
    }

    @Test
    void rechazaDetalleNulo() {
        List<SaleDetail> details = new ArrayList<>();
        details.add(null);

        assertThrows(IllegalArgumentException.class, () -> factory.createSale(details));
    }

    @Test
    void totalCoincideConSumaDeSubtotales() {
        Product laptop = new Product("P001", "Laptop", new BigDecimal("1200.00"), 10);
        Product mouse = new Product("P002", "Mouse", new BigDecimal("250.00"), 10);

        SaleDetail laptopDetail = factory.createSaleDetail(laptop, 2);
        SaleDetail mouseDetail = factory.createSaleDetail(mouse, 3);
        Sale sale = factory.createSale(List.of(laptopDetail, mouseDetail));

        BigDecimal subtotalTotal = laptopDetail.getSubtotal().add(mouseDetail.getSubtotal());
        assertEquals(0, subtotalTotal.compareTo(sale.getSubtotal()));
    }
}
