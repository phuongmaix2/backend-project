package com.billwise.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.RECORD_COMPONENT;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/** Bean-validation annotation for ISO-4217 currency codes such as EUR or USD. */

@Documented
@Constraint(validatedBy = CurrencyCodeValidator.class)
@Target({
    FIELD,
    PARAMETER,
    RECORD_COMPONENT
})
@Retention(RUNTIME)
public @interface ValidCurrency {

    String message()
        default "must be a valid uppercase ISO-4217 currency code";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
