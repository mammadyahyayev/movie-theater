package az.aistgroup.domain.dto;

import java.time.LocalDateTime;

public record OperationResponseDto(boolean success, String message, LocalDateTime dateTime) {
    public OperationResponseDto(boolean success, String message) {
        this(success, message, LocalDateTime.now());
    }
}
