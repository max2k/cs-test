package com.max2k.cs.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.max2k.cs.DTO.ResultDTO;
import com.max2k.cs.DTO.UserDTO;
import com.max2k.cs.exception.NotImplementedException;
import com.max2k.cs.service.UserService;
import com.max2k.cs.service.ValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import java.time.Instant;
import java.util.List;
import java.util.function.Consumer;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class UserControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private UserService userService;

    @MockBean
    private ValidationService validationService;

    private MockMvc mockMvc;
    private UserDTO reqUserDTO;
    private UserDTO answerUserDTO;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        reqUserDTO = getNewUserDTO();

        answerUserDTO = UserDTO.builder()
                .email("email@email.com")
                .address("testAddress")
                .firstName("test firstname")
                .lastName("test lastname")
                .dateOfBirth(Instant.parse("2000-01-01T00:00:00Z"))
                .phoneNumber("testPhoneNumber")
                .id(1L)
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

    }

    private static UserDTO getNewUserDTO() {
        return UserDTO.builder()
                .email("email@email.com")
                .address("testAddress")
                .firstName("test firstname")
                .lastName("test lastname")
                .dateOfBirth(Instant.parse("2000-01-01T00:00:00Z"))
                .phoneNumber("testPhoneNumber")
                .build();
    }

    @Test
    void createUserDTONotValidated() throws Exception {
        when(validationService.validateNewUser(reqUserDTO))
                .thenReturn(new ResultDTO(false,"new user validation failed"));

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/user/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(reqUserDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("new user validation failed"))
                .andReturn();
    }

    @Test
    void createUserDTOBadInput() throws Exception {
        UserDTO testUserDTO = getNewUserDTO();

        when(validationService.validateNewUser(testUserDTO))
                .thenReturn(new ResultDTO(true,"User validated successfully"));

        testUserDTO.setId(null);
        testValueOver(testUserDTO, new String[]{null,""},testUserDTO::setEmail,"Email cannot be blank");
        testValueOver(testUserDTO, new String[]{"bad email"},testUserDTO::setEmail,"Email should be valid");
        testUserDTO.setEmail("test@email.com");

        testValueOver(testUserDTO, new String[]{null,""},testUserDTO::setFirstName,"first name cannot be blank");
        testUserDTO.setFirstName("test");
        testValueOver(testUserDTO, new String[]{null,""},testUserDTO::setLastName,
                "last name cannot be blank");

    }

    private void testValueOver(UserDTO userDTO, String[] testValues,
                               Consumer<String> setMethod, String message) throws Exception {
        for(String testValue:testValues){
            setMethod.accept(testValue);
            doCreateBadRequestWithMessage(userDTO,message);
        }
    }

    private MvcResult doCreateBadRequestWithMessage(UserDTO userDTO, String message) throws Exception {
        return mockMvc.perform(
                        MockMvcRequestBuilders.post("/user/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(message))
                .andReturn();
    }

    @Test
    void createUserError() throws Exception{

        when(validationService.validateNewUser(reqUserDTO))
                .thenReturn(new ResultDTO(true,"User validated successfully"));

        when(userService.createUser(reqUserDTO))
                .thenThrow(new NotImplementedException("Not implemented"));

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/user/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(reqUserDTO)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Not implemented"))
                .andReturn();


    }

    @Test
    void createUserNormal() throws Exception {

        when(validationService.validateNewUser(reqUserDTO))
                .thenReturn(new ResultDTO(true,"User validated successfully"));

        when(userService.createUser(reqUserDTO))
                .thenReturn(answerUserDTO);

        MvcResult normalResult= mockMvc.perform(
                MockMvcRequestBuilders.post("/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqUserDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("email@email.com"))
                .andExpect(jsonPath("$.address").value("testAddress"))
                .andExpect(jsonPath("$.firstName").value("test firstname"))
                .andExpect(jsonPath("$.lastName").value("test lastname"))
                .andExpect(jsonPath("$.dateOfBirth").value("2000-01-01T00:00:00Z"))
                .andExpect(jsonPath("$.phoneNumber").value("testPhoneNumber"))
                .andReturn();

    }

    @Test
    void updateUserNormal() throws Exception {
        when(validationService.validateNewUser(reqUserDTO))
                .thenReturn(new ResultDTO(true,"User validated successfully"));

        MvcResult normalResult= mockMvc.perform(
                        MockMvcRequestBuilders.patch("/user/update/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(reqUserDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value("true"))
                .andExpect(jsonPath("$.message").value("User updated successfully"))
                .andReturn();

    }

    @Test
    void updateUserValidationFail() throws Exception {

        when(validationService.validateNewUser(reqUserDTO))
                .thenReturn(new ResultDTO(false,"Validation failed"));

        MvcResult normalResult= mockMvc.perform(
                        MockMvcRequestBuilders.patch("/user/update/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(reqUserDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andReturn();

    }

    @Test
    void updateUserIncorrectID() throws Exception {
        when(validationService.validateNewUser(reqUserDTO))
                .thenReturn(new ResultDTO(true,"Validation passed"));

        MvcResult normalResult= mockMvc.perform(
                        MockMvcRequestBuilders.patch("/user/update/t1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(reqUserDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Id is not a number"))
                .andReturn();

    }

    @Test
    void updateUserUpdateFail() throws Exception {
        when(validationService.validateNewUser(reqUserDTO))
                .thenReturn(new ResultDTO(true,"Validation passed"));

        doThrow(new RuntimeException("Unexpected exception")).when(userService)
                .updateUser(1,reqUserDTO);


        MvcResult normalResult= mockMvc.perform(
                        MockMvcRequestBuilders.patch("/user/update/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(reqUserDTO)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Unexpected exception"))
                .andReturn();

    }

    @Test
    void updateUserFieldsNormal() throws Exception {
        when(validationService.validateUserFields(any()))
                .thenReturn(new ResultDTO(true,"Validation passed"));

        MultiValueMap<String,String> testParams= getUpdateFieldTestParams();

        mockMvc.perform(
                        MockMvcRequestBuilders.patch("/user/update_field/1")
                                .params(testParams)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(reqUserDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User updated successfully"))
                .andReturn();

        verify(userService, times(1)).updateUserFields(eq(1L),
                eq(testParams.toSingleValueMap()));
    }

    private MultiValueMap<String,String> getUpdateFieldTestParams(){
        var testParams= new LinkedMultiValueMap<String, String>();
        testParams.add("firstName","testFirstName");
        testParams.add("lastName","testLastName");
        testParams.add("address","changed test Address");
        return testParams;
    }

    @Test
    void updateUserFieldsValidationFail() throws Exception {
        when(validationService.validateUserFields(any()))
                .thenReturn(new ResultDTO(false,"Validation failed"));

        MultiValueMap<String,String> testParams= getUpdateFieldTestParams();


        mockMvc.perform(
                        MockMvcRequestBuilders.patch("/user/update_field/1")
                                .params(testParams)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(reqUserDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andReturn();
    }

    @Test
    void updateUserFieldsIncorrectID() throws Exception {
        when(validationService.validateUserFields(any()))
                .thenReturn(new ResultDTO(true,"Validation passed"));

        mockMvc.perform(
                        MockMvcRequestBuilders.patch("/user/update_field/t1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(reqUserDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("User id is not a number"))
                .andReturn();
    }


    @Test
    void updateUserFieldsUpdateException() throws Exception {
        when(validationService.validateUserFields(anyMap()))
                .thenReturn(new ResultDTO(true,"Validation passed"));

        doThrow(new RuntimeException("Unexpected exception"))
                .when(userService)
                .updateUserFields(eq(1L),anyMap());

        mockMvc.perform(
                        MockMvcRequestBuilders.patch("/user/update_field/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .params(getUpdateFieldTestParams())
                                .content(objectMapper.writeValueAsString(reqUserDTO)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Unexpected exception"))
                .andReturn();

    }

    @Test
    void updateUserFieldsBlankID() throws Exception {

        mockMvc.perform(
                        MockMvcRequestBuilders.patch("/user/update_field")
                                .contentType(MediaType.APPLICATION_JSON)
                                .params(getUpdateFieldTestParams())
                )
                .andExpect(status().isMethodNotAllowed())
                .andReturn();

    }

    @Test
    void findByBirthdayNormal() throws Exception{
        when(validationService.validateBirthdayRange(any(),any()))
                .thenReturn(new ResultDTO(true,"Validation passed"));

        when(userService.findWithBirthdayBetween(any(),any()))
                .thenReturn(List.of(answerUserDTO));

        var result=mockMvc.perform(
                        MockMvcRequestBuilders.get("/user/findbybirthday")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("datefrom","1980-01-01T00:00:00Z")
                                .param("dateto","1990-01-01T00:00:00Z")
                            )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].email").value("email@email.com"))
                .andExpect(jsonPath("$[0].address").value("testAddress"))
                .andExpect(jsonPath("$[0].firstName").value("test firstname"))
                .andExpect(jsonPath("$[0].lastName").value("test lastname"))
                .andExpect(jsonPath("$[0].dateOfBirth").value("2000-01-01T00:00:00Z"))
                .andExpect(jsonPath("$[0].phoneNumber").value("testPhoneNumber"))
                .andReturn();
        System.out.println( result.getResponse() );
    }

    @Test
    void findByBirthdateWrongParam() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/user/findbybirthday")
                                .contentType(MediaType.APPLICATION_JSON)
                                )
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    void findByBirthdateValidationFail() throws Exception {
        when(validationService.validateBirthdayRange(any(),any()))
                .thenReturn(new ResultDTO(false,"Validation failed"));

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/user/findbybirthday")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("datefrom","1980-01-01T00:00:00Z")
                                .param("dateto","1990-01-01T00:00:00Z")
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andReturn();
    }

    @Test
    void findByBirthdateUserServiceFail() throws Exception {
        when(validationService.validateBirthdayRange(any(),any()))
                .thenReturn(new ResultDTO(true,"Validation passed"));

        doThrow(new RuntimeException("Unexpected exception"))
                .when(userService)
                .findWithBirthdayBetween(any(),any());

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/user/findbybirthday")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("datefrom","1980-01-01T00:00:00Z")
                                .param("dateto","1990-01-01T00:00:00Z")
                )
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Unexpected exception"))
                .andReturn();
    }

    @Test
    void deleteUserNormal() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/user/1")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User deleted successfully"))
                .andReturn();

    }

    @Test
    void deleteUserWrongID() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/user/T1")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("User id is not a number"))
                .andReturn();

    }
}