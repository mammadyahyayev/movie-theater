package az.aistgroup.domain.dto;

import java.math.BigDecimal;

public interface UserViewDto {
    String getFirstName();

    String getLastName();

    String getFatherName();

    String getUsername();

    BigDecimal getBalance();
}
