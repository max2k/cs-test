package com.max2k.cs.service;

import com.max2k.cs.DTO.UserDTO;
import com.max2k.cs.exception.NotImplementedException;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public interface UserService {

    UserDTO createUser(UserDTO userDTO) throws NotImplementedException;

    void updateUser(long l, UserDTO userDTO) throws NotImplementedException;

    void updateUserFields(long l, Map<String, String> allParams) throws NotImplementedException;

    void deleteUserById(long l);

    List<UserDTO> findWithBirthdayBetween(Instant from, Instant to);
}
