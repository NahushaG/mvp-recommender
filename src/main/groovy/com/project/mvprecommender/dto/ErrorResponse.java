package com.project.mvprecommender.dto;

import lombok.*;

// Standardized error response
@Data
@ToString
@NoArgsConstructor
public class ErrorResponse {
    public String code;
    public Object message;
    public ErrorResponse(String code, Object message) {
        this.code = code;
        this.message = message;
    }
}