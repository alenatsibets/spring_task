package com.example.spring;

import com.example.spring.model.User;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class UserTest {
    private static Validator validator;

    @BeforeClass
    public static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void userNameIsNull() {
        User user = new User(null, "user@gmail.com", "12345678");

        Set<ConstraintViolation<User>> constraintViolations =
                validator.validate(user);
        assertEquals(1, constraintViolations.size());

        assertTrue(constraintViolations.iterator().next().getMessage().contains("cannot be empty"));
    }

    @Test
    public void EmailIsNull() {
        User user = new User("username", null, "12345678");

        Set<ConstraintViolation<User>> constraintViolations =
                validator.validate(user);
        assertEquals(1, constraintViolations.size());

        assertTrue(constraintViolations.iterator().next().getMessage().contains("cannot be empty"));
    }

    @Test
    public void testEmailFormat() {
        User user = new User("testuser", "invalid-email", "password123");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());

        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("must be a well-formed email address", violations.iterator().next().getMessage());
        assertEquals("email", violation.getPropertyPath().toString());
    }

    @Test
    public void PasswordIsNull() {
        User user = new User("username", "user@gmail.com", null);

        Set<ConstraintViolation<User>> constraintViolations =
                validator.validate(user);
        assertEquals(1, constraintViolations.size());

        assertTrue(constraintViolations.iterator().next().getMessage().contains("cannot be empty"));
    }

    @Test
    public void testPasswordSize() {
        User user = new User("testuser", "test@example.com", "short");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());

        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("password must contains at least 8 characters", violation.getMessage());
        assertEquals("password", violation.getPropertyPath().toString());
    }

    @Test
    public void testValidUser() {
        User user = new User("testuser", "test@example.com", "password123");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }
}
