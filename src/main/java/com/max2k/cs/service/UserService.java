package com.max2k.cs.service;

import com.max2k.cs.DTO.UserDTO;
import com.max2k.cs.exception.NotImplementedException;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public interface UserService {

    UserDTO createUser(UserDTO userDTO) ;

    void updateUser(long l, UserDTO userDTO) ;

    void updateUserFields(long l, Map<String, String> allParams) ;

    void deleteUserById(long l);

    List<UserDTO> findWithBirthdayBetween(Instant from, Instant to);
}
