package com.zhouyi.config;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MailPropertiesValidatorTest {

    @Test
    void testValidProperties() {
        MailPropertiesValidator validator = new MailPropertiesValidator();
        ReflectionTestUtils.setField(validator, "username", "test@example.com");
        ReflectionTestUtils.setField(validator, "password", "secret");

        assertDoesNotThrow(validator::afterPropertiesSet);
    }

    @Test
    void testMissingUsername() {
        MailPropertiesValidator validator = new MailPropertiesValidator();
        ReflectionTestUtils.setField(validator, "username", "");
        ReflectionTestUtils.setField(validator, "password", "secret");

        IllegalStateException exception = assertThrows(IllegalStateException.class, validator::afterPropertiesSet);
        assertTrue(exception.getMessage().contains("MAIL_USERNAME"));
    }

    @Test
    void testUnresolvedUsername() {
        MailPropertiesValidator validator = new MailPropertiesValidator();
        ReflectionTestUtils.setField(validator, "username", "${MAIL_USERNAME}");
        ReflectionTestUtils.setField(validator, "password", "secret");

        IllegalStateException exception = assertThrows(IllegalStateException.class, validator::afterPropertiesSet);
        assertTrue(exception.getMessage().contains("MAIL_USERNAME"));
    }

    @Test
    void testMissingPassword() {
        MailPropertiesValidator validator = new MailPropertiesValidator();
        ReflectionTestUtils.setField(validator, "username", "test@example.com");
        ReflectionTestUtils.setField(validator, "password", null);

        IllegalStateException exception = assertThrows(IllegalStateException.class, validator::afterPropertiesSet);
        assertTrue(exception.getMessage().contains("MAIL_PASSWORD"));
    }

    @Test
    void testUnresolvedPassword() {
        MailPropertiesValidator validator = new MailPropertiesValidator();
        ReflectionTestUtils.setField(validator, "username", "test@example.com");
        ReflectionTestUtils.setField(validator, "password", "${MAIL_PASSWORD}");

        IllegalStateException exception = assertThrows(IllegalStateException.class, validator::afterPropertiesSet);
        assertTrue(exception.getMessage().contains("MAIL_PASSWORD"));
    }
}
