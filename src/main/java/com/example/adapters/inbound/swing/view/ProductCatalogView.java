package com.example.adapters.inbound.swing.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.example.adapters.inbound.swing.model.ProductDTO;
import com.example.adapters.inbound.swing.model.ProductTableModel;

public class ProductCatalogView extends ProductView {

    private static final long serialVersionUID = 1L;

    private final ProductTableModel tableModel;
    private final JTable table;
    private final JTextField searchField;
    private final JButton newProductButton;
    private final JLabel summaryLabel;
    private final JScrollPane scrollPane;
    private final TableRowSorter<ProductTableModel> rowSorter;
    private Consumer<ProductDTO> onViewProduct = product -> { };

    public ProductCatalogView() {
        this(new ProductTableModel());
    }

    public ProductCatalogView(ProductTableModel tableModel) {
        super();
        this.tableModel = tableModel == null ? new ProductTableModel() : tableModel;
        setLayout(new BorderLayout(0, 20));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 228, 235), 1),
                BorderFactory.createEmptyBorder(24, 26, 24, 26)));

        JLabel titleLabel = new JLabel("Productos");
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
        titleLabel.setForeground(TEXT_MAIN);

        JLabel subtitleLabel = new JLabel("Administra el catálogo de productos");
        subtitleLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
        subtitleLabel.setForeground(TEXT_SECONDARY);

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new javax.swing.BoxLayout(headerPanel, javax.swing.BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);
        headerPanel.add(titleLabel);
        headerPanel.add(javax.swing.Box.createVerticalStrut(5));
        headerPanel.add(subtitleLabel);

        searchField = new JTextField();
        searchField.setToolTipText("Buscar por código o nombre");
        searchField.setPreferredSize(new Dimension(100, 38));
        searchField.setFont(normalFont());
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(215, 218, 224), 1),
                BorderFactory.createEmptyBorder(7, 11, 7, 11)));

        newProductButton = new JButton("+ Nuevo producto");
        newProductButton.setOpaque(true);
        newProductButton.setContentAreaFilled(true);
        newProductButton.setBorderPainted(false);
        newProductButton.setFocusPainted(false);
        newProductButton.setBackground(PRIMARY);
        newProductButton.setForeground(Color.WHITE);
        newProductButton.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        newProductButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        newProductButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        JPanel actionsPanel = new JPanel(new BorderLayout(14, 0));
        actionsPanel.setOpaque(false);
        actionsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        actionsPanel.add(searchField, BorderLayout.CENTER);
        actionsPanel.add(newProductButton, BorderLayout.EAST);

        table = new JTable(this.tableModel);
        table.setFillsViewportHeight(false);
        table.setRowHeight(36);
        table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        table.setSelectionBackground(new Color(237, 229, 255));
        table.setSelectionForeground(TEXT_MAIN);
        table.setGridColor(new Color(232, 234, 240));
        table.setBackground(Color.WHITE);
        table.setForeground(TEXT_MAIN);
        table.setFont(normalFont());
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setRowMargin(0);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setPreferredSize(new Dimension(0, 38));
        table.getTableHeader().setBackground(new Color(241, 243, 248));
        table.getTableHeader().setForeground(TEXT_MAIN);
        table.getTableHeader().setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        javax.swing.table.DefaultTableCellRenderer cellRenderer =
                new javax.swing.table.DefaultTableCellRenderer();
        cellRenderer.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        table.getColumnModel().getColumn(0).setCellRenderer(cellRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(cellRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(new CurrencyCellRenderer());
        javax.swing.table.DefaultTableCellRenderer stockRenderer =
                new javax.swing.table.DefaultTableCellRenderer();
        stockRenderer.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        stockRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(3).setCellRenderer(stockRenderer);

        rowSorter = new TableRowSorter<>(this.tableModel);
        table.setRowSorter(rowSorter);
        table.getColumnModel().getColumn(0).setPreferredWidth(90);
        table.getColumnModel().getColumn(1).setPreferredWidth(220);
        table.getColumnModel().getColumn(2).setPreferredWidth(110);
        table.getColumnModel().getColumn(3).setPreferredWidth(110);
        table.getColumnModel().getColumn(4).setPreferredWidth(75);
        table.getColumnModel().getColumn(4).setCellRenderer(new ViewButtonRenderer());
        table.getColumnModel().getColumn(4).setCellEditor(new ViewButtonEditor());
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent event) {
                if (event.getClickCount() == 2 && table.getSelectedRow() >= 0) {
                    showProductAt(table.convertRowIndexToModel(table.getSelectedRow()));
                }
            }
        });

        scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(215, 218, 224), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);

        summaryLabel = new JLabel();
        summaryLabel.setForeground(TEXT_SECONDARY);
        summaryLabel.setFont(normalFont());

        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setOpaque(false);
        footerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 18));
        footerPanel.add(summaryLabel, BorderLayout.EAST);

        JPanel content = new JPanel();
        content.setLayout(new javax.swing.BoxLayout(content, javax.swing.BoxLayout.Y_AXIS));
        content.setOpaque(false);
        actionsPanel.setAlignmentX(LEFT_ALIGNMENT);
        scrollPane.setAlignmentX(LEFT_ALIGNMENT);
        footerPanel.setAlignmentX(LEFT_ALIGNMENT);
        content.add(actionsPanel);
        content.add(javax.swing.Box.createVerticalStrut(14));
        content.add(scrollPane);
        content.add(javax.swing.Box.createVerticalStrut(9));
        content.add(footerPanel);
        content.add(javax.swing.Box.createVerticalGlue());

        add(headerPanel, BorderLayout.NORTH);
        add(content, BorderLayout.CENTER);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent event) {
                applySearchFilter();
            }

            @Override
            public void removeUpdate(DocumentEvent event) {
                applySearchFilter();
            }

            @Override
            public void changedUpdate(DocumentEvent event) {
                applySearchFilter();
            }
        });
        tableModel.addTableModelListener(event -> {
            updateSummary();
            updateTableViewportSize();
        });
        rowSorter.addRowSorterListener(event -> {
            updateSummary();
            updateTableViewportSize();
        });
        updateSummary();
        updateTableViewportSize();
    }

    private void applySearchFilter() {
        String query = searchField.getText().trim();
        rowSorter.setRowFilter(query.isEmpty()
                ? null
                : RowFilter.regexFilter("(?i)" + Pattern.quote(query), 0, 1));
        updateSummary();
    }

    private void updateSummary() {
        int visibleRows = table == null ? tableModel.getRowCount() : table.getRowCount();
        summaryLabel.setText(visibleRows + (visibleRows == 1 ? " producto" : " productos"));
    }

    private void updateTableViewportSize() {
        int visibleRows = table == null ? tableModel.getRowCount() : table.getRowCount();
        int displayedRows = Math.min(Math.max(visibleRows, 1), 5);
        int headerHeight = table == null ? 38 : table.getTableHeader().getPreferredSize().height;
        scrollPane.setPreferredSize(new Dimension(
                700,
                headerHeight + displayedRows * table.getRowHeight() + 2));
        scrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE,
                headerHeight + displayedRows * table.getRowHeight() + 2));
        revalidate();
    }

    private void showProductAt(int modelRow) {
        ProductDTO product = tableModel.getProductAt(modelRow);
        if (product != null) {
            onViewProduct.accept(product);
        }
    }

    public void setProducts(List<ProductDTO> products) {
        tableModel.setProducts(products);
        applySearchFilter();
    }

    public void setOnNewProduct(Runnable action) {
        if (action != null) {
            newProductButton.addActionListener(event -> action.run());
        }
    }

    public void setOnViewProduct(Consumer<ProductDTO> action) {
        onViewProduct = action == null ? product -> { } : action;
    }

    public JTable getTable() {
        return table;
    }

    public String getSearchText() {
        return searchField.getText();
    }

    public ProductTableModel getTableModel() {
        return tableModel;
    }

    private final class ViewButtonRenderer extends JButton implements TableCellRenderer {
        private static final long serialVersionUID = 1L;

        private ViewButtonRenderer() {
            setText("Ver");
            setOpaque(true);
            setContentAreaFilled(true);
            setBorderPainted(false);
            setFocusPainted(false);
            setBackground(new Color(237, 229, 255));
            setForeground(PRIMARY);
            setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable source, Object value, boolean selected, boolean focused, int row, int column) {
            return this;
        }
    }

    private static final class CurrencyCellRenderer
            extends javax.swing.table.DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);

        private CurrencyCellRenderer() {
            setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        }

        @Override
        protected void setValue(Object value) {
            setText(value instanceof BigDecimal
                    ? currencyFormat.format(value)
                    : value == null ? "" : value.toString());
        }
    }

    private final class ViewButtonEditor extends AbstractCellEditor implements TableCellEditor {
        private static final long serialVersionUID = 1L;
        private final JButton button = new JButton("Ver");
        private int modelRow;

        private ViewButtonEditor() {
            button.setOpaque(true);
            button.setContentAreaFilled(true);
            button.setBorderPainted(false);
            button.setFocusPainted(false);
            button.setBackground(new Color(237, 229, 255));
            button.setForeground(PRIMARY);
            button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
            button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
            button.addActionListener(event -> {
                showProductAt(modelRow);
                fireEditingStopped();
            });
        }

        @Override
        public Component getTableCellEditorComponent(
                JTable source, Object value, boolean selected, int row, int column) {
            modelRow = source.convertRowIndexToModel(row);
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return "Ver";
        }
    }
}
