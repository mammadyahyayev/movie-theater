package az.aistgroup.domain.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class TopUpBalanceDto {

    @NotBlank(message = "{field.notBlank}")
    private String username;

    @NotNull(message = "{field.notNull}")
    @Positive
    @Digits(integer = 10, fraction = 2, message = "Amount format must be: e.g 34.75")
    private BigDecimal amount;

    public TopUpBalanceDto() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}

