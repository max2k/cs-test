package com.max2k.cs.DTO;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class ApiErrorDTO {
    private String code;
    private Instant timestamp;
    private String message;
}
