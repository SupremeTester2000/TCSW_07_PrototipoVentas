package com.example.adapters.inbound.swing.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.example.adapters.inbound.swing.model.ProductDTO;

public class ProductFormView extends ProductView {

    private static final long serialVersionUID = 1L;
    private static final Pattern PRODUCT_CODE_PATTERN = Pattern.compile("^P\\d{5}$");

    public enum FormMode {
        CREATE,
        EDIT
    }

    private final JTextField codeField = new JTextField();
    private final JTextField nameField = new JTextField();
    private final JTextField priceField = new JTextField();
    private final JTextField stockField = new JTextField();

    private final JLabel codeError = createErrorLabel();
    private final JLabel nameError = createErrorLabel();
    private final JLabel priceError = createErrorLabel();
    private final JLabel stockError = createErrorLabel();

    private final JButton cancelButton = new JButton("Cancelar");
    private final JButton saveButton = new JButton("Guardar");
    private final JPanel alertPanel = createAlertPanel();

    private transient Runnable onAlertAccept = () -> { };
    private boolean successAlert;
    private boolean codeTouched;
    private boolean nameTouched;
    private boolean priceTouched;
    private boolean stockTouched;
    private FormMode formMode = FormMode.CREATE;

    public ProductFormView() {
        super();
        setLayout(new BorderLayout(0, 20));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 228, 235), 1),
                BorderFactory.createEmptyBorder(24, 26, 24, 26)));

        JLabel titleLabel = new JLabel("Producto");
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
        titleLabel.setForeground(TEXT_MAIN);

        JLabel subtitleLabel = new JLabel("Completa la información del producto");
        subtitleLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
        subtitleLabel.setForeground(TEXT_SECONDARY);

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new javax.swing.BoxLayout(headerPanel, javax.swing.BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);
        headerPanel.add(titleLabel);
        headerPanel.add(javax.swing.Box.createVerticalStrut(5));
        headerPanel.add(subtitleLabel);

        alertPanel.setVisible(false);
        alertPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setOpaque(false);
        addField(fieldsPanel, 0, 0, "Código", codeField, codeError);
        addField(fieldsPanel, 1, 0, "Precio", priceField, priceError);
        addField(fieldsPanel, 0, 1, "Nombre", nameField, nameError);
        addField(fieldsPanel, 1, 1, "Existencia", stockField, stockError);

        cancelButton.setOpaque(true);
        cancelButton.setContentAreaFilled(true);
        cancelButton.setBorderPainted(true);
        cancelButton.setFocusPainted(false);
        cancelButton.setBackground(new Color(243, 244, 246));
        cancelButton.setForeground(TEXT_MAIN);
        cancelButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 214, 222), 1),
                BorderFactory.createEmptyBorder(10, 24, 10, 24)));

        saveButton.setOpaque(true);
        saveButton.setContentAreaFilled(true);
        saveButton.setBorderPainted(false);
        saveButton.setFocusPainted(false);
        saveButton.setBackground(PRIMARY);
        saveButton.setForeground(Color.WHITE);
        saveButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        saveButton.setBorder(BorderFactory.createEmptyBorder(10, 24, 10, 24));
        saveButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        JPanel actionsPanel = new JPanel(new BorderLayout());
        actionsPanel.setOpaque(false);
        actionsPanel.setBorder(BorderFactory.createEmptyBorder(28, 4, 2, 4));
        actionsPanel.add(cancelButton, BorderLayout.WEST);
        actionsPanel.add(saveButton, BorderLayout.EAST);

        JPanel formCard = new JPanel(new BorderLayout(0, 10));
        formCard.setOpaque(true);
        formCard.setBackground(Color.WHITE);
        formCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 228, 235), 1),
                BorderFactory.createEmptyBorder(20, 20, 18, 20)));
        formCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 340));
        formCard.add(fieldsPanel, BorderLayout.NORTH);
        formCard.add(actionsPanel, BorderLayout.SOUTH);

        JPanel content = new JPanel();
        content.setLayout(new javax.swing.BoxLayout(content, javax.swing.BoxLayout.Y_AXIS));
        content.setOpaque(false);
        alertPanel.setAlignmentX(LEFT_ALIGNMENT);
        formCard.setAlignmentX(LEFT_ALIGNMENT);
        content.add(alertPanel);
        content.add(javax.swing.Box.createVerticalStrut(12));
        content.add(formCard);
        content.add(javax.swing.Box.createVerticalGlue());

        add(headerPanel, BorderLayout.NORTH);
        add(content, BorderLayout.CENTER);

        configureField(codeField);
        configureField(nameField);
        configureField(priceField);
        configureField(stockField);
        codeField.setEditable(false);
        codeField.setBackground(new Color(243, 244, 246));

        bindValidation(codeField, codeError);
        bindValidation(nameField, nameError);
        bindValidation(priceField, priceError);
        bindValidation(stockField, stockError);
    }

    private void addField(
            JPanel parent, int column, int row, String labelText, JTextField field, JLabel errorLabel) {
        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(new javax.swing.BoxLayout(fieldPanel, javax.swing.BoxLayout.Y_AXIS));
        fieldPanel.setOpaque(false);

        JLabel label = new JLabel(labelText + " *");
        label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        label.setForeground(TEXT_MAIN);
        label.setLabelFor(field);

        field.setFont(normalFont());
        field.setPreferredSize(new Dimension(280, 38));
        field.setMinimumSize(new Dimension(120, 38));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        field.setBackground(Color.WHITE);
        field.setForeground(TEXT_MAIN);
        field.setCaretColor(TEXT_MAIN);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(211, 214, 221), 1),
                BorderFactory.createEmptyBorder(7, 10, 7, 10)));

        fieldPanel.add(label);
        fieldPanel.add(javax.swing.Box.createVerticalStrut(6));
        fieldPanel.add(field);
        fieldPanel.add(javax.swing.Box.createVerticalStrut(3));
        fieldPanel.add(errorLabel);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = column;
        constraints.gridy = row;
        constraints.weightx = 1.0;
        constraints.weighty = 0.0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.insets = new Insets(0, column == 0 ? 0 : 12, row == 0 ? 14 : 0,
                column == 0 ? 12 : 0);
        fieldPanel.setMinimumSize(new Dimension(280, fieldPanel.getMinimumSize().height));
        parent.add(fieldPanel, constraints);
    }

    private JLabel createErrorLabel() {
        JLabel label = new JLabel(" ");
        label.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        label.setForeground(ERROR);
        label.setPreferredSize(new Dimension(280, 16));
        label.setMinimumSize(new Dimension(0, 16));
        label.setMaximumSize(new Dimension(Integer.MAX_VALUE, 16));
        label.setAlignmentX(CENTER_ALIGNMENT);
        return label;
    }

    private void configureField(JTextField field) {
        field.setMargin(new java.awt.Insets(4, 8, 4, 8));
    }

    private void bindValidation(JTextField field, JLabel errorLabel) {
        field.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent event) {
                validateField(field, errorLabel);
            }

            @Override
            public void removeUpdate(DocumentEvent event) {
                validateField(field, errorLabel);
            }

            @Override
            public void changedUpdate(DocumentEvent event) {
                validateField(field, errorLabel);
            }
        });
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent event) {
                markTouched(field);
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent event) {
                markTouched(field);
                validateField(field, errorLabel);
            }
        });
    }

    private void validateField(JTextField field, JLabel errorLabel) {
        if (field == codeField && formMode == FormMode.EDIT) {
            clearFieldError(field, errorLabel);
            return;
        }
        String message;
        if (field == codeField) {
            message = validateCodeField();
        } else if (field == nameField) {
            message = validateNameField();
        } else if (field == priceField) {
            message = validatePriceField();
        } else if (field == stockField) {
            message = validateStockField();
        } else {
            return;
        }

        if (message == null) {
            clearFieldError(field, errorLabel);
        } else {
            showFieldError(field, errorLabel, message);
        }
    }

    private String validateCodeField() {
        if (formMode == FormMode.EDIT) {
            return null;
        }
        String value = codeField.getText().trim();
        if (value.isEmpty()) {
            return codeTouched ? "El código es obligatorio." : null;
        }
        return PRODUCT_CODE_PATTERN.matcher(value).matches()
                ? null
                : "El código debe tener el formato P00000.";
    }

    private String validateNameField() {
        if (nameField.getText().trim().isEmpty() && nameTouched) {
            return "El nombre es obligatorio.";
        }
        return null;
    }

    private String validatePriceField() {
        String value = priceField.getText().trim();
        if (value.isEmpty()) {
            return priceTouched ? "El precio debe ser un número válido." : null;
        }
        try {
            new BigDecimal(value);
            return null;
        } catch (NumberFormatException exception) {
            return "El precio debe ser un número válido.";
        }
    }

    private String validateStockField() {
        String value = stockField.getText().trim();
        if (value.isEmpty()) {
            return stockTouched ? "La existencia debe ser un número entero válido." : null;
        }
        try {
            Integer.parseInt(value);
            return null;
        } catch (NumberFormatException exception) {
            return "La existencia debe ser un número entero válido.";
        }
    }

    private void markTouched(JTextField field) {
        if (field == codeField) {
            codeTouched = true;
        } else if (field == nameField) {
            nameTouched = true;
        } else if (field == priceField) {
            priceTouched = true;
        } else if (field == stockField) {
            stockTouched = true;
        }
    }

    public boolean validateAllFields() {
        codeTouched = true;
        nameTouched = true;
        priceTouched = true;
        stockTouched = true;

        boolean codeValid = formMode == FormMode.EDIT || validateAndDisplay(codeField, codeError);
        boolean nameValid = validateAndDisplay(nameField, nameError);
        boolean priceValid = validateAndDisplay(priceField, priceError);
        boolean stockValid = validateAndDisplay(stockField, stockError);

        if (!codeValid) {
            codeField.requestFocusInWindow();
        } else if (!nameValid) {
            nameField.requestFocusInWindow();
        } else if (!priceValid) {
            priceField.requestFocusInWindow();
        } else if (!stockValid) {
            stockField.requestFocusInWindow();
        }
        return codeValid && nameValid && priceValid && stockValid;
    }

    public void prepareForCreate(String generatedCode) {
        formMode = FormMode.CREATE;
        resetTouchedState();
        clearErrors();
        codeField.setText(generatedCode);
        codeField.setEditable(false);
        codeField.setBackground(new Color(243, 244, 246));
        nameField.setText("");
        priceField.setText("");
        stockField.setText("");
        saveButton.setText("Guardar");
    }

    public void prepareForEdit(ProductDTO product) {
        if (product == null) {
            throw new IllegalArgumentException("El producto es obligatorio.");
        }
        formMode = FormMode.EDIT;
        resetTouchedState();
        clearErrors();
        codeField.setText(product.getCode());
        codeField.setEditable(false);
        codeField.setBackground(new Color(243, 244, 246));
        nameField.setText(product.getName());
        priceField.setText(product.getPrice().toPlainString());
        stockField.setText(String.valueOf(product.getStock()));
        saveButton.setText("Guardar cambios");
    }

    private void resetTouchedState() {
        codeTouched = false;
        nameTouched = false;
        priceTouched = false;
        stockTouched = false;
    }

    public FormMode getFormMode() {
        return formMode;
    }

    private boolean validateAndDisplay(JTextField field, JLabel errorLabel) {
        String message;
        if (field == codeField) {
            message = validateCodeField();
        } else if (field == nameField) {
            message = validateNameField();
        } else if (field == priceField) {
            message = validatePriceField();
        } else {
            message = validateStockField();
        }

        if (message == null) {
            clearFieldError(field, errorLabel);
            return true;
        }
        showFieldError(field, errorLabel, message);
        return false;
    }

    public void setOnCancel(Runnable action) {
        cancelButton.addActionListener(event -> {
            clearErrors();
            if (action != null) {
                action.run();
            }
        });
    }

    public void setOnSave(Runnable action) {
        saveButton.addActionListener(event -> {
            if (action != null) {
                action.run();
            }
        });
    }

    public ProductDTO buildProductDTO() {
        return new ProductDTO(
                codeField.getText(),
                nameField.getText(),
                new java.math.BigDecimal(priceField.getText().trim().isEmpty()
                        ? "0"
                        : priceField.getText().trim()),
                Integer.parseInt(stockField.getText().trim().isEmpty()
                        ? "0"
                        : stockField.getText().trim()));
    }

    public JTextField getCodeField() {
        return codeField;
    }

    public JTextField getNameField() {
        return nameField;
    }

    public JTextField getPriceField() {
        return priceField;
    }

    public JTextField getStockField() {
        return stockField;
    }

    public JLabel getCodeErrorLabel() {
        return codeError;
    }

    public JLabel getNameErrorLabel() {
        return nameError;
    }

    public JLabel getPriceErrorLabel() {
        return priceError;
    }

    public JLabel getStockErrorLabel() {
        return stockError;
    }

    public String getCode() {
        return codeField.getText();
    }

    @Override
    public String getName() {
        return nameField.getText();
    }

    public String getPriceText() {
        return priceField.getText();
    }

    public String getStockText() {
        return stockField.getText();
    }

    public void clearForm() {
        resetTouchedState();
        codeField.setText("");
        nameField.setText("");
        priceField.setText("");
        stockField.setText("");
        clearErrors();
    }

    public void resetForNewProduct() {
        prepareForCreate("");
    }

    public void clearErrors() {
        clearFieldError(codeField, codeError);
        clearFieldError(nameField, nameError);
        clearFieldError(priceField, priceError);
        clearFieldError(stockField, stockError);
        hideAlert();
    }

    public void showFieldError(JTextField field, JLabel errorLabel, String message) {
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ERROR, 1),
                BorderFactory.createEmptyBorder(7, 10, 7, 10)));
        errorLabel.setText(message);
    }

    public void clearFieldError(JTextField field, JLabel errorLabel) {
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(211, 214, 221), 1),
                BorderFactory.createEmptyBorder(7, 10, 7, 10)));
        errorLabel.setText(" ");
    }

    public void showErrorAlert(String title, String message) {
        successAlert = false;
        updateAlert(title, message, "×", ERROR, new Color(254, 242, 242));
    }

    public void showSuccessAlert(String title, String message) {
        successAlert = true;
        updateAlert(title, message, "✓", SUCCESS, new Color(236, 253, 245));
    }

    private void updateAlert(String title, String message, String icon, Color color, Color background) {
        JLabel titleLabel = (JLabel) alertPanel.getClientProperty("title");
        JLabel messageLabel = (JLabel) alertPanel.getClientProperty("message");
        JLabel iconLabel = (JLabel) alertPanel.getClientProperty("icon");
        if (titleLabel != null) {
            titleLabel.setText(title);
        }
        if (messageLabel != null) {
            messageLabel.setText("<html>" + message.replace("\n", "<br>") + "</html>");
        }
        if (iconLabel != null) {
            iconLabel.setText(icon);
            iconLabel.setForeground(color);
        }
        alertPanel.setBackground(background);
        alertPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 1),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        alertPanel.setVisible(true);
        revalidate();
        repaint();
    }

    public void hideAlert() {
        alertPanel.setVisible(false);
    }

    public void setOnAlertAccept(Runnable action) {
        onAlertAccept = action == null ? () -> { } : action;
    }

    private void dismissAlert() {
        boolean returnToCatalog = successAlert;
        hideAlert();
        if (returnToCatalog && onAlertAccept != null) {
            onAlertAccept.run();
        }
    }

    private JPanel createAlertPanel() {
        JPanel panel = new JPanel(new BorderLayout(12, 0));
        panel.setOpaque(true);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        panel.setVisible(false);

        JLabel icon = new JLabel("×", SwingConstants.CENTER);
        icon.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
        icon.setPreferredSize(new Dimension(32, 32));

        JLabel titleLabel = new JLabel("Error en la operación");
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        titleLabel.setForeground(TEXT_MAIN);

        JLabel messageLabel = new JLabel();
        messageLabel.setFont(normalFont());
        messageLabel.setForeground(TEXT_SECONDARY);

        JPanel textPanel = new JPanel(new BorderLayout(0, 4));
        textPanel.setOpaque(false);
        textPanel.add(titleLabel, BorderLayout.NORTH);
        textPanel.add(messageLabel, BorderLayout.CENTER);

        JButton closeButton = new JButton("×");
        closeButton.setContentAreaFilled(false);
        closeButton.setBorderPainted(false);
        closeButton.setFocusPainted(false);
        closeButton.setForeground(TEXT_SECONDARY);
        closeButton.addActionListener(event -> dismissAlert());

        JButton actionButton = new JButton("Aceptar");
        actionButton.setOpaque(true);
        actionButton.setContentAreaFilled(true);
        actionButton.setBorderPainted(false);
        actionButton.setFocusPainted(false);
        actionButton.setBackground(PRIMARY);
        actionButton.setForeground(Color.WHITE);
        actionButton.setBorder(BorderFactory.createEmptyBorder(9, 18, 9, 18));
        actionButton.addActionListener(event -> dismissAlert());

        JPanel actionArea = new JPanel(new BorderLayout(4, 0));
        actionArea.setOpaque(false);
        actionArea.add(closeButton, BorderLayout.NORTH);
        actionArea.add(actionButton, BorderLayout.SOUTH);

        panel.add(icon, BorderLayout.WEST);
        panel.add(textPanel, BorderLayout.CENTER);
        panel.add(actionArea, BorderLayout.EAST);
        panel.putClientProperty("title", titleLabel);
        panel.putClientProperty("message", messageLabel);
        panel.putClientProperty("icon", icon);
        return panel;
    }
}
