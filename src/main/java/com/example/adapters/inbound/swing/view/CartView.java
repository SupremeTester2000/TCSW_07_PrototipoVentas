package com.example.adapters.inbound.swing.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableCellRenderer;

import com.example.adapters.inbound.swing.controller.CartController;
import com.example.adapters.inbound.swing.model.CartItemDTO;
import com.example.adapters.inbound.swing.model.CartTableModel;
import com.example.adapters.inbound.swing.model.ProductDTO;
import com.example.adapters.inbound.swing.model.SaleDTO;
import com.example.adapters.inbound.swing.model.SaleRequestDTO;

public class CartView extends ProductView {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(CartView.class.getName());
    private static final NumberFormat CURRENCY = NumberFormat.getCurrencyInstance(new Locale("es", "MX"));
    private static final String FONT_FAMILY = "Inter";

    private final transient CartController controller;
    private final CartTableModel tableModel;
    private final JTable table;
    private final JButton addButton = primaryButton("Agregar producto");
    private final JButton updateButton = secondaryButton("Modificar cantidad");
    private final JButton removeButton = destructiveButton("Eliminar");
    private final JButton confirmButton = primaryButton("Confirmar venta");
    private final JLabel subtotalValue = summaryValue();
    private final JLabel discountValue = summaryValue();
    private final JLabel taxValue = summaryValue();
    private final JLabel totalValue = summaryValue();
    private final JLabel statusLabel = new JLabel(" ");
    private final AtomicInteger previewVersion = new AtomicInteger();
    private boolean processing;

