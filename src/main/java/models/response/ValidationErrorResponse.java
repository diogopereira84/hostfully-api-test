package models.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValidationErrorResponse {
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
    private List<ValidationErrorDetail> errors;
}
