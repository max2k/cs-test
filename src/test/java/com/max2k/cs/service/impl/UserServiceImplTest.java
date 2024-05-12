package com.max2k.cs.service.impl;

import com.max2k.cs.DAO.UserDAO;
import com.max2k.cs.DTO.UserDTO;
import com.max2k.cs.model.User;
import com.max2k.cs.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Autowired
    UserDAO userDAO;
    @Autowired
    UserService userServiceImpl;

    private UserDTO reqUserDTO;
    private UserDTO changedUserDTO;

    @BeforeEach
    void setUp() {

        reqUserDTO = UserDTO.builder()
                .email("email@email.com")
                .address("testAddress")
                .firstName("test firstname")
                .lastName("test lastname")
                .dateOfBirth(Instant.parse("2000-01-01T00:00:00Z"))
                .phoneNumber("testPhoneNumber")
                .build();

        changedUserDTO = UserDTO.builder()
                .email("changed email@email.com")
                .address("changed testAddress")
                .firstName("changed test firstname")
                .lastName("changed test lastname")
                .dateOfBirth(Instant.parse("2000-01-02T00:00:00Z"))
                .phoneNumber("changed testPhoneNumber")
                .id(1L)
                .build();

        userDAO.resetAll();

    }


    @Test
    void createUserNormal() {

        UserDTO result=userServiceImpl.createUser(reqUserDTO);
        assertNotNull(result);
        assertEquals(reqUserDTO.getEmail(), result.getEmail());
        assertEquals(reqUserDTO.getAddress(), result.getAddress());
        assertEquals(reqUserDTO.getFirstName(), result.getFirstName());
        assertEquals(reqUserDTO.getLastName(), result.getLastName());
        assertEquals(reqUserDTO.getDateOfBirth(), result.getDateOfBirth());
        assertEquals(reqUserDTO.getPhoneNumber(), result.getPhoneNumber());
        assertEquals(0L,result.getId() );

        result=userServiceImpl.createUser(reqUserDTO);
        assertNotNull(result);
        assertEquals(reqUserDTO.getEmail(), result.getEmail());
        assertEquals(reqUserDTO.getAddress(), result.getAddress());
        assertEquals(reqUserDTO.getFirstName(), result.getFirstName());
        assertEquals(reqUserDTO.getLastName(), result.getLastName());
        assertEquals(reqUserDTO.getDateOfBirth(), result.getDateOfBirth());
        assertEquals(reqUserDTO.getPhoneNumber(), result.getPhoneNumber());
        assertEquals(1L,result.getId() );

    }

    @Test
    void updateUserNormal() {
        UserDTO createdUser=userServiceImpl.createUser(reqUserDTO);

        userServiceImpl.updateUser(createdUser.getId(),changedUserDTO);

        Optional<User> changedUser=userDAO.findUserById(createdUser.getId());
        assertTrue(changedUser.isPresent());
        changedUser.ifPresent(user -> {
                assertEquals(changedUserDTO.getEmail(), user.getEmail());
                assertEquals(changedUserDTO.getAddress(), user.getAddress());
                assertEquals(changedUserDTO.getFirstName(), user.getFirstName());
                assertEquals(changedUserDTO.getLastName(), user.getLastName());
                assertEquals(changedUserDTO.getDateOfBirth(), user.getDateOfBirth());
                assertEquals(changedUserDTO.getPhoneNumber(), user.getPhoneNumber());
        });
    }

    @Test
    void updateUserWrongId(){
        assertThrows(NoSuchElementException.class, () -> userServiceImpl.updateUser(20,changedUserDTO)
        );
    }

    @Test
    void updateUserFieldsNormal() {
        UserDTO createdUser=userServiceImpl.createUser(reqUserDTO);

        userServiceImpl.updateUserFields(0, Map.of("firstname","changed firstname"
                ,"lastname","changed lastname"
                ,"dateofbirth","2000-01-02T00:00:00Z"
                ) );
        Optional<User> changedUser=userDAO.findUserById(createdUser.getId());

        assertTrue(changedUser.isPresent());

        changedUser.ifPresent(user -> {
            assertEquals("changed firstname", user.getFirstName() );
            assertEquals("changed lastname", user.getLastName() );
            assertEquals(Instant.parse("2000-01-02T00:00:00Z"), user.getDateOfBirth() );
            assertEquals(createdUser.getAddress(), user.getAddress());
            assertEquals(createdUser.getPhoneNumber(), user.getPhoneNumber());
            assertEquals(createdUser.getEmail(), user.getEmail() );
        });
    }

    @Test
    void updateUserFieldsNormalCaseInsensitive() {
        UserDTO createdUser=userServiceImpl.createUser(reqUserDTO);

        userServiceImpl.updateUserFields(0, Map.of("Firstname","changed firstname"
                ,"lastName","changed lastname"
                ,"datEofbirth","2000-01-02T00:00:00Z"
        ) );
        Optional<User> changedUser=userDAO.findUserById(createdUser.getId());

        assertTrue(changedUser.isPresent());

        changedUser.ifPresent(user -> {
            assertEquals("changed firstname", user.getFirstName() );
            assertEquals("changed lastname", user.getLastName() );
            assertEquals(Instant.parse("2000-01-02T00:00:00Z"), user.getDateOfBirth() );
            assertEquals(createdUser.getAddress(), user.getAddress());
            assertEquals(createdUser.getPhoneNumber(), user.getPhoneNumber());
            assertEquals(createdUser.getEmail(), user.getEmail() );
        });
    }

    @Test
    void updateUserFieldsNormalBadDate() {
        UserDTO createdUser=userServiceImpl.createUser(reqUserDTO);

        assertThrows(IllegalArgumentException.class,() ->
        userServiceImpl.updateUserFields(createdUser.getId(), Map.of("firstname","changed firstname"
                ,"lastname","changed lastname"
                ,"dateofbirth","bad date"
        ) )
        );


    }

    @Test
    void updateUserFieldsWrongId() {

        assertThrows(NoSuchElementException.class,() ->
                userServiceImpl.updateUserFields(-1, Map.of("firstname","changed firstname"
                        ,"lastname","changed lastname"

                ) )
        );
    }

    @Test
    void deleteUserWrongId() {
        assertThrows(NoSuchElementException.class,() ->
                userServiceImpl.deleteUserById(1) );
    }

    @Test
    void deleteUserNormal() {
        UserDTO createdUser=userServiceImpl.createUser(reqUserDTO);
        userServiceImpl.deleteUserById(createdUser.getId());
    }

    @Test
    void findWithBirthdayNormal() {
        IntStream.range(1980,2000)
                .forEach(i -> {
                    reqUserDTO.setFirstName("firstname:" + i);
                    reqUserDTO.setLastName("lastname:" + i);
                    reqUserDTO.setDateOfBirth(Instant.parse(i+"-01-02T00:00:00Z"));
                    userServiceImpl.createUser(reqUserDTO);
                });

        //range inside the birthday set
        List<UserDTO> userWithBirthDay=userServiceImpl
                .findWithBirthdayBetween(Instant.parse("1981-01-01T00:00:00Z")
                ,Instant.parse("1985-01-01T00:00:00Z")
                );
        assertEquals(4,userWithBirthDay.size());
        assertEquals("firstname:1981",userWithBirthDay.get(0).getFirstName());

        //range overlap over the birthday set
        userWithBirthDay=userServiceImpl
                .findWithBirthdayBetween(Instant.parse("1881-01-01T00:00:00Z")
                        ,Instant.parse("1985-01-01T00:00:00Z")
                );
        assertEquals(5,userWithBirthDay.size());
        assertEquals("firstname:1980",userWithBirthDay.get(0).getFirstName());

        //range out of the birthday set
        userWithBirthDay=userServiceImpl
                .findWithBirthdayBetween(Instant.parse("1881-01-01T00:00:00Z")
                        ,Instant.parse("1975-01-01T00:00:00Z")
                );
        assertEquals(0,userWithBirthDay.size());
        //assertEquals("firstname:1980",userWithBirthDay.get(0).getFirstName());
    }
}