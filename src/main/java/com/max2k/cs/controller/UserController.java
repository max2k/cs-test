package com.max2k.cs.controller;

import com.max2k.cs.DTO.ResultDTO;
import com.max2k.cs.DTO.UserDTO;
import com.max2k.cs.service.UserService;
import com.max2k.cs.service.ValidationService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;
    private final ValidationService validationService;

    @PostMapping("/create")
    public ResponseEntity<UserDTO> create(@Validated(UserDTO.New.class) @RequestBody UserDTO user) {
       validateUserDTOifNotThrow(user);
       try{
           var creationResult=userService.createUser(user);
           return ResponseEntity.status(HttpStatus.CREATED).body(creationResult);
       }catch (RuntimeException e){
           throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage());
       }

    }

    private void validateUserDTOifNotThrow(UserDTO user) throws ResponseStatusException {
        var validationResult=validationService.validateNewUser(user);

        if (!validationResult.isValid()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    validationResult.getMessage());
        }
    }

    @PatchMapping("/update/{userId}")
    public ResultDTO update(@Validated(UserDTO.New.class) @RequestBody UserDTO user,
                            @PathVariable @NotNull String userId) {
        validateUserDTOifNotThrow(user);

        try {
            userService.updateUser(Long.parseLong(userId), user);
            ResponseEntity.ok().body(
                    new ResultDTO(true, "User updated successfully")
            );
        }catch (NumberFormatException numberFormatException) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Id is not a number");
        }catch (RuntimeException e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage());
        }

        return new ResultDTO(true,"User updated successfully");
    }

    @PatchMapping("/update_field/{userId}")
    public ResultDTO updateField(@Validated @RequestParam Map<String,String> allParams,
                                 @PathVariable @NotNull String userId) {
        var validationResult=validationService.validateUserFields(allParams);

        if (!validationResult.isValid()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    validationResult.getMessage());

        try {
            userService.updateUserFields(Long.parseLong(userId),allParams);

            return new ResultDTO(true,"User updated successfully");
        }catch (NumberFormatException numberFormatException){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"User id is not a number");
        }catch (RuntimeException e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage());
        }

    }

    @DeleteMapping("/{userId}")
    public ResultDTO delete(@PathVariable @NotBlank String userId) {
        try {
            userService.deleteUserById(Long.parseLong(userId));
            return new ResultDTO(true,"User deleted successfully");

        }catch (NumberFormatException numberFormatException){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"User id is not a number");
        }catch (RuntimeException e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage());
        }

    }

    @GetMapping("/findbybirthday")
    public ResponseEntity<List<UserDTO>> findByBirthday(@RequestParam(name="datefrom")
                                                        @NotNull Instant dateFrom,
                                                        @RequestParam(name="dateto")
                                                        @NotNull Instant dateTo) {
        ResultDTO validation=validationService.validateBirthdayRange(dateFrom,dateTo);
        if (!validation.isValid()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,validation.getMessage());

        try {
            List<UserDTO> usersWithDTO = userService.findWithBirthdayBetween(
                    dateFrom,
                    dateTo
            );

            return ResponseEntity.ok(usersWithDTO);

        }catch (RuntimeException e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage());
        }

    }

}
