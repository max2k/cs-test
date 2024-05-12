package com.max2k.cs.model;

import lombok.Data;
import lombok.Getter;

import java.time.Instant;
import java.util.Map;

@Data
public class User {
    @Getter
    static final Map<String,String> methodNames = Map.of(
            "email", "setEmail",
            "firstname", "setFirstName",
            "lastname", "setLastName",
            "dateofbirth", "setDateOfBirthStr",
            "phonenumber", "setPhoneNumber",
            "address", "setAddress"
    );

    long id;
    String email;
    String firstName;
    String lastName;
    Instant dateOfBirth;
    String phoneNumber;
    String address;

    public void setDateOfBirthStr(final String dateOfBirthStr) {
        this.dateOfBirth = Instant.parse(dateOfBirthStr);
    }


}
