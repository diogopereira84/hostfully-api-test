package models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {
    private String exception;
    private String path;
    private String error;
    private String message;
    private String timestamp;
    private String type;
    private String title;
    private int status;
    private String detail;
    private String instance;
}
