package com.example.adapters.inbound.swing.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.table.AbstractTableModel;

public class ProductTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 1L;

    private static final String[] COLUMN_NAMES = {
            "Código",
            "Nombre",
            "Precio",
            "Existencia",
            "Acción"
    };

    private List<ProductDTO> products = new ArrayList<>();

    public ProductTableModel() {
        this(Collections.emptyList());
    }

    public ProductTableModel(List<ProductDTO> products) {
        setProducts(products);
    }

    public void setProducts(List<ProductDTO> products) {
        this.products = products == null ? new ArrayList<>() : new ArrayList<>(products);
        fireTableDataChanged();
    }

    public void addProduct(ProductDTO product) {
        if (product == null) {
            return;
        }
        int index = products.size();
        products.add(product);
        fireTableRowsInserted(index, index);
    }

    public ProductDTO getProductAt(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= products.size()) {
            return null;
        }
        return products.get(rowIndex);
    }

    @Override
    public int getRowCount() {
        return products.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMN_NAMES[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return String.class;
            case 1:
                return String.class;
            case 2:
                return BigDecimal.class;
            case 3:
                return Integer.class;
            case 4:
                return String.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        ProductDTO product = products.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return product.getCode();
            case 1:
                return product.getName();
            case 2:
                return product.getPrice();
            case 3:
                return product.getStock();
            case 4:
                return "Ver";
            default:
                return null;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 4;
    }
}
