package com.max2k.cs.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class NotImplementedException extends RuntimeException{

    public NotImplementedException(String message) {
        super(message);
    }

}
