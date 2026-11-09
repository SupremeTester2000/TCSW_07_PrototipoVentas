package com.example.adapters.inbound.rest.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class SaleResponse {

    private final List<Line> lines;
    private final BigDecimal subtotal;
    private final BigDecimal discount;
    private final BigDecimal taxes;
    private final BigDecimal total;

    public SaleResponse(List<Line> lines, BigDecimal subtotal, BigDecimal discount,
            BigDecimal taxes, BigDecimal total) {
        this.lines = Collections.unmodifiableList(new ArrayList<>(lines));
        this.subtotal = subtotal;
        this.discount = discount;
        this.taxes = taxes;
        this.total = total;
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

        public Line(String productCode, String productName, int quantity,
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
