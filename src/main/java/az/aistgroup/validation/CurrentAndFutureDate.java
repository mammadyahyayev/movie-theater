package az.aistgroup.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * This validation annotation is used to validate date
 * values. It is valid if the date is after yesterday.
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = {ElementType.FIELD, ElementType.PARAMETER})
@Constraint(validatedBy = CurrentAndFutureDateValidator.class)
@Documented
public @interface CurrentAndFutureDate {
    String message() default "Date cannot be in the past";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
