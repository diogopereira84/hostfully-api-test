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
public class ValidationArgument {
    private List<String> codes;
    private List<String> arguments;
    private String defaultMessage;
    private String code;
}
