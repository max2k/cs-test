package com.max2k.cs.DAO;

import com.max2k.cs.DTO.UserDTO;
import com.max2k.cs.model.User;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface UserDAO {
    User createUser(User user);

    void updateUser(long l, User user);

    Optional<User> findUserById(long l);

    void resetAll();

    boolean deleteUser(long l);

    List<User> findAllUsersWithBirthdayBetween(Instant from, Instant to);
}
