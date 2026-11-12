package com.billwise.validation;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CurrencyCodeValidatorTests {

    private final CurrencyCodeValidator validator =
        new CurrencyCodeValidator();

    @Test
    void acceptsValidUppercaseCurrencyCodes() {
        assertThat(validator.isValid("EUR", null)).isTrue();
        assertThat(validator.isValid("USD", null)).isTrue();
        assertThat(validator.isValid("JPY", null)).isTrue();
    }

    @Test
    void rejectsLowercaseCurrencyCode() {
        assertThat(validator.isValid("eur", null)).isFalse();
    }

    @Test
    void rejectsUnknownCurrencyCode() {
        assertThat(validator.isValid("ABC", null)).isFalse();
    }

    @Test
    void allowsNullAndBlankForOtherValidatorsToHandle() {
        assertThat(validator.isValid(null, null)).isTrue();
        assertThat(validator.isValid("", null)).isTrue();
        assertThat(validator.isValid("   ", null)).isTrue();
    }
}