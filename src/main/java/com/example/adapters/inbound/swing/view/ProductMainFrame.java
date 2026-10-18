package com.example.adapters.inbound.swing.view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import com.example.adapters.inbound.swing.controller.CartController;
import com.example.adapters.inbound.swing.controller.ProductController;
import com.example.adapters.inbound.swing.model.ProductDTO;

public class ProductMainFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(ProductMainFrame.class.getName());
    private static final String CATALOG_CARD = "catalog";
    private static final String FORM_CARD = "form";
    private static final String DETAIL_CARD = "detail";
    private static final String CART_CARD = "cart";

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel mainContent = new JPanel(cardLayout);
    private final ProductCatalogView catalogView;
    private final CartView cartView;
    private final ProductFormView formView;
    private final ProductDetailView detailView;
    private final transient ProductController controller;
    private final JButton catalogButton;
    private final JButton cartButton;

    public ProductMainFrame(ProductController controller, CartController cartController) {
        super("Prototipo Ventas");
        this.controller = controller;
        this.catalogView = new ProductCatalogView(controller.getTableModel());
        this.cartView = new CartView(cartController);
        this.formView = new ProductFormView();
        this.detailView = new ProductDetailView();
        this.catalogButton = new JButton();
        this.cartButton = new JButton();

        buildMainWindow();
        bindNavigation();
        bindFormActions();
        loadInitialData();
    }

    private void buildMainWindow() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);
        add(mainContent, BorderLayout.CENTER);

        mainContent.setBackground(new Color(243, 235, 255));
        mainContent.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainContent.setPreferredSize(new Dimension(980, 650));

        mainContent.add(catalogView, CATALOG_CARD);
        mainContent.add(formView, FORM_CARD);
        mainContent.add(detailView, DETAIL_CARD);
        mainContent.add(cartView, CART_CARD);

        cardLayout.show(mainContent, CATALOG_CARD);
        pack();
        setMinimumSize(new Dimension(980, 620));
        setLocationRelativeTo(null);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(new Color(45, 42, 74));
        sidebar.setPreferredSize(new Dimension(190, 0));

        JPanel topBar = new JPanel();
        topBar.setLayout(new javax.swing.BoxLayout(topBar, javax.swing.BoxLayout.Y_AXIS));
        topBar.setBackground(new Color(45, 42, 74));
        topBar.setBorder(BorderFactory.createEmptyBorder(16, 14, 0, 14));
        JLabel brand = new JLabel("Prototipo Ventas");
        brand.setForeground(Color.WHITE);
        brand.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        topBar.add(brand);
        topBar.add(javax.swing.Box.createVerticalStrut(30));

        JPanel menu = new JPanel();
        menu.setLayout(new javax.swing.BoxLayout(menu, javax.swing.BoxLayout.Y_AXIS));
        menu.setBackground(new Color(45, 42, 74));
        menu.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        addMenuItem(menu, "Inicio", false);
        addMenuItem(menu, "Productos", true, catalogButton);
        addMenuItem(menu, "Carrito", false, cartButton);
        addMenuItem(menu, "Ventas", false);
        addMenuItem(menu, "Configuración", false);

        JPanel navigation = new JPanel(new BorderLayout());
        navigation.setBackground(new Color(45, 42, 74));
        navigation.add(topBar, BorderLayout.NORTH);
        navigation.add(menu, BorderLayout.CENTER);
        sidebar.add(navigation, BorderLayout.NORTH);
        return sidebar;
    }

    private void addMenuItem(JPanel menu, String text, boolean active) {
        addMenuItem(menu, text, active, new JButton());
    }

    private void addMenuItem(JPanel menu, String text, boolean active, JButton button) {
        button.setText(text);
        button.setFocusPainted(false);
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        button.setBackground(active ? new Color(108, 62, 184) : new Color(45, 42, 74));
        button.setForeground(active ? Color.WHITE : new Color(229, 231, 235));
        button.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        button.setBorderPainted(false);
        button.setEnabled(active || "Carrito".equals(text));
        button.setPreferredSize(new Dimension(166, 40));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        menu.add(button);
        menu.add(javax.swing.Box.createVerticalStrut(8));
    }

    private void bindNavigation() {
        catalogButton.addActionListener(event -> showCatalog());
        cartButton.addActionListener(event -> showCart());
        catalogView.setOnNewProduct(this::showForm);
        catalogView.setOnViewProduct(product -> {
            detailView.display(product);
            showDetail();
        });
        detailView.setOnEdit(this::showEditForm);
        formView.setOnCancel(this::cancelForm);
        detailView.setOnBack(this::showCatalog);
    }

    private void bindFormActions() {
        formView.setOnSave(() -> {
            if (!formView.validateAllFields()) {
                return;
            }
            boolean editing = formView.getFormMode() == ProductFormView.FormMode.EDIT;
            try {
                String code = formView.getCode();
                String name = formView.getName();
                String priceText = formView.getPriceText();
                String stockText = formView.getStockText();

                ProductDTO dto = controller.validateAndBuildProduct(code, name, priceText, stockText);
                if (editing) {
                    controller.updateProduct(dto);
                    detailView.display(dto);
                } else {
                    controller.registerProduct(dto);
                }
                formView.clearForm();
                if (editing) {
                    formView.setOnAlertAccept(() -> {
                        detailView.display(dto);
                        showDetail();
                    });
                    formView.showSuccessAlert("Producto actualizado", "Los cambios se guardaron correctamente.");
                } else {
                    formView.setOnAlertAccept(this::showCatalog);
                    formView.showSuccessAlert("Producto registrado", "El producto se registró correctamente.");
                }
            } catch (IllegalArgumentException ex) {
                LOGGER.log(Level.SEVERE, editing ? "Error updating product" : "Error registering product", ex);
                showSaveError(editing);
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Unexpected error saving product", ex);
                showSaveError(editing);
            }
        });
    }

    private void showSaveError(boolean editing) {
        String message = editing
                ? "No fue posible actualizar el producto.\nVerifique los datos e intente nuevamente."
                : "No fue posible registrar el producto.\nVerifique los datos capturados e intente nuevamente.";
        formView.showErrorAlert("Error en la operación", message);
    }

    private void showEditForm(ProductDTO product) {
        formView.prepareForEdit(product);
        formView.setOnAlertAccept(() -> {
            detailView.display(product);
            showDetail();
        });
        cardLayout.show(mainContent, FORM_CARD);
    }

    private void cancelForm() {
        if (formView.getFormMode() == ProductFormView.FormMode.EDIT) {
            cardLayout.show(mainContent, DETAIL_CARD);
        } else {
            showCatalog();
        }
    }

    private void loadInitialData() {
        controller.loadProducts();
    }

    public void showCatalog() {
        controller.loadProducts();
        cardLayout.show(mainContent, CATALOG_CARD);
    }

    public void showForm() {
        try {
            String code = controller.getNextProductCode();
            formView.prepareForCreate(code);
            formView.setOnAlertAccept(this::showCatalog);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unable to generate a new product code", ex);
            formView.prepareForCreate("");
            formView.setOnAlertAccept(this::showCatalog);
            formView.showErrorAlert(
                    "Error en la operación",
                    "No es posible generar un nuevo código de producto.");
        }
        cardLayout.show(mainContent, FORM_CARD);
    }

    public void showDetail() {
        cardLayout.show(mainContent, DETAIL_CARD);
    }

    public void showCart() {
        cardLayout.show(mainContent, CART_CARD);
    }
}