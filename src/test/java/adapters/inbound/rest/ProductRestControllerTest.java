package adapters.inbound.rest;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;

import com.example.adapters.inbound.rest.ProductRestController;
import com.example.domain.model.Product;
import com.example.domain.model.Sale;
import com.example.ports.inbound.SaleFacadePort;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProductRestControllerTest {

    private final SaleFacadePort facade = mock(SaleFacadePort.class);
    private final LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        validator.afterPropertiesSet();
        mockMvc = MockMvcBuilders.standaloneSetup(new ProductRestController(facade))
                .setValidator(validator)
                .build();
    }

    @AfterEach
    void tearDown() {
        validator.destroy();
    }

    @Test
    void listProducts_returns_rest_fields_without_domain_properties() throws Exception {
        when(facade.listProducts()).thenReturn(Arrays.asList(
                new Product("P00001", "Café", new BigDecimal("12.50"), 4)));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("P00001"))
                .andExpect(jsonPath("$[0].name").value("Café"))
                .andExpect(jsonPath("$[0].price").value(12.50))
                .andExpect(jsonPath("$[0].stock").value(4))
                .andExpect(jsonPath("$[0].class").doesNotExist());
    }

    @Test
    void registerProduct_generates_code_and_returns_created_response() throws Exception {
        when(facade.getNextProductCode()).thenReturn("P00003");

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Café\",\"price\":12.50,\"stock\":4}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("P00003"))
                .andExpect(jsonPath("$.name").value("Café"));

        org.mockito.ArgumentCaptor<Product> product =
                org.mockito.ArgumentCaptor.forClass(Product.class);
        verify(facade).registerProduct(product.capture());
        assertEquals("P00003", product.getValue().getCode());
    }

    @Test
    void registerProduct_rejects_client_supplied_code() throws Exception {
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"P99999\",\"name\":\"Café\",\"price\":12.50,\"stock\":4}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerProduct_rejects_invalid_structure_and_values() throws Exception {
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\" \",\"price\":-1,\"stock\":-1}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateProduct_uses_path_code_instead_of_request_identifier() throws Exception {
        mockMvc.perform(put("/api/products/P00002")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Café actualizado\",\"price\":13.00,\"stock\":5}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("P00002"))
                .andExpect(jsonPath("$.name").value("Café actualizado"));

        org.mockito.ArgumentCaptor<Product> product =
                org.mockito.ArgumentCaptor.forClass(Product.class);
        verify(facade).updateProduct(product.capture());
        assertEquals("P00002", product.getValue().getCode());
    }

    @Test
    void updateProduct_rejects_identifier_in_request_body() throws Exception {
        mockMvc.perform(put("/api/products/P00002")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"P99999\",\"name\":\"Café\",\"price\":13.00,\"stock\":5}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateProduct_rejects_invalid_path_code() throws Exception {
        mockMvc.perform(put("/api/products/not-a-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Café\",\"price\":13.00,\"stock\":5}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void processSale_preserves_root_object_request_and_returns_rest_response() throws Exception {
        Sale sale = new Sale();
        sale.addDetail(new Product("P00001", "Café", new BigDecimal("12.50"), 4), 2);
        when(facade.processSale(anyMap())).thenReturn(sale);

        mockMvc.perform(post("/api/sales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"P00001\":2}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lines[0].productCode").value("P00001"))
                .andExpect(jsonPath("$.lines[0].quantity").value(2))
                .andExpect(jsonPath("$.subtotal").value(25.0))
                .andExpect(jsonPath("$.total").exists())
                .andExpect(jsonPath("$.details").doesNotExist());

        org.mockito.ArgumentCaptor<Map<String, Integer>> products =
                org.mockito.ArgumentCaptor.forClass(Map.class);
        verify(facade).processSale(products.capture());
        assertEquals(Integer.valueOf(2), products.getValue().get("P00001"));
    }

    @Test
    void processSale_rejects_invalid_code_and_quantity() throws Exception {
        mockMvc.perform(post("/api/sales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"bad-code\":0}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void processSale_rejects_empty_product_map() throws Exception {
        mockMvc.perform(post("/api/sales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }
}
