package com.max2k.cs.service.impl;

import com.max2k.cs.DTO.ResultDTO;
import com.max2k.cs.DTO.UserDTO;
import com.max2k.cs.service.ValidationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@Service
public class ValidationServiceImpl implements ValidationService {

    private final int allowedAgeInYears;

    public ValidationServiceImpl(@Value("${com.max2k.cs.allowedAgeInYears}") int allowedAgeInYears) {
        this.allowedAgeInYears = allowedAgeInYears;
    }

    @Override
    public ResultDTO validateNewUser(UserDTO user) {

        if (ChronoUnit.YEARS.between(
                user.getDateOfBirth().atZone(ZoneId.systemDefault()),
                Instant.now().atZone(ZoneId.systemDefault())
        )<allowedAgeInYears)
            return new ResultDTO(false,"Not allowed to register user age under "+allowedAgeInYears+" years");

        return new ResultDTO(true,"Ok")

    }

    @Override
    public ResultDTO validateUserFields(Map<String, String> allParams) {
        return new ResultDTO(false,"not implemented yet");
    }

    @Override
    public ResultDTO validateBirthdayRange(String dateFrom, String dateTo) {
        return new ResultDTO(false,"not implemented yet");
    }
}
