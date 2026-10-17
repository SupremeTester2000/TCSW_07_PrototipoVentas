package adapters.swing.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.FocusEvent;
import java.awt.Point;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.junit.jupiter.api.Test;

import com.example.adapters.inbound.swing.model.ProductDTO;
import com.example.adapters.inbound.swing.model.ProductTableModel;
import com.example.adapters.inbound.swing.view.ProductCatalogView;
import com.example.adapters.inbound.swing.view.ProductDetailView;
import com.example.adapters.inbound.swing.view.ProductFormView;

class ProductViewsTest {

    @Test
    void catalogFiltersProductsAndOpensDetailFromActionButton() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            ProductDTO first = new ProductDTO("P001", "Monitor", new BigDecimal("249.99"), 4);
            ProductDTO second = new ProductDTO("P002", "Mouse", new BigDecimal("19.99"), 8);
            ProductTableModel model = new ProductTableModel(Arrays.asList(first, second));
            ProductCatalogView view = new ProductCatalogView(model);
            AtomicBoolean newProductOpened = new AtomicBoolean();
            ProductDTO[] viewedProduct = new ProductDTO[1];
            view.setOnNewProduct(() -> newProductOpened.set(true));
            view.setOnViewProduct(product -> viewedProduct[0] = product);

            JButton createButton = findButton(view, "+ Nuevo producto");
            assertNotNull(createButton);
            assertTrue(createButton.isOpaque());
            assertTrue(createButton.isContentAreaFilled());
            assertEquals(new java.awt.Color(108, 62, 184), createButton.getBackground());
            createButton.doClick();
            assertTrue(newProductOpened.get());

            JTextField searchField = findSearchField(view);
            assertNotNull(searchField);
            searchField.setText("mouse");
            assertEquals(1, view.getTable().getRowCount());
            assertEquals("P002", view.getTable().getValueAt(0, 0));

            assertTrue(view.getTable().editCellAt(0, 4));
            Component editor = view.getTable().getEditorComponent();
            assertTrue(editor instanceof JButton);
            ((JButton) editor).doClick();
            assertSame(second, viewedProduct[0]);

