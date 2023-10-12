package com.blackjack.jraoms.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmailAlreadyExistsExceptionTest {
    @Test
    public void testExceptionMessage() {
        String errorMessage = "Email address already exists";
        EmailAlreadyExistsException exception = new EmailAlreadyExistsException(errorMessage);
        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    public void testExceptionInheritance() {
        String errorMessage = "Email address already exists";
        EmailAlreadyExistsException exception = new EmailAlreadyExistsException(errorMessage);
        assertTrue(exception instanceof RuntimeException);
    }
}