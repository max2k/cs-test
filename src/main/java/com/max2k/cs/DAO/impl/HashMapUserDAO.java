package com.max2k.cs.DAO.impl;

import com.max2k.cs.DAO.UserDAO;
import com.max2k.cs.model.User;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class HashMapUserDAO implements UserDAO {
    private final Map<Long, User> usersMap= new ConcurrentSkipListMap<>();

    private final AtomicLong lastId =new AtomicLong(0);

    @Override
    public User createUser(User user) {
        long newId= lastId.getAndIncrement();
        user.setId(newId);
        usersMap.put(newId, user);
        return user;
    }

    @Override
    public void updateUser(long l, User newUser) {
        var mapUser = usersMap.get(l);
        if ( mapUser==null ) throw new NoSuchElementException("no such user "+l);
        mapUser.setAddress(newUser.getAddress());
        mapUser.setEmail(newUser.getEmail());
        mapUser.setFirstName(newUser.getFirstName());
        mapUser.setLastName(newUser.getLastName());
        mapUser.setDateOfBirth(newUser.getDateOfBirth());
        mapUser.setPhoneNumber(newUser.getPhoneNumber());
    }

    @Override
    public Optional<User> findUserById(long l) {
        return Optional.ofNullable(usersMap.get(l));
    }

    @Override
    public void resetAll() {
        lastId.set(0);
        usersMap.clear();
    }

    @Override
    public boolean deleteUser(long l) {
        return usersMap.remove(l)!=null;
    }

    @Override
    public List<User> findAllUsersWithBirthdayBetween(Instant from, Instant to) {
        return usersMap.values().stream()
                .filter(user -> user.getDateOfBirth().isAfter(from) && user.getDateOfBirth().isBefore(to))
                .toList();
    }
}
