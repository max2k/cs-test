package com.max2k.cs.service.impl;

import com.max2k.cs.DAO.UserDAO;
import com.max2k.cs.DTO.UserDTO;
import com.max2k.cs.model.User;
import com.max2k.cs.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Instant;
import java.util.*;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDAO userDAO;
    private final ModelMapper modelMapper;


    @Override
    public UserDTO createUser(UserDTO userDTO) throws
            IllegalArgumentException {

        User user=modelMapper.map(userDTO, User.class);

        return modelMapper.map(
                userDAO.createUser(user),
                UserDTO.class
        );

    }

    @Override
    public void updateUser(long l, UserDTO userDTO)  {
        User user=modelMapper.map(userDTO, User.class);
        userDAO.updateUser(l,user);
    }

    @Override
    public void updateUserFields(long l, Map<String, String> allParams) {
        Optional<User> optUser=userDAO.findUserById(l);
        User user2Update=optUser.orElseThrow(() -> new NoSuchElementException("User not found"));

        for (String fieldName: allParams.keySet()) {
            try {
                Method method = user2Update.getClass().getMethod(
                        User.getMethodNames().get(fieldName.toLowerCase(Locale.ROOT))
                        , String.class
                );
                method.invoke(user2Update, allParams.get(fieldName));
            } catch (InvocationTargetException invocationTargetException) {
                throw new IllegalArgumentException("Set method for " + fieldName + " failed,"
                        +invocationTargetException.getMessage(),
                        invocationTargetException);
            }catch (NoSuchMethodException | IllegalAccessException e) {
                throw new IllegalArgumentException("Set method for " + fieldName + " not found");
            }
        }
        userDAO.updateUser(l,user2Update);

    }

    @Override
    public void deleteUserById(long l) {
        if (!userDAO.deleteUser(l)) throw new NoSuchElementException("User not found");

    }

    @Override
    public List<UserDTO> findWithBirthdayBetween(Instant from, Instant to) {

        List<User> inputList=userDAO.findAllUsersWithBirthdayBetween(from,to);

        return inputList
                .stream()
                .map(user -> modelMapper.map(user,UserDTO.class))
                .toList()
                ;
    }
}
