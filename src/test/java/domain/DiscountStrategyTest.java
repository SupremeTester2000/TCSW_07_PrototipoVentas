package domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.function.Function;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import com.example.config.ConfiguracionVentasSingleton;
import com.example.domain.model.Product;
import com.example.domain.model.Sale;
import com.example.domain.strategy.DiscountPercent;
import com.example.domain.strategy.FixedDiscount;
import com.example.domain.strategy.FunctionalDiscountPolicies;
import com.example.domain.strategy.VatTaxStrategy;

class DiscountStrategyTest {

    private final ConfiguracionVentasSingleton configuration = ConfiguracionVentasSingleton.getInstance();

    @AfterEach
    void restoreConfiguration() {
        configuration.setIvaRate(new BigDecimal("0.16"));
        configuration.setMaximumDiscountAmount(new BigDecimal("100000.00"));
        configuration.setMaximumSaleDetails(100);
    }

    @Test
    void classicAndFunctionalPoliciesCalculateTheSamePercentageDiscount() {
        Sale classicSale = createSale(new DiscountPercent(new BigDecimal("10")));
        Function<Sale, BigDecimal> functionalPolicy = FunctionalDiscountPolicies.percentage(new BigDecimal("10"));
        Sale functionalSale = createSale(functionalPolicy);

        assertEquals(new BigDecimal("100.00"), classicSale.getDiscountAmount());
        assertEquals(new BigDecimal("144.00"), classicSale.getTaxAmount());
        assertEquals(new BigDecimal("1044.00"), classicSale.getTotal());
        assertEquals(0, classicSale.getTotal().compareTo(functionalSale.getTotal()));
    }

    @Test
    void fixedDiscountIsCappedAndUsesConfiguredMaximum() {
        configuration.setMaximumDiscountAmount(new BigDecimal("75.00"));
        Sale sale = createSale(new FixedDiscount(new BigDecimal("150.00")));
        Sale functionalSale = createSale(FunctionalDiscountPolicies.fixed(new BigDecimal("150.00")));

        assertEquals(new BigDecimal("75.00"), sale.getDiscountAmount());
        assertEquals(new BigDecimal("1073.00"), sale.getTotal());
        assertEquals(0, sale.getTotal().compareTo(functionalSale.getTotal()));
    }

    @Test
    void singletonConfigurationControlsTaxAndMaximumDetails() {
        configuration.setIvaRate(new BigDecimal("0.10"));
        configuration.setMaximumSaleDetails(1);
        Sale sale = new Sale();
        Product product = new Product("P001", "Producto", new BigDecimal("100.00"), 5);
        sale.agregarDetalle(product, 1);

        assertEquals(new BigDecimal("110.00"), sale.getTotal());
        assertThrows(IllegalArgumentException.class, () -> sale.agregarDetalle(product, 1));
        assertSame(configuration, ConfiguracionVentasSingleton.getInstance());
    }

    private Sale createSale(Function<Sale, BigDecimal> discountPolicy) {
        Sale sale = new Sale(discountPolicy, new VatTaxStrategy());
        sale.agregarDetalle(new Product("P001", "Producto", new BigDecimal("1000.00"), 5), 1);
        return sale;
    }
}