    public CartView(CartController controller) {
        this.controller = controller;
        this.tableModel = controller.getTableModel();
        setLayout(new BorderLayout(0, 16));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 228, 235), 1),
                BorderFactory.createEmptyBorder(22, 24, 20, 24)));

        JLabel title = new JLabel("Carrito de venta");
        title.setFont(new Font(FONT_FAMILY, Font.BOLD, 20));
        title.setForeground(TEXT_MAIN);
        JLabel subtitle = new JLabel("Revisa los productos y confirma la operación");
        subtitle.setFont(new Font(FONT_FAMILY, Font.PLAIN, 13));
        subtitle.setForeground(TEXT_SECONDARY);
        JPanel heading = new JPanel(new GridLayout(2, 1, 0, 5));
        heading.setOpaque(false);
        heading.add(title);
        heading.add(subtitle);

        table = new JTable(tableModel);
        table.setRowHeight(38);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setSelectionBackground(new Color(237, 229, 255));
        table.setSelectionForeground(TEXT_MAIN);
        table.setGridColor(new Color(232, 234, 240));
        table.setBackground(Color.WHITE);
        table.setForeground(TEXT_MAIN);
        table.setFont(normalFont());
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setShowVerticalLines(false);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setPreferredSize(new Dimension(0, 38));
        table.getTableHeader().setBackground(new Color(241, 243, 248));
        table.getTableHeader().setForeground(TEXT_MAIN);
        table.getTableHeader().setFont(new Font(FONT_FAMILY, Font.BOLD, 12));
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        for (int column = 0; column < table.getColumnCount(); column++) {
            table.getColumnModel().getColumn(column).setCellRenderer(renderer);
        }
        table.getColumnModel().getColumn(1).setPreferredWidth(80);
        table.getColumnModel().getColumn(2).setPreferredWidth(130);
        table.getColumnModel().getColumn(3).setPreferredWidth(130);

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createLineBorder(new Color(215, 218, 224), 1));
        tableScroll.getViewport().setBackground(Color.WHITE);

        JPanel actions = new JPanel(new BorderLayout(8, 0));
        actions.setOpaque(false);
        JPanel cartActions = new JPanel(new GridLayout(1, 3, 8, 0));
        cartActions.setOpaque(false);
        cartActions.add(addButton);
        cartActions.add(updateButton);
        cartActions.add(removeButton);
        actions.add(cartActions, BorderLayout.WEST);

        JPanel cartBody = new JPanel(new BorderLayout(0, 12));
        cartBody.setOpaque(false);
        cartBody.add(actions, BorderLayout.NORTH);
        cartBody.add(tableScroll, BorderLayout.CENTER);

        JPanel totals = createTotalsPanel();
        JPanel footer = new JPanel(new BorderLayout(12, 0));
        footer.setOpaque(false);
        footer.add(statusLabel, BorderLayout.CENTER);
        footer.add(confirmButton, BorderLayout.EAST);

        JPanel bottom = new JPanel(new BorderLayout(0, 12));
        bottom.setOpaque(false);
        bottom.add(totals, BorderLayout.NORTH);
        bottom.add(footer, BorderLayout.SOUTH);

        add(heading, BorderLayout.NORTH);
        add(cartBody, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        addButton.addActionListener(event -> showAddProductDialog());
        updateButton.addActionListener(event -> editSelectedQuantity());
        removeButton.addActionListener(event -> removeSelectedProduct());
        confirmButton.addActionListener(event -> confirmSale());
        table.getSelectionModel().addListSelectionListener(event -> updateActionState());
        tableModel.addTableModelListener(event -> {
            updateActionState();
            refreshSummary();
        });
        updateActionState();
        refreshSummary();
    }

    private JPanel createTotalsPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 6));
        panel.setBackground(SURFACE);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        addSummaryRow(panel, "Subtotal", subtotalValue);
        addSummaryRow(panel, "Descuento", discountValue);
        addSummaryRow(panel, "Impuestos", taxValue);
        JLabel totalLabel = new JLabel("Total");
        totalLabel.setFont(subtitleFont());
        totalLabel.setForeground(TEXT_MAIN);
        totalValue.setFont(subtitleFont());
        panel.add(totalLabel);
        panel.add(totalValue);
        return panel;
    }

    private void addSummaryRow(JPanel panel, String label, JLabel value) {
        JLabel text = new JLabel(label);
        text.setFont(normalFont());
        text.setForeground(TEXT_SECONDARY);
        panel.add(text);
        panel.add(value);
    }

    private void showAddProductDialog() {
        try {
            List<ProductDTO> products = controller.loadProducts();
            if (products.isEmpty()) {
                showMessage("No hay productos disponibles en el catálogo.", JOptionPane.WARNING_MESSAGE);
                return;
            }
            JComboBox<ProductDTO> productSelector = new JComboBox<>(products.toArray(new ProductDTO[0]));
            productSelector.setRenderer((list, product, index, selected, focus) -> {
                JLabel label = new JLabel(product == null ? "" : product.getName() + "  |  "
                        + CURRENCY.format(product.getPrice()) + "  |  Existencia: " + product.getStock());
                label.setOpaque(true);
                label.setBorder(BorderFactory.createEmptyBorder(5, 7, 5, 7));
                label.setBackground(selected ? list.getSelectionBackground() : Color.WHITE);
                label.setForeground(TEXT_MAIN);
                return label;
            });
            JTextField quantityField = new JTextField("1", 8);
            JPanel form = new JPanel(new GridLayout(2, 1, 0, 10));
            form.add(productSelector);
            form.add(quantityField);
            if (JOptionPane.showConfirmDialog(this, form, "Agregar al carrito",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) {
                return;
            }
            ProductDTO selected = (ProductDTO) productSelector.getSelectedItem();
            if (selected == null) {
                throw new IllegalArgumentException("Seleccione un producto.");
            }
            controller.addProduct(selected, parseQuantity(quantityField.getText()));
            statusLabel.setText("Producto agregado al carrito.");
        } catch (IllegalArgumentException exception) {
            showMessage(exception.getMessage(), JOptionPane.ERROR_MESSAGE);
        } catch (RuntimeException exception) {
            LOGGER.log(Level.WARNING, "No fue posible cargar los productos", exception);
            showMessage("No fue posible consultar el catálogo de productos.", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editSelectedQuantity() {
        int row = table.getSelectedRow();
        if (row < 0) {
            showMessage("Seleccione un producto del carrito.", JOptionPane.WARNING_MESSAGE);
            return;
        }
        CartItemDTO item = tableModel.getItemAt(table.convertRowIndexToModel(row));
        JTextField quantityField = new JTextField(Integer.toString(item.getQuantity()), 8);
        if (JOptionPane.showConfirmDialog(this, quantityField, "Modificar cantidad",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) {
            return;
        }
        try {
            controller.updateQuantity(table.convertRowIndexToModel(row), parseQuantity(quantityField.getText()));
            statusLabel.setText("Cantidad actualizada.");
        } catch (IllegalArgumentException exception) {
            showMessage(exception.getMessage(), JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removeSelectedProduct() {
        int row = table.getSelectedRow();
        if (row < 0) {
            showMessage("Seleccione un producto del carrito.", JOptionPane.WARNING_MESSAGE);
            return;
        }
        controller.removeProduct(table.convertRowIndexToModel(row));
        statusLabel.setText("Producto eliminado del carrito.");
    }

    private void refreshSummary() {
        int version = previewVersion.incrementAndGet();
        if (tableModel.getRowCount() == 0) {
            showTotals(null);
            return;
        }
        final SaleRequestDTO request;
        try {
            request = controller.buildRequest();
        } catch (IllegalArgumentException exception) {
            showTotals(null);
            return;
        }
        statusLabel.setText("Actualizando totales...");
        new SwingWorker<SaleDTO, Void>() {
            @Override
            protected SaleDTO doInBackground() {
                return controller.previewSale(request);
            }

            @Override
            protected void done() {
                if (version != previewVersion.get()) {
                    return;
                }
                try {
                    showTotals(get());
                    statusLabel.setText(" ");
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    showMessage("Se interrumpió el cálculo del total.", JOptionPane.ERROR_MESSAGE);
                } catch (ExecutionException exception) {
                    LOGGER.log(Level.WARNING, "No fue posible calcular el resumen", exception.getCause());
                    showMessage("No fue posible calcular el resumen de la venta.", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void showTotals(SaleDTO sale) {
        subtotalValue.setText(format(sale == null ? BigDecimal.ZERO : sale.getSubtotal()));
        discountValue.setText(format(sale == null ? BigDecimal.ZERO : sale.getDiscount()));
        taxValue.setText(format(sale == null ? BigDecimal.ZERO : sale.getTaxes()));
        totalValue.setText(format(sale == null ? BigDecimal.ZERO : sale.getTotal()));
    }

    private void confirmSale() {
        if (processing) {
            return;
        }
        final SaleRequestDTO request;
        try {
            request = controller.buildRequest();
        } catch (IllegalArgumentException exception) {
            showMessage("No se puede confirmar un carrito vacío.", JOptionPane.WARNING_MESSAGE);
            return;
        }
        setProcessing(true);
        statusLabel.setText("Procesando venta...");
        new SwingWorker<SaleDTO, Void>() {
            @Override
            protected SaleDTO doInBackground() {
                return controller.processSale(request);
            }

            @Override
            protected void done() {
                try {
                    SaleDTO sale = get();
                    controller.clearCart();
                    showTotals(null);
                    statusLabel.setForeground(SUCCESS);
                    statusLabel.setText("Venta procesada: " + format(sale.getTotal()));
                    JOptionPane.showMessageDialog(CartView.this,
                            "La venta se procesó correctamente.\nTotal: " + format(sale.getTotal()),
                            "Venta confirmada", JOptionPane.INFORMATION_MESSAGE);
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    statusLabel.setForeground(ERROR);
                    statusLabel.setText("Se interrumpió el procesamiento.");
                } catch (ExecutionException exception) {
                    LOGGER.log(Level.WARNING, "No fue posible procesar la venta", exception.getCause());
                    statusLabel.setForeground(ERROR);
                    statusLabel.setText("No fue posible procesar la venta.");
                    showMessage(exception.getCause() == null
                            ? "Revise las existencias e intente nuevamente."
                            : exception.getCause().getMessage(), JOptionPane.ERROR_MESSAGE);
                } finally {
                    setProcessing(false);
                }
            }
        }.execute();
    }

    private void setProcessing(boolean busy) {
        processing = busy;
        addButton.setEnabled(!busy);
        updateButton.setEnabled(!busy && table.getSelectedRow() >= 0);
        removeButton.setEnabled(!busy && table.getSelectedRow() >= 0);
        confirmButton.setEnabled(!busy && tableModel.getRowCount() > 0);
    }

    private void updateActionState() {
        boolean selected = table.getSelectedRow() >= 0;
        updateButton.setEnabled(!processing && selected);
        removeButton.setEnabled(!processing && selected);
        confirmButton.setEnabled(!processing && tableModel.getRowCount() > 0);
    }

    private static int parseQuantity(String text) {
        if (text == null || !text.trim().matches("\\d+")) {
            throw new IllegalArgumentException("La cantidad debe ser un número entero válido.");
        }
        try {
            int quantity = Integer.parseInt(text.trim());
            if (quantity <= 0) {
                throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");
            }
            return quantity;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("La cantidad debe ser un número entero válido.", exception);
        }
    }

    private void showMessage(String message, int type) {
        JOptionPane.showMessageDialog(this, message, "Carrito de venta", type);
    }

    private static String format(BigDecimal amount) {
        return CURRENCY.format(amount);
    }

    private static JLabel summaryValue() {
        JLabel value = new JLabel(format(BigDecimal.ZERO));
        value.setFont(normalFont());
        value.setForeground(TEXT_MAIN);
        return value;
    }

    private static JButton primaryButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(PRIMARY);
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setFont(new Font(FONT_FAMILY, Font.BOLD, 13));
        button.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        return button;
    }

    private static JButton secondaryButton(String text) {
        JButton button = primaryButton(text);
        button.setBackground(new Color(237, 229, 255));
        button.setForeground(PRIMARY);
        return button;
    }

    private static JButton destructiveButton(String text) {
        JButton button = primaryButton(text);
        button.setBackground(new Color(254, 226, 226));
        button.setForeground(ERROR);
        return button;
    }
}