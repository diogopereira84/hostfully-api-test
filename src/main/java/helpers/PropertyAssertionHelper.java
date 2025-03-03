package helpers;

import io.restassured.response.Response;
import models.request.Property;
import models.response.PropertyResponse;
import org.testng.asserts.SoftAssert;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Helper class for asserting API responses and validating test results.
 */
public class PropertyAssertionHelper {

    /**
     * Validates the HTTP status code of a response.
     *
     * @param softAssert Soft assertion object
     * @param response   API response
     * @param expectedStatus Expected HTTP status code
     */
    public static void validateStatusCode(SoftAssert softAssert, Response response, int expectedStatus) {
        softAssert.assertEquals(response.getStatusCode(), expectedStatus, "Unexpected HTTP status code");
    }

    /**
     * Asserts that a list of objects contains unique values based on a specified field extractor.
     *
     * @param softAssert Soft assertion object
     * @param items      List of objects
     * @param extractor  Function to extract the field to check uniqueness
     * @param errorMessage Error message in case of duplicates
     * @param <T>        Type of object in the list
     * @param <R>        Type of field being checked for uniqueness
     */
    public static <T, R> void assertUniqueValues(SoftAssert softAssert, List<T> items, Function<T, R> extractor, String errorMessage) {
        Set<R> uniqueValues = items.stream().map(extractor).collect(Collectors.toSet());
        softAssert.assertEquals(uniqueValues.size(), items.size(), errorMessage);
    }

    /**
     * Validates a PropertyResponse against the expected Property data.
     *
     * @param softAssert       Soft assertion object
     * @param response         The actual API response containing the property
     * @param expectedProperty The expected property values
     */
    public static void validatePropertyResponse(SoftAssert softAssert, PropertyResponse response, Property expectedProperty) {
        softAssert.assertNotNull(response.getId(), "ID should not be null");
        softAssert.assertEquals(response.getAlias(), expectedProperty.getAlias(), "Alias mismatch");
        softAssert.assertEquals(response.getCountryCode(), expectedProperty.getCountryCode(), "Country code mismatch");
    }

    /**
     * Validates a PropertyResponse against the expected fields.
     *
     * @param softAssert       Soft assertion object
     * @param response         The actual API response containing the property
     * @param expectedFields   The expected fields to be present
     * @param expectedProperty The expected property values
     */
    public static void validatePropertyResponse(SoftAssert softAssert, PropertyResponse response, List<String> expectedFields, Property expectedProperty) {
        if (expectedFields.contains("id")) {
            softAssert.assertNotNull(response.getId(), "ID should not be null");
        }
        if (expectedFields.contains("alias")) {
            softAssert.assertEquals(response.getAlias(), expectedProperty.getAlias(), "Alias mismatch");
        }
        if (expectedFields.contains("countryCode")) {
            softAssert.assertEquals(response.getCountryCode(), expectedProperty.getCountryCode(), "Country code mismatch");
        }
        if (expectedFields.contains("createdAt")) {
            softAssert.assertNotNull(response.getCreatedAt(), "CreatedAt should not be null");
        }
    }
}
