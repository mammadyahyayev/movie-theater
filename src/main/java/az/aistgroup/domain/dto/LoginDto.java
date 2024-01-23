package az.aistgroup.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import static az.aistgroup.constants.ValidationConstant.PASSWORD_MAX_LENGTH;
import static az.aistgroup.constants.ValidationConstant.PASSWORD_MIN_LENGTH;

public record LoginDto(
        @NotNull
        @NotBlank
        String username,

        @NotBlank
        @NotNull
        @Size(min = PASSWORD_MIN_LENGTH, max = PASSWORD_MAX_LENGTH)
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        String password,

        boolean isRememberMe
) {
}
