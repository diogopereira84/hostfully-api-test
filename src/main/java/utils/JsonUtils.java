package utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.response.PropertyResponse;

public class JsonUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    // Serialize: Convert Java object to JSON string
    public static String serialize(PropertyResponse propertyResponse) throws JsonProcessingException {
        return objectMapper.writeValueAsString(propertyResponse);
    }

    // Deserialize: Convert JSON string to Java object
    public static PropertyResponse deserialize(String json) throws JsonProcessingException {
        return objectMapper.readValue(json, PropertyResponse.class);
    }
}