package com.example.adapters.inbound.swing.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.example.adapters.inbound.swing.model.ProductDTO;

public class ProductDetailView extends ProductView {

    private static final long serialVersionUID = 1L;

    private final JLabel nameLabel = new JLabel("-");
    private final JLabel codeLabel = new JLabel("-");
    private final JLabel priceLabel = new JLabel("-");
    private final JLabel stockLabel = new JLabel("-");
    private final JButton editButton = new JButton("Editar producto");
    private final JButton backButton = new JButton("Volver al catálogo");
    private ProductDTO displayedProduct;
    private Consumer<ProductDTO> onEdit = product -> { };

    public ProductDetailView() {
        super();
        setLayout(new BorderLayout(0, 18));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 228, 235), 1),
                BorderFactory.createEmptyBorder(24, 26, 24, 26)));

        JLabel titleLabel = new JLabel("Detalle del producto");
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
        titleLabel.setForeground(TEXT_MAIN);

        JLabel subtitleLabel = new JLabel("Información completa del producto");
        subtitleLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
        subtitleLabel.setForeground(TEXT_SECONDARY);

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new javax.swing.BoxLayout(headerPanel, javax.swing.BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);
        headerPanel.add(titleLabel);
        headerPanel.add(javax.swing.Box.createVerticalStrut(5));
        headerPanel.add(subtitleLabel);

        JPanel productCard = new JPanel(new BorderLayout(0, 22));
        productCard.setBackground(Color.WHITE);
        productCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 228, 235), 1),
                BorderFactory.createEmptyBorder(20, 20, 18, 20)));
        productCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 320));

        JPanel productIdentity = new JPanel(new BorderLayout(0, 7));
        productIdentity.setOpaque(false);
        nameLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 23));
        nameLabel.setForeground(TEXT_MAIN);

        JLabel codeCaption = new JLabel("Código");
        codeCaption.setFont(normalFont());
        codeCaption.setForeground(TEXT_SECONDARY);

        JPanel codePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        codePanel.setOpaque(false);
        codePanel.add(codeCaption);
        codePanel.add(codeLabel);
        codeLabel.setFont(normalFont());
        codeLabel.setForeground(TEXT_MAIN);

        productIdentity.add(nameLabel, BorderLayout.NORTH);
        productIdentity.add(codePanel, BorderLayout.CENTER);

        JPanel metrics = new JPanel(new GridBagLayout());
        metrics.setOpaque(false);
        JPanel priceCard = createMetricCard("Precio", priceLabel);
        JPanel stockCard = createMetricCard("Existencia", stockLabel);

        GridBagConstraints metricConstraints = new GridBagConstraints();
        metricConstraints.gridx = 0;
        metricConstraints.gridy = 0;
        metricConstraints.weightx = 0.5;
        metricConstraints.fill = GridBagConstraints.HORIZONTAL;
        metricConstraints.insets = new Insets(0, 0, 0, 14);
        metrics.add(priceCard, metricConstraints);
        metricConstraints.gridx = 1;
        metricConstraints.weightx = 0.5;
        metricConstraints.insets = new Insets(0, 0, 0, 0);
        metrics.add(stockCard, metricConstraints);

        productCard.add(productIdentity, BorderLayout.NORTH);
        productCard.add(metrics, BorderLayout.CENTER);

        stylePrimaryButton(backButton);
        editButton.setOpaque(true);
        editButton.setContentAreaFilled(true);
        editButton.setBorderPainted(true);
        editButton.setFocusPainted(false);
        editButton.setBackground(Color.WHITE);
        editButton.setForeground(PRIMARY);
        editButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        editButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PRIMARY, 1),
                BorderFactory.createEmptyBorder(10, 17, 10, 17)));
        editButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        actionsPanel.setOpaque(false);
        actionsPanel.add(editButton);
        actionsPanel.add(backButton);
        productCard.add(actionsPanel, BorderLayout.SOUTH);

        JPanel content = new JPanel(new GridBagLayout());
        content.setOpaque(false);
        GridBagConstraints contentConstraints = new GridBagConstraints();
        contentConstraints.gridx = 0;
        contentConstraints.gridy = 0;
        contentConstraints.weightx = 1.0;
        contentConstraints.fill = GridBagConstraints.HORIZONTAL;
        contentConstraints.anchor = GridBagConstraints.NORTH;
        content.add(productCard, contentConstraints);
        contentConstraints.gridy = 1;
        contentConstraints.weighty = 1.0;
        contentConstraints.fill = GridBagConstraints.BOTH;
        JPanel verticalFiller = new JPanel();
        verticalFiller.setOpaque(false);
        content.add(verticalFiller, contentConstraints);

        add(headerPanel, BorderLayout.NORTH);
        add(content, BorderLayout.CENTER);

        editButton.addActionListener(event -> {
            if (displayedProduct != null) {
                onEdit.accept(displayedProduct);
            }
        });
    }

    private void stylePrimaryButton(JButton button) {
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setBackground(PRIMARY);
        button.setForeground(Color.WHITE);
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        button.setBorder(BorderFactory.createEmptyBorder(11, 18, 11, 18));
        button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    }

    private JPanel createMetricCard(String title, JLabel valueLabel) {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(new Color(248, 249, 252));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(232, 234, 240), 1),
                BorderFactory.createEmptyBorder(16, 18, 16, 18)));
        card.setPreferredSize(new Dimension(220, 94));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(normalFont());
        titleLabel.setForeground(TEXT_SECONDARY);

        valueLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        valueLabel.setForeground(TEXT_MAIN);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    public void display(ProductDTO product) {
        if (product == null) {
            clear();
            return;
        }
        displayedProduct = product;
        nameLabel.setText(product.getName());
        codeLabel.setText(product.getCode());
        priceLabel.setText(formatCurrency(product.getPrice()));
        stockLabel.setText(String.valueOf(product.getStock()));
    }

    private String formatCurrency(BigDecimal amount) {
        if (amount == null) {
            return "$0.00";
        }
        return "$" + amount.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    public void clear() {
        displayedProduct = null;
        nameLabel.setText("-");
        codeLabel.setText("-");
        priceLabel.setText("-");
        stockLabel.setText("-");
    }

    public void setOnBack(Runnable action) {
        backButton.addActionListener(event -> {
            if (action != null) {
                action.run();
            }
        });
    }

    public void setOnEdit(Consumer<ProductDTO> action) {
        onEdit = action == null ? product -> { } : action;
    }
}
