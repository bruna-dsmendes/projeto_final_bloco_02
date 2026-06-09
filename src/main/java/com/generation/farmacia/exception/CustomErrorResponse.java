package com.generation.farmacia.exception;

import java.time.LocalDateTime;
import java.util.List;

public record CustomErrorResponse(
    LocalDateTime timestamp,
    Integer status,
    String error,
    String message,
    List<String> details
) {}