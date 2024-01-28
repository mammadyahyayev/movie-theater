package az.aistgroup.domain.dto;

import java.math.BigDecimal;

public interface UserView {
    String getFirstName();

    String getLastName();

    String getFatherName();

    String getUsername();

    BigDecimal getBalance();
}
