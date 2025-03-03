package tests;

import base.BaseTest;
import helpers.PropertyAssertionHelper;
import helpers.PropertyHelper;
import io.restassured.response.Response;
import models.request.Property;
import models.response.PropertyResponse;
import models.response.ValidationErrorResponse;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import services.AuthRole;
import services.AuthenticationService;

import java.util.List;
import java.util.UUID;

@Listeners(utils.TestListener.class)
public class PropertyTests extends BaseTest {

    @DataProvider(name = "invalidUUIDs")
    public static Object[][] invalidUUIDs() {
        return new Object[][]{
                {"34545"},
                {"4814adee-cd2e-4c70-921d-19b4f0cd527i"}
        };
    }

    @DataProvider(name = "forbiddenRoles")
    public static Object[][] forbiddenRoles() {
        return new Object[][]{{AuthRole.USER_ROLE}};
    }

    @DataProvider(name = "mandatoryFields")
    public static Object[][] mandatoryFields() {
        return new Object[][]{{List.of("alias")}};
    }

    @DataProvider(name = "invalidInputs")
    public static Object[][] invalidInputs() {
        return new Object[][]{
                {"invalid-uuid", "ValidAlias", "BR", "2024-03-02T10:00:00Z"}, // Invalid UUID
                {UUID.randomUUID().toString(), "A", "BR", "2024-03-02T10:00:00Z"}, // Too short alias
                {UUID.randomUUID().toString(), "ValidAlias", "INVALID", "2024-03-02T10:00:00Z"}, // Invalid country code
                {UUID.randomUUID().toString(), "ValidAlias", "BR", "InvalidDate"} // Invalid date
        };
    }

    @Test(groups = {"positive", "regression"})
    public void shouldReturn200WhenGettingAllProperties() {
        Response response = propertyService.propertyRetrievalAll();
        List<PropertyResponse> properties = response.jsonPath().getList(".", PropertyResponse.class);

        SoftAssert softAssert = new SoftAssert();
        PropertyAssertionHelper.validateStatusCode(softAssert, response, 200);
        PropertyAssertionHelper.assertUniqueValues(softAssert, properties, PropertyResponse::getId, "Duplicate IDs found!");
        PropertyAssertionHelper.assertUniqueValues(softAssert, properties, PropertyResponse::getAlias, "Duplicate aliases found!");
        softAssert.assertFalse(properties.isEmpty(), "Property list should not be empty");
        softAssert.assertAll();
    }

    @Test(groups = {"positive", "regression"})
    public void shouldReturn201WhenCreatingValidProperty() {
        Property property = PropertyHelper.createValidProperty();
        Response response = propertyService.propertyCreation(property);
        PropertyResponse propertyResponse = response.as(PropertyResponse.class);

        SoftAssert softAssert = new SoftAssert();
        PropertyAssertionHelper.validateStatusCode(softAssert, response, 201);
        PropertyAssertionHelper.validatePropertyResponse(softAssert, propertyResponse, property);
        softAssert.assertAll();
    }

    @Test(dataProvider = "mandatoryFields", groups = {"positive", "regression"})
    public void shouldReturn201WhenCreatingPropertyWithMandatoryFields(List<String> fields) {
        Property property = PropertyHelper.constructProperty(fields);
        Response response = propertyService.propertyCreation(property);

        Assert.assertEquals(response.getStatusCode(), 201, "Expected 201 Created");

        PropertyResponse propertyResponse = response.as(PropertyResponse.class);
        SoftAssert softAssert = new SoftAssert();
        PropertyAssertionHelper.validatePropertyResponse(softAssert, propertyResponse, fields, property);
        softAssert.assertAll();
    }

    @Test(groups = {"negative", "regression"})
    public void shouldReturn204WhenPropertyNotFound() {
        String propertyId = UUID.randomUUID().toString();
        Response response = propertyService.propertyRetrieval(propertyId);

        SoftAssert softAssert = new SoftAssert();
        PropertyAssertionHelper.validateStatusCode(softAssert, response, 204);
        softAssert.assertAll();
    }

    @Test(groups = {"negative", "regression"})
    public void shouldReturn409WhenCreatingDuplicateProperty() {
        Property property = PropertyHelper.createValidProperty();
        propertyService.propertyCreation(property); // First request to create property
        Response response = propertyService.propertyCreation(property); // Duplicate request

        SoftAssert softAssert = new SoftAssert();
        PropertyAssertionHelper.validateStatusCode(softAssert, response, 409);
        softAssert.assertAll();
    }

    @Test(dataProvider = "forbiddenRoles", groups = {"negative", "security", "regression"})
    public void shouldReturn403WhenUserLacksPermissionToCreateProperty(AuthRole role) {
        AuthenticationService.setAuthRole(role);
        Property property = PropertyHelper.createValidProperty();
        Response response = propertyService.propertyCreation(property);

        SoftAssert softAssert = new SoftAssert();
        PropertyAssertionHelper.validateStatusCode(softAssert, response, 403);
        softAssert.assertAll();
    }

    @Test(dataProvider = "invalidUUIDs", groups = {"negative", "regression"})
    public void shouldReturn400WhenGettingPropertyWithInvalidUUID(String propertyId) {
        Response response = propertyService.propertyRetrieval(propertyId);
        ValidationErrorResponse errorResponse = response.as(ValidationErrorResponse.class);

        SoftAssert softAssert = new SoftAssert();
        PropertyAssertionHelper.validateStatusCode(softAssert, response, 400);
        assertValidationErrorResponse(softAssert, errorResponse, "Bad Request", "Failed to convert 'propertyId' with value: '" + propertyId + "'");
        softAssert.assertAll();
    }

    @Test(dataProvider = "invalidInputs", groups = {"negative", "regression"})
    public void shouldReturn400WhenCreatingPropertyWithInvalidInputs(String id, String alias, String countryCode, String createdAt) {
        Property property = PropertyHelper.createProperty(id, alias, countryCode, createdAt);
        Response response = propertyService.propertyCreation(property);
        Assert.assertEquals(response.getStatusCode(), 400, "Expected 400 BadRequest");
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertAll();
    }

    private void assertValidationErrorResponse(SoftAssert softAssert, ValidationErrorResponse errorResponse, String expectedTitle, String expectedDetail) {
        softAssert.assertEquals(errorResponse.getTitle(), expectedTitle, "Incorrect error title");
        softAssert.assertTrue(errorResponse.getDetail().contains(expectedDetail), "Unexpected error detail");
    }
}