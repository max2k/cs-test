package com.max2k.cs.service.impl;

import com.max2k.cs.DTO.ResultDTO;
import com.max2k.cs.DTO.UserDTO;
import com.max2k.cs.service.ValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        assertTrue(
            validationService.validateUserFields(Map.of("firstName","test firstname",
                "lastNAme","test lastName")).isValid()
        );
    }

    @Test
    void validateUserFieldsFail() {
        ResultDTO resultDTO=validationService.validateUserFields(Map.of("firstName","test firstname",
                "11lastNAme","test lastName"));
        assertFalse(resultDTO.isValid());
        assertEquals("Field 11lastNAme not allowed to update",resultDTO.getMessage());
    }

    @Test
    void validateUserFieldsFailNull() {
        Map<String,String> testMap=new HashMap<>();
        testMap.put("lastname",null);
        ResultDTO resultDTO=validationService.validateUserFields(testMap);
        assertFalse(resultDTO.isValid());
        assertEquals("Null field values not allowed",resultDTO.getMessage());
    }

    @Test
    void validateBirthdayRangeNormal() {
        assertTrue(
                validationService.validateBirthdayRange(
                        Instant.parse("1980-07-07T00:00:00Z"),
                        Instant.parse("1990-01-01T00:00:00Z")).isValid()
        );
    }

    @Test
    void validateBirthdayRangeFail() {
        assertFalse(
                validationService.validateBirthdayRange(
                        Instant.parse("1990-07-07T00:00:00Z"),
                        Instant.parse("1980-01-01T00:00:00Z")).isValid()
        );
    }

}