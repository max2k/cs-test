package com.max2k.cs.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResultDTO {
    private boolean valid;
    private String message;
}
