
package com.soundarya.enterpriseexpensemanagementsystem.dto;

import java.time.Instant;
import java.util.Map;

public class ApiErrorResponse {

    private Instant timestamp;
    private int status;
    private String error;
    private String message;
    private Map<String, String> fieldErrors;

    public ApiErrorResponse(
            Instant timestamp,
            int status,
            String error,
            String message,
            Map<String, String> fieldErrors) {

        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.fieldErrors = fieldErrors;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }
}
