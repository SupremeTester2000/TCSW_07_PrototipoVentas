package com.example.adapters.inbound.swing.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.example.domain.model.Sale;
import com.example.domain.model.SaleDetail;

public final class SaleDTO {

    private final List<Line> lines;
    private final BigDecimal subtotal;
    private final BigDecimal discount;
    private final BigDecimal taxes;
    private final BigDecimal total;

    private SaleDTO(List<Line> lines, BigDecimal subtotal, BigDecimal discount,
            BigDecimal taxes, BigDecimal total) {
        this.lines = Collections.unmodifiableList(new ArrayList<>(lines));
        this.subtotal = subtotal;
        this.discount = discount;
        this.taxes = taxes;
        this.total = total;
    }

    public static SaleDTO fromSale(Sale sale) {
        if (sale == null) {
            throw new IllegalArgumentException("La venta no puede ser nula.");
        }
        List<Line> lines = sale.getDetails().stream()
            .map(SaleDTO::toLine)
            .collect(Collectors.toList());
        return new SaleDTO(lines, sale.getSubtotal(), sale.getDiscountAmount(),
                sale.getTaxAmount(), sale.getTotal());
    }

        private static Line toLine(SaleDetail detail) {
        return new Line(detail.getProduct().getCode(), detail.getProduct().getName(),
            detail.getQuantity(), detail.getCapturedPrice(), detail.getSubtotal());
        }

    public List<Line> getLines() {
        return lines;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public BigDecimal getTaxes() {
        return taxes;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public static final class Line {
        private final String productCode;
        private final String productName;
        private final int quantity;
        private final BigDecimal unitPrice;
        private final BigDecimal subtotal;

        private Line(String productCode, String productName, int quantity,
                BigDecimal unitPrice, BigDecimal subtotal) {
            this.productCode = productCode;
            this.productName = productName;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.subtotal = subtotal;
        }

        public String getProductCode() {
            return productCode;
        }

        public String getProductName() {
            return productName;
        }

        public int getQuantity() {
            return quantity;
        }

        public BigDecimal getUnitPrice() {
            return unitPrice;
        }

        public BigDecimal getSubtotal() {
            return subtotal;
        }
    }
}