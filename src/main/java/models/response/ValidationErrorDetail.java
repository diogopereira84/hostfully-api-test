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
public class ValidationErrorDetail {
    private List<String> codes;
    private List<ValidationArgument> arguments;
    private String defaultMessage;
    private String objectName;
    private String field;
    private Object rejectedValue;
    private boolean bindingFailure;
    private String code;
}