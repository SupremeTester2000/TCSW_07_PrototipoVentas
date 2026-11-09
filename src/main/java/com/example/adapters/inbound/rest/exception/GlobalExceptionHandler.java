
package com.example.adapters.inbound.rest.exception;

import java.util.stream.Collectors;

import com.example.domain.exception.ProductNotFound;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;

import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ProductNotFound.class)
    public ResponseEntity<ApiError> handleProductNotFound(
            ProductNotFound exception) {

        return createResponse(
                HttpStatus.NOT_FOUND,
                "Producto inexistente",
                "No se encontró el producto solicitado"
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(
            MethodArgumentNotValidException exception) {

        String fields = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getField)
                .distinct()
                .collect(Collectors.joining(", "));

        String detail = fields.isEmpty()
                ? "La solicitud contiene datos inválidos"
                : "Revise los siguientes campos: " + fields;

        return createResponse(
                HttpStatus.BAD_REQUEST,
                "Solicitud inválida",
                detail
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleUnreadableMessage(
            HttpMessageNotReadableException exception) {

        return createResponse(
                HttpStatus.BAD_REQUEST,
                "Solicitud inválida",
                "El cuerpo de la solicitud contiene JSON inválido o datos incompatibles"
        );
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiError> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException exception) {

        return createResponse(
                HttpStatus.METHOD_NOT_ALLOWED,
                "Método HTTP no permitido",
                "El método utilizado no está permitido para este recurso"
        );
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(
            NoHandlerFoundException exception) {

        return createResponse(
                HttpStatus.NOT_FOUND,
                "Recurso inexistente",
                "No se encontró el recurso solicitado"
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(
            Exception exception) {

        LOGGER.error(
                "Error inesperado durante una solicitud REST",
                exception
        );

        return createResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Error interno del servidor",
                "Ocurrió un error inesperado. Intente nuevamente más tarde"
        );
    }

    private ResponseEntity<ApiError> createResponse(
            HttpStatus status,
            String title,
            String detail) {

        ApiError error = new ApiError(
                title,
                status.value(),
                detail
        );

        return ResponseEntity.status(status).body(error);
    }
}
