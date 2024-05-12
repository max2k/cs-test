package com.max2k.cs.service.impl;

import com.max2k.cs.DTO.ResultDTO;
import com.max2k.cs.DTO.UserDTO;
import com.max2k.cs.model.User;
import com.max2k.cs.service.ValidationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

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

        return new ResultDTO(true, "Ok");

    }

    @Override
    public ResultDTO validateUserFields(Map<String, String> inputFields) {
        if (inputFields.keySet().isEmpty()) return new ResultDTO(false, "No fields set to update");

        Map<String, String> methodNames = User.getMethodNames();

        // not fields not allowed to update
        for (String key : inputFields.keySet()) {
            if (!methodNames.containsKey(key.toLowerCase(Locale.ROOT)))
                return new ResultDTO(false, "Field " + key + " not allowed to update");
        }

        // any null or empty values
        if (inputFields.values().stream().anyMatch(Objects::isNull))
            return new ResultDTO(false, "Null field values not allowed");

        return new ResultDTO(true, "Ok");
    }

    @Override
    public ResultDTO validateBirthdayRange(Instant dateFrom, Instant dateTo) {
        if (dateFrom.isAfter(dateTo))
            return new ResultDTO(false, "Invalid date range");
        return new ResultDTO(true,"Ok");
    }
}
