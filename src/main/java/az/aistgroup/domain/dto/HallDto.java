package az.aistgroup.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record HallDto(
        @NotNull
        @NotBlank
        String name,

        @PositiveOrZero
        int capacity
) {
}
