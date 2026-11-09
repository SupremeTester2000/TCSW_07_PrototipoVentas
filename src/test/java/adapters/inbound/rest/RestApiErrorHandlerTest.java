
package adapters.inbound.rest;

import java.math.BigDecimal;
import java.util.Collections;

import com.example.adapters.inbound.rest.ProductRestController;
import com.example.adapters.inbound.rest.exception.GlobalExceptionHandler;
import com.example.domain.exception.ProductNotFound;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RestApiErrorHandlerTest {

    private final SaleFacadePort facade = mock(SaleFacadePort.class);
    private final LocalValidatorFactoryBean validator =
            new LocalValidatorFactoryBean();

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(new ProductRestController(facade))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @AfterEach
    void tearDown() {
        validator.destroy();
    }

    @Test
    void getProducts_returns200WithJson() throws Exception {
        when(facade.listProducts()).thenReturn(Collections.singletonList(
                new Product("P00001", "Café", new BigDecimal("12.50"), 4)));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].code").value("P00001"))
                .andExpect(jsonPath("$[0].name").value("Café"))
                .andExpect(jsonPath("$[0].stock").value(4));
    }

    @Test
    void postProduct_returns201WithJson() throws Exception {
        when(facade.getNextProductCode()).thenReturn("P00002");

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Café\",\"price\":12.50,\"stock\":4}"))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("P00002"))
                .andExpect(jsonPath("$.name").value("Café"));
    }

    @Test
    void putProduct_returns200WithJson() throws Exception {
        mockMvc.perform(put("/api/products/P00001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Café actualizado\","
                                + "\"price\":13.00,\"stock\":5}"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("P00001"))
                .andExpect(jsonPath("$.name").value("Café actualizado"));
    }

    @Test
    void postSale_returns200WithJson() throws Exception {
        Sale sale = new Sale();
        sale.addDetail(
                new Product("P00001", "Café", new BigDecimal("12.50"), 4),
                2);

        when(facade.processSale(anyMap())).thenReturn(sale);

        mockMvc.perform(post("/api/sales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"P00001\":2}"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.lines[0].productCode").value("P00001"))
                .andExpect(jsonPath("$.lines[0].quantity").value(2))
                .andExpect(jsonPath("$.total").exists());
    }

    @Test
    void invalidProduct_returns400WithStandardError() throws Exception {
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\" \",\"price\":-1,\"stock\":-1}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("Solicitud inválida"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").exists());
    }

    @Test
    void missingProduct_returns404WithStandardError() throws Exception {
        doThrow(new ProductNotFound("P99999"))
                .when(facade).updateProduct(any(Product.class));

        mockMvc.perform(put("/api/products/P99999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Café\",\"price\":12.50,\"stock\":4}"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("Producto inexistente"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail")
                        .value("No se encontró el producto solicitado"));
    }

    @Test
    void unexpectedError_doesNotExposeInternalInformation() throws Exception {
        when(facade.listProducts()).thenThrow(
                new IllegalStateException("SELECT password=secreto"));

        String response = mockMvc.perform(get("/api/products"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title")
                        .value("Error interno del servidor"))
                .andExpect(jsonPath("$.status").value(500))
                .andReturn()
                .getResponse()
                .getContentAsString();

        org.junit.jupiter.api.Assertions.assertFalse(
                response.contains("secreto"));
        org.junit.jupiter.api.Assertions.assertFalse(
                response.contains("SELECT"));
        org.junit.jupiter.api.Assertions.assertFalse(
                response.contains("IllegalStateException"));
    }

    @Test
    void malformedJson_returns400WithStandardError() throws Exception {
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Solicitud inválida"));
    }
@Test
void saleWithMissingProduct_returns404WithStandardError() throws Exception {

    when(facade.processSale(anyMap()))
            .thenThrow(new ProductNotFound("P99999"));

    mockMvc.perform(post("/api/sales")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"P99999\":1}"))
            .andExpect(status().isNotFound())
            .andExpect(content().contentTypeCompatibleWith(
                    MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.title").value("Producto inexistente"))
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.detail")
                    .value("No se encontró el producto solicitado"));
}
}
