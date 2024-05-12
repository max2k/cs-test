package com.max2k.cs.service.impl;

import com.max2k.cs.DTO.UserDTO;
import com.max2k.cs.service.ValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


class ValidationServiceImplTest {

    ValidationService validationService;

    @BeforeEach
    void setUp() {
        validationService=new ValidationServiceImpl(18);
    }

    @Test
    void validateNewUserAgeOver18() {
        UserDTO userDTO = Mockito.mock(UserDTO.class);
        when(userDTO.getDateOfBirth()).thenReturn(Instant.parse("1980-01-01T00:00:00Z"));
        assertTrue(validationService.validateNewUser(userDTO).isValid());

    }

    @Test
    void validateNewUserAgeUnder18() {
        UserDTO userDTO = Mockito.mock(UserDTO.class);
        when(userDTO.getDateOfBirth()).thenReturn(Instant.parse("2014-01-01T00:00:00Z"));
        assertFalse(validationService.validateNewUser(userDTO).isValid());
        assertEquals("Not allowed to register user age under " + 18 + " years", validationService.validateNewUser(userDTO).getMessage());
    }

    @Test
    void validateUserFieldsNormal() {

        validationService.validateUserFields(List.of("")
        );
    }
}