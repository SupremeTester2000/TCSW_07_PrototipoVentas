package com.example.adapters.inbound.swing.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.table.AbstractTableModel;

public class CartTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 1L;
    private static final String[] COLUMNS = {"Producto", "Cantidad", "Precio unitario", "Subtotal"};
    private List<CartItemDTO> items = new ArrayList<>();

    public void setItems(List<CartItemDTO> items) {
        this.items = items == null ? new ArrayList<>() : new ArrayList<>(items);
        fireTableDataChanged();
    }

    public List<CartItemDTO> getItems() {
        return Collections.unmodifiableList(items);
    }

    public CartItemDTO getItemAt(int row) {
        return row < 0 || row >= items.size() ? null : items.get(row);
    }

    @Override
    public int getRowCount() {
        return items.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMNS.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMNS[column];
    }

    @Override
    public Class<?> getColumnClass(int column) {
        if (column == 1) {
            return Integer.class;
        }
        if (column == 2 || column == 3) {
            return BigDecimal.class;
        }
        return String.class;
    }

    @Override
    public Object getValueAt(int row, int column) {
        CartItemDTO item = items.get(row);
        switch (column) {
            case 0: return item.getProductName();
            case 1: return item.getQuantity();
            case 2: return item.getUnitPrice();
            case 3: return item.getSubtotal();
            default: throw new IndexOutOfBoundsException("Columna inválida: " + column);
        }
    }
}