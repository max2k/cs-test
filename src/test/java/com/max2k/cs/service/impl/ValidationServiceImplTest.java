package com.max2k.cs.service.impl;

import com.max2k.cs.DTO.UserDTO;
import com.max2k.cs.service.ValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;


class ValidationServiceImplTest {

    ValidationService validationService;

    @BeforeEach
    void setUp() {
        validationService=new ValidationServiceImpl(18);
    }

    @Test
    void validateNewUserAgeOver18() {
        UserDTO userDTO=Mockito.m
    }
}