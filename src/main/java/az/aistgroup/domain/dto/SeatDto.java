package az.aistgroup.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SeatDto(
        @NotNull
        @NotBlank
        String name
) {
}
