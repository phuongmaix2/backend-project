package com.billwise.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Currency;
import java.util.Locale;

public class CurrencyCodeValidator
        implements ConstraintValidator<ValidCurrency, String> {

    @Override
    public boolean isValid(
            String value,
            ConstraintValidatorContext context) {
        // Null and blank values should be handled separately by @NotBlank.
        if (value == null || value.isBlank()) {
            return true;
        }

        String uppercaseValue = value.toUpperCase(Locale.ROOT);

        // Require the client to send uppercase currency codes.
        if (!value.equals(uppercaseValue)) {
            return false;
        }

        try {
            Currency currency = Currency.getInstance(value);

            return currency.getCurrencyCode().equals(value);
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }
}