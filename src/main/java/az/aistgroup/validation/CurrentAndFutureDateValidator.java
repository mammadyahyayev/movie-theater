package az.aistgroup.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;

public class CurrentAndFutureDateValidator implements ConstraintValidator<CurrentAndFutureDate, LocalDateTime> {
    @Override
    public boolean isValid(LocalDateTime value, ConstraintValidatorContext context) {
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime yesterday = today.minusDays(1);
        return value != null && (value.isEqual(today) || value.isAfter(yesterday));
    }
}
