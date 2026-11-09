
package adapters.inbound.rest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class OpenApiContractTest {


private String readContract() throws IOException {
    return new String(
            Files.readAllBytes(Paths.get("docs/openapi.yaml")),
            StandardCharsets.UTF_8)
            .replace("\r\n", "\n");
}


    private String section(
            String document, String start, String end) {

        int startIndex = document.indexOf(start);

        assertTrue(startIndex >= 0, "No se encontró: " + start);

        int endIndex = document.indexOf(end, startIndex + start.length());

        assertTrue(endIndex >= 0, "No se encontró el final: " + end);

        return document.substring(startIndex, endIndex);
    }

    @Test
    void productsGetContractDeclaresObservedMethodAndStatus()
            throws IOException {

        String document = readContract();

        String path = section(
                document,
                "  /api/products:\n",
                "  /api/products/{code}:\n");

        String operation = section(
                path,
                "    get:\n",
                "    post:\n");

        assertTrue(operation.contains("'200':"));
        assertTrue(operation.contains("application/json:"));
        assertTrue(operation.contains("ProductResponse"));
    }

   @Test
void productPostContractDeclaresCreatedAndValidationError()
        throws IOException {

    String document = readContract();

    String path = section(
            document,
            "  /api/products:\n",
            "  /api/products/{code}:\n");

    int postIndex = path.indexOf("    post:\n");

    assertTrue(postIndex >= 0);

    String operation = path.substring(postIndex);

    assertTrue(operation.contains("'201':"));
    assertTrue(operation.contains("'400':"));
    assertTrue(operation.contains("ProductRequest"));
    assertTrue(operation.contains("ProductResponse"));
}
    @Test
    void productPutContractDeclaresSuccessAndNotFound()
            throws IOException {

        String document = readContract();

        String path = section(
                document,
                "  /api/products/{code}:\n",
                "  /api/sales:\n");

        assertTrue(path.contains("    put:\n"));
        assertTrue(path.contains("'200':"));
        assertTrue(path.contains("'400':"));
        assertTrue(path.contains("'404':"));
        assertTrue(path.contains("ProductRequest"));
        assertTrue(path.contains("ProductResponse"));
    }

    @Test
    void salePostContractDeclaresSuccessAndErrors()
            throws IOException {

        String document = readContract();

        String path = section(
                document,
                "  /api/sales:\n",
                "components:\n");

        assertTrue(path.contains("    post:\n"));
        assertTrue(path.contains("'200':"));
        assertTrue(path.contains("'400':"));
        assertTrue(path.contains("'404':"));
        assertTrue(path.contains("SaleRequest"));
        assertTrue(path.contains("SaleResponse"));
    }
}
