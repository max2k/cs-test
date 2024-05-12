package com.max2k.cs.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    public interface New {}

    public interface Exist {}

    @Null(message = "Id must be null", groups = New.class)
    @NotNull(message = "Id must be not null", groups = Exist.class)
    private Long id;

    @NotBlank(message = "Email cannot be blank", groups = {Exist.class,New.class})
    @Email(message = "Email should be valid", groups = {Exist.class,New.class})
    private String email;

    @NotBlank(message = "first name cannot be blank", groups = {Exist.class,New.class})
    private String firstName;

    @NotBlank
    @NotBlank(message = "last name cannot be blank", groups = {Exist.class,New.class})
    private String lastName;

    @NotNull(message = "Date of birth cannot be blank", groups = {Exist.class,New.class})
    private Instant dateOfBirth;

    private String phoneNumber;

    private String address;
}
