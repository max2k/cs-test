package com.max2k.cs.service;

import com.max2k.cs.DTO.ResultDTO;
import com.max2k.cs.DTO.UserDTO;
import jakarta.validation.constraints.NotBlank;

import java.time.Instant;
import java.util.Map;

public interface ValidationService {

    ResultDTO validateNewUser(UserDTO user);

    ResultDTO validateUserFields(Map<String, String> allParams);

    ResultDTO validateBirthdayRange(@NotBlank Instant dateFrom, @NotBlank Instant dateTo);
}