            searchField.setText("");
            assertEquals(2, view.getTable().getRowCount());
            assertEquals(5, model.getColumnCount());
            assertEquals("Acción", model.getColumnName(4));
        });
    }

    @Test
    void detailUsesReadOnlyLabelsAndProvidesWorkingReturnButton() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            ProductDetailView view = new ProductDetailView();
            ProductDTO product = new ProductDTO("P003", "Teclado", new BigDecimal("37.50"), 12);
            AtomicBoolean returned = new AtomicBoolean();
            ProductDTO[] editedProduct = new ProductDTO[1];
            view.display(product);
            view.setOnBack(() -> returned.set(true));
            view.setOnEdit(selected -> editedProduct[0] = selected);

            assertEquals(0, countTextFields(view));
            assertTrue(findLabelContaining(view, "Teclado"));
            assertTrue(findLabelContaining(view, "P003"));
            assertTrue(findLabelContaining(view, "$37.50"));
            assertTrue(findLabelContaining(view, "12"));

            JButton backButton = findButton(view, "Volver al catálogo");
            assertNotNull(backButton);
            assertTrue(backButton.isOpaque());
            assertEquals(new java.awt.Color(108, 62, 184), backButton.getBackground());
            backButton.doClick();
            assertTrue(returned.get());
            JButton editButton = findButton(view, "Editar producto");
            assertNotNull(editButton);
            editButton.doClick();
            assertSame(product, editedProduct[0]);
        });
    }

    @Test
    void formButtonsAndInlineErrorsRespondToUserInput() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            ProductFormView view = new ProductFormView();
            AtomicBoolean cancelled = new AtomicBoolean();
            AtomicBoolean saved = new AtomicBoolean();
            view.setOnCancel(() -> cancelled.set(true));
            view.setOnSave(() -> saved.set(true));

            JButton cancelButton = findButton(view, "Cancelar");
            JButton saveButton = findButton(view, "Guardar");
            assertNotNull(cancelButton);
            assertNotNull(saveButton);
            cancelButton.doClick();
            saveButton.doClick();
            assertTrue(cancelled.get());
            assertTrue(saved.get());

            view.getCodeField().setText(" ");
            view.showFieldError(
                    view.getCodeField(),
                    view.getCodeErrorLabel(),
                    "El código es obligatorio.");
            assertEquals(" ", view.getCodeField().getText());
            assertTrue(view.getCodeErrorLabel().isVisible());
            view.getCodeField().setText("P00004");
            assertEquals(" ", view.getCodeErrorLabel().getText());
        });
    }

    @Test
    void formFieldsValidateDynamicallyAndTrackTouchedState() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            ProductFormView view = new ProductFormView();

            assertEquals(" ", view.getCodeErrorLabel().getText());
            assertEquals(" ", view.getNameErrorLabel().getText());
            assertEquals(" ", view.getPriceErrorLabel().getText());
            assertEquals(" ", view.getStockErrorLabel().getText());

            view.getCodeField().setText("P");
            assertEquals("El código debe tener el formato P00000.", view.getCodeErrorLabel().getText());
            for (String code : Arrays.asList("P0", "P00", "P000", "P0000")) {
                view.getCodeField().setText(code);
                assertEquals("El código debe tener el formato P00000.", view.getCodeErrorLabel().getText());
            }
            view.getCodeField().setText("P00002");
            assertEquals(" ", view.getCodeErrorLabel().getText());
            view.getCodeField().setText("P0000");
            assertEquals("El código debe tener el formato P00000.", view.getCodeErrorLabel().getText());
            view.getCodeField().setText("P00001");
            assertEquals(" ", view.getCodeErrorLabel().getText());
            view.getCodeField().setText("P0000A");
            assertEquals("El código debe tener el formato P00000.", view.getCodeErrorLabel().getText());
            markFieldTouched(view.getCodeField());
            view.getCodeField().setText("");
            assertEquals("El código es obligatorio.", view.getCodeErrorLabel().getText());

            view.getNameField().setText("Monitor");
            assertEquals(" ", view.getNameErrorLabel().getText());
            view.getNameField().setText("");
            assertEquals(" ", view.getNameErrorLabel().getText());
            markFieldTouched(view.getNameField());
            assertEquals("El nombre es obligatorio.", view.getNameErrorLabel().getText());

            view.getPriceField().setText("hola");
            assertEquals("El precio debe ser un número válido.", view.getPriceErrorLabel().getText());
            view.getPriceField().setText("33.50");
            assertEquals(" ", view.getPriceErrorLabel().getText());

            view.getStockField().setText("No");
            assertEquals("La existencia debe ser un número entero válido.", view.getStockErrorLabel().getText());
            view.setSize(950, 650);
            layoutRecursively(view);
            int requiredMessageWidth = view.getStockErrorLabel()
                    .getFontMetrics(view.getStockErrorLabel().getFont())
                    .stringWidth("La existencia debe ser un número entero válido.");
            assertTrue(view.getStockErrorLabel().getWidth() >= requiredMessageWidth);
            view.getStockField().setText("100");
            assertEquals(" ", view.getStockErrorLabel().getText());
        });
    }

    @Test
    void finalFormValidationMarksAllFieldsTouchedAndBlocksInvalidInput() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            ProductFormView view = new ProductFormView();

            assertFalse(view.validateAllFields());
            assertEquals("El código es obligatorio.", view.getCodeErrorLabel().getText());
            assertEquals("El nombre es obligatorio.", view.getNameErrorLabel().getText());
            assertEquals("El precio debe ser un número válido.", view.getPriceErrorLabel().getText());
            assertEquals("La existencia debe ser un número entero válido.", view.getStockErrorLabel().getText());

            view.getCodeField().setText("P00124");
            view.getNameField().setText("Producto");
            view.getPriceField().setText("10.50");
            view.getStockField().setText("20");
            assertTrue(view.validateAllFields());
            assertEquals(" ", view.getCodeErrorLabel().getText());
            assertEquals(" ", view.getNameErrorLabel().getText());
            assertEquals(" ", view.getPriceErrorLabel().getText());
            assertEquals(" ", view.getStockErrorLabel().getText());
        });
    }

    @Test
    void formCreateAndEditModesKeepProductCodeReadOnly() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            ProductFormView view = new ProductFormView();
            view.prepareForCreate("P00003");
            assertEquals("P00003", view.getCode());
            assertFalse(view.getCodeField().isEditable());
            assertEquals("Guardar", findButton(view, "Guardar").getText());

            ProductDTO product = new ProductDTO(
                    "P00001", "Producto inicial", new BigDecimal("10.99"), 100);
            view.prepareForEdit(product);
            assertEquals(ProductFormView.FormMode.EDIT, view.getFormMode());
            assertEquals("P00001", view.getCode());
            assertEquals("Producto inicial", view.getName());
            assertEquals("10.99", view.getPriceText());
            assertEquals("100", view.getStockText());
            assertFalse(view.getCodeField().isEditable());
            assertEquals("Guardar cambios", findButton(view, "Guardar cambios").getText());
            assertTrue(view.validateAllFields());
        });
    }

    @Test
    void editFormActionsKeepFullButtonTextAndAlignAcrossResizes() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            ProductFormView view = new ProductFormView();
            view.prepareForEdit(new ProductDTO(
                    "P00001", "Producto", new BigDecimal("10.99"), 100));
            view.setSize(950, 650);

            JButton cancelButton = findButton(view, "Cancelar");
            JButton saveButton = findButton(view, "Guardar cambios");
            assertNotNull(cancelButton);
            assertNotNull(saveButton);
            assertEquals("Guardar cambios", saveButton.getText());

            int textWidth = saveButton.getFontMetrics(saveButton.getFont())
                    .stringWidth(saveButton.getText());
            assertTrue(saveButton.getPreferredSize().width > textWidth);

            for (int width : Arrays.asList(950, 700)) {
                view.setSize(width, 650);
                layoutRecursively(view);

                JPanel actionsPanel = (JPanel) cancelButton.getParent();
                assertSame(actionsPanel, saveButton.getParent());
                int leftInset = actionsPanel.getInsets().left;
                int rightInset = actionsPanel.getInsets().right;
                assertEquals(leftInset, cancelButton.getX());
                assertEquals(actionsPanel.getWidth() - rightInset,
                        saveButton.getX() + saveButton.getWidth());
                assertTrue(saveButton.getWidth() >= saveButton.getPreferredSize().width);
                assertTrue(Math.abs(cancelButton.getHeight() - saveButton.getHeight()) <= 2);
            }

            AtomicBoolean cancelled = new AtomicBoolean();
            AtomicBoolean saved = new AtomicBoolean();
            view.setOnCancel(() -> cancelled.set(true));
            view.setOnSave(() -> saved.set(true));
            cancelButton.doClick();
            saveButton.doClick();
            assertTrue(cancelled.get());
            assertTrue(saved.get());
        });
    }

    @Test
    void catalogFormAndDetailKeepCompactVerticalLayouts() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            ProductCatalogView catalog = new ProductCatalogView(new ProductTableModel(Arrays.asList(
                    new ProductDTO("P001", "Producto", new BigDecimal("10.00"), 1))));
            catalog.setSize(950, 650);
            layoutRecursively(catalog);
            JScrollPane tableViewport = findScrollPane(catalog);
            assertNotNull(tableViewport);
            assertTrue(tableViewport.getPreferredSize().height <= 250);

            ProductFormView form = new ProductFormView();
            form.setSize(950, 650);
            layoutRecursively(form);
            Point priceErrorLocation = javax.swing.SwingUtilities.convertPoint(
                    form.getPriceErrorLabel().getParent(),
                    form.getPriceErrorLabel().getLocation(),
                    form);
            Point stockFieldLocation = javax.swing.SwingUtilities.convertPoint(
                    form.getStockField().getParent(),
                    form.getStockField().getLocation(),
                    form);
            assertTrue(priceErrorLocation.y + form.getPriceErrorLabel().getHeight()
                    <= stockFieldLocation.y);

            ProductDetailView detail = new ProductDetailView();
            detail.setSize(950, 650);
            layoutRecursively(detail);
            assertTrue(detail.getPreferredSize().height < 500);
        });
    }

    private static JButton findButton(Container parent, String text) {
        for (Component component : parent.getComponents()) {
            if (component instanceof JButton && text.equals(((JButton) component).getText())) {
                return (JButton) component;
            }
            if (component instanceof Container) {
                JButton match = findButton((Container) component, text);
                if (match != null) {
                    return match;
                }
            }
        }
        return null;
    }

    private static JTextField findSearchField(Container parent) {
        for (Component component : parent.getComponents()) {
            if (component instanceof JTextField) {
                return (JTextField) component;
            }
            if (component instanceof Container) {
                JTextField match = findSearchField((Container) component);
                if (match != null) {
                    return match;
                }
            }
        }
        return null;
    }

    private static boolean findLabelContaining(Container parent, String text) {
        for (Component component : parent.getComponents()) {
            if (component instanceof javax.swing.JLabel
                    && ((javax.swing.JLabel) component).getText().contains(text)) {
                return true;
            }
            if (component instanceof Container && findLabelContaining((Container) component, text)) {
                return true;
            }
        }
        return false;
    }

    private static int countTextFields(Container parent) {
        int count = 0;
        for (Component component : parent.getComponents()) {
            if (component instanceof JTextField) {
                count++;
            }
            if (component instanceof Container) {
                count += countTextFields((Container) component);
            }
        }
        return count;
    }

    private static void markFieldTouched(JTextField field) {
        FocusEvent event = new FocusEvent(field, FocusEvent.FOCUS_LOST);
        for (java.awt.event.FocusListener listener : field.getFocusListeners()) {
            listener.focusLost(event);
        }
    }

    private static JScrollPane findScrollPane(Container parent) {
        for (Component component : parent.getComponents()) {
            if (component instanceof JScrollPane) {
                return (JScrollPane) component;
            }
            if (component instanceof Container) {
                JScrollPane match = findScrollPane((Container) component);
                if (match != null) {
                    return match;
                }
            }
        }
        return null;
    }

    private static void layoutRecursively(Container parent) {
        parent.doLayout();
        for (Component component : parent.getComponents()) {
            if (component instanceof Container) {
                layoutRecursively((Container) component);
            }
        }
    }
}
