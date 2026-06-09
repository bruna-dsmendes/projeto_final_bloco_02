package com.generation.farmacia.exception;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.EntityNotFoundException;

@Hidden
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Captura erro de validação (@Valid, @NotNull, @Min, etc.)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<String> erros = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.toList());

        CustomErrorResponse erroResponse = new CustomErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Erro de Validação nos campos",
                "Um ou mais campos contêm dados inválidos. Verifique os detalhes.",
                erros
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erroResponse);
    }

    // Captura exceções do CarrinhoService
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<CustomErrorResponse> handleResponseStatus(ResponseStatusException ex) {
        CustomErrorResponse erroResponse = new CustomErrorResponse(
                LocalDateTime.now(),
                ex.getStatusCode().value(),
                "Erro na Requisição",
                ex.getReason(),
                List.of()
        );

        return ResponseEntity.status(ex.getStatusCode()).body(erroResponse);
    }

    // Captura erros quando uma entidade não é encontrada no banco pelo JPA
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<CustomErrorResponse> handleNotFound(EntityNotFoundException ex) {
        CustomErrorResponse erroResponse = new CustomErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Recurso Não Encontrado",
                ex.getMessage(),
                List.of()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erroResponse);
    }

    // Filtragem final: captura qualquer outro erro genérico interno (500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomErrorResponse> handleGenericException(Exception ex) {
        CustomErrorResponse erroResponse = new CustomErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Erro Interno do Servidor",
                "Ocorreu um erro inesperado no sistema. Tente novamente mais tarde.",
                List.of(ex.getMessage() != null ? ex.getMessage() : "Sem detalhes disponíveis")
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erroResponse);
    }
}