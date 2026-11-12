package com.billwise.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class ErrorTemplateTests {

    @Autowired
    private TemplateEngine templateEngine;

    @Test
    void rendersErrorTemplate() {
        Context context = new Context();
        context.setVariable("status", 404);
        context.setVariable("message", "Page not found");

        String html = templateEngine.process("error", context);

        assertThat(html)
                .contains("404")
                .contains("Page not found")
                .contains("Return to BillWise");
    }
}