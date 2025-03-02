package helpers;

import models.request.Property;
import models.response.PropertyResponse;
import org.testng.asserts.SoftAssert;
import utils.UUIDUtils;
import java.util.List;
import java.util.Arrays;

public class AssertionHelper {

    public static void validatePropertyResponse(SoftAssert softAssert, PropertyResponse response, List<String> fields, Property expected) {

        if (fields.contains("id")) {
            softAssert.assertTrue(UUIDUtils.isValidUUID(response.getId()), "Invalid UUID format");
        }
        if (fields.contains("alias")) {
            softAssert.assertEquals(response.getAlias(), expected.getAlias(), "Alias mismatch");
        }
        if (fields.contains("countryCode")) {
            softAssert.assertEquals(response.getCountryCode(), expected.getCountryCode(), "Country code mismatch");
        }
        if (fields.contains("createdAt")) {
            List<Integer> expectedCreatedAt = Arrays.asList(2025, 3, 1, 23, 37, 6, 536538509);
            softAssert.assertEquals(response.getCreatedAt(), expectedCreatedAt, "CreatedAt mismatch");
        }
    }
}
