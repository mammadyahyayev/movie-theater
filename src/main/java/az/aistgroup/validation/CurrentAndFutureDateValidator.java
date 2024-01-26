package az.aistgroup.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class CurrentAndFutureDateValidator implements ConstraintValidator<CurrentAndFutureDate, LocalDate> {
    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        return value != null && (value.isEqual(today) || value.isAfter(yesterday));
    }
}
