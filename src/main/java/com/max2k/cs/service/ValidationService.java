package com.max2k.cs.service;

import com.max2k.cs.DTO.UserDTO;
import com.max2k.cs.DTO.ResultDTO;

import java.util.Map;

public interface ValidationService {

    ResultDTO validateNewUser(UserDTO user);

    ResultDTO validateUserFields(Map<String, String> allParams);

    ResultDTO validateBirthdayRange(String dateFrom, String dateTo);
}
