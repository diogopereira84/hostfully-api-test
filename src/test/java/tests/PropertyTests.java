package tests;

import base.BaseTest;
import helpers.AssertionHelper;
import helpers.PropertyHelper;
import io.restassured.response.Response;
import models.response.PropertyResponse;
import models.response.ValidationErrorDetail;
import models.response.ValidationErrorResponse;
import models.request.Property;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import services.AuthRole;
import services.AuthenticationService;
import utils.DateTimeUtils;
import utils.UUIDUtils;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Listeners(utils.TestListener.class)
public class PropertyTests extends BaseTest {

    private SoftAssert softAssert; // Declare SoftAssert globally for each test

    /**
     * Initializes SoftAssert before each test.
     */
    private void initSoftAssert() {
        softAssert = new SoftAssert();
    }

    @DataProvider(name = "invalidUUIDs")
    public static Object[][] invalidUUIDs() {
        return new Object[][]{
                {"34545"},
                {"4814adee-cd2e-4c70-921d-19b4f0cd527i"}
        };
    }

    @DataProvider(name = "grantedRoles")
    public static Object[][] grantedRoles() {
        return new Object[][]{
                {AuthRole.ADMIN_ROLE}
        };
    }

    @DataProvider(name = "forbiddenRoles")
    public static Object[][] forbiddenRoles() {
        return new Object[][]{
                {AuthRole.USER_ROLE}
        };
    }

    @DataProvider(name = "mandatoryFields")
    public static Object[][] mandatoryFields() {
        return new Object[][]{
                {List.of("alias")}
        };
    }

    @DataProvider(name = "invalidInputs")
    public static Object[][] invalidInputs() {
        return new Object[][]{
                {"invalid-uuid", "ValidAlias", "BR", "2024-03-02T10:00:00Z"}, // Invalid UUID format
                {UUID.randomUUID().toString(), "A", "BR", "2024-03-02T10:00:00Z"}, // Too short Alias
                {UUID.randomUUID().toString(), "ValidAlias", "INVALID", "2024-03-02T10:00:00Z"}, // Invalid Country Code
                {UUID.randomUUID().toString(), "ValidAlias", "BR", "InvalidDate"}, // Invalid Date Format
        };
    }

    @DataProvider(name = "optionalFields")
    public static Object[][] optionalFields() {
        return new Object[][]{
                {List.of()},
                {List.of("id")},
                {List.of("countryCode")},
                {List.of("createdAt")},
                {List.of("id", "countryCode")},
                {List.of("id", "createdAt")},
                {List.of("countryCode", "createdAt")},
                {List.of("id", "countryCode", "createdAt")},
        };
    }

    @Test(groups = {"positive", "regression"})
    public void shouldReturn200WhenGettingPropertyRetrievalAllTest() {
        initSoftAssert();

        Response response = propertyService.propertyRetrievalAll();
        List<PropertyResponse> propertyResponses = response.as(new io.restassured.common.mapper.TypeRef<List<PropertyResponse>>() {});

        softAssert.assertTrue(!propertyResponses.isEmpty());
        softAssert.assertEquals(response.getStatusCode(), 200, "Expected 200 OK");
        softAssert.assertAll();
    }

    @Test(groups = {"positive", "regression"})
    public void shouldReturn200WhenGettingASinglePropertyRetrievalTest() {
        initSoftAssert();

        String propertyId = "4814adee-cd2e-4c70-921d-19b4f0cd527d";
        String expectedAlias = "Property for American";
        Response response = propertyService.propertyRetrieval(propertyId);
        PropertyResponse propertyResponse = response.as(PropertyResponse.class);

        // Assert HTTP status
        softAssert.assertEquals(response.getStatusCode(), 200, "Expected 200 OK");
        softAssert.assertNotNull(propertyResponse.getId(), "Id should not be null");
        softAssert.assertEquals(propertyResponse.getAlias(), expectedAlias, "Alias mismatch");
        softAssert.assertNull(propertyResponse.getCountryCode(), "CountryCode should be null");

        // Assert CreatedAt field (Date-Time Array)
        List<Integer> expectedCreatedAt = List.of(2025, 2, 26, 9, 2, 42, 214054000);
        softAssert.assertEquals(propertyResponse.getCreatedAt(), expectedCreatedAt, "CreatedAt mismatch");

        // Collect all assertion failures at the end
        softAssert.assertAll();
    }

    @Test(groups = {"positive", "regression"})
    public void shouldReturn201WhenCreatingValidPropertyTest() {
        initSoftAssert();

        String expectedUUID = UUID.randomUUID().toString();
        String expectedAlias = "Property for Hostfully: " + expectedUUID;
        String dateTime = DateTimeUtils.getCurrentUtcTimestamp();
        String expectedCountryCode = "BR";

        Property property = Property.builder()
                .id(expectedUUID)
                .alias(expectedAlias)
                .countryCode(expectedCountryCode)
                .createdAt(dateTime)
                .build();

        Response response = propertyService.propertyCreation(property);
        PropertyResponse propertyResponse = response.as(PropertyResponse.class);

        // Assert HTTP status
        softAssert.assertEquals(response.getStatusCode(), 201, "Expected 201 Created");

        // Validate UUID format
        softAssert.assertTrue(UUIDUtils.isValidUUID(propertyResponse.getId()), "ID is not a valid UUID");

        // Assert response fields
        softAssert.assertEquals(propertyResponse.getAlias(), expectedAlias, "Alias mismatch");
        softAssert.assertEquals(propertyResponse.getCountryCode(), expectedCountryCode, "CountryCode mismatch");

        // Assert createdAt array values
        List<Integer> expectedCreatedAt = Arrays.asList(2025, 3, 1, 23, 37, 6, 536538509);
        softAssert.assertEquals(propertyResponse.getCreatedAt(), expectedCreatedAt, "CreatedAt mismatch");

        // Collect all assertion failures at the end
        softAssert.assertAll();
    }

    @Test(dataProvider = "mandatoryFields", groups = {"positive", "regression"})
    public void shouldReturn201WhenCreatingValidPropertyWithMandatoryFieldsTest(List<String> fields) {
        SoftAssert softAssert = new SoftAssert();

        Property property = PropertyHelper.constructProperty(fields);
        Response response = propertyService.propertyCreation(property);
        PropertyResponse propertyResponse = response.as(PropertyResponse.class);

        AssertionHelper.validatePropertyResponse(softAssert, propertyResponse, fields, property);

        softAssert.assertAll();
    }

    @Test(groups = {"negative", "regression"})
    public void shouldReturn204WhenPropertyRetrievalNotFoundTest() {
        initSoftAssert();

        String propertyId = UUID.randomUUID().toString();
        Response response = propertyService.propertyRetrieval(propertyId);
        softAssert.assertEquals(response.getStatusCode(), 204, "Expected 204 No Content");

        softAssert.assertAll();
    }

    @Test(groups = {"negative", "regression"})
    public void shouldReturn409WhenPostingADuplicatePropertyCreationTest(){
        initSoftAssert();

        String expectedUUID = UUID.randomUUID().toString();
        String expectedAlias = "Property for American";
        String dateTime = DateTimeUtils.getCurrentUtcTimestamp();
        String expectedCountryCode = "BR";

        Property property = Property.builder()
                .id(expectedUUID)
                .alias(expectedAlias)
                .countryCode(expectedCountryCode)
                .createdAt(dateTime)
                .build();

        Response response = propertyService.propertyCreation(property);

        softAssert.assertEquals(response.getStatusCode(), 409, "Expected 409 A property with this alias already exists.");
        softAssert.assertAll();
    }

    @Test(dataProvider = "forbiddenRoles", groups = {"negative", "security", "regression"})
    public void shouldReturn403WhenUserLacksPermissionToAccessPropertyCreationTest(AuthRole role){
        initSoftAssert();

        AuthenticationService.setAuthRole(role);

        String expectedUUID = UUID.randomUUID().toString();
        String expectedAlias = "Property for Hostfully: " + expectedUUID;
        String dateTime = DateTimeUtils.getCurrentUtcTimestamp();
        String expectedCountryCode = "BR";

        Property property = Property.builder()
                .id(expectedUUID)
                .alias(expectedAlias)
                .countryCode(expectedCountryCode)
                .createdAt(dateTime)
                .build();

        Response response = propertyService.propertyCreation(property);

        softAssert.assertEquals(response.getStatusCode(), 403, "Expected 403 Forbidden");
        softAssert.assertAll();
    }

    @Test(dataProvider = "optionalFields", groups = {"negative", "regression"})
    public void shouldReturn400WhenCreatingPropertyWithOptionalFieldsTest(List<String> fields) {
        initSoftAssert();

        Property property = PropertyHelper.constructProperty(fields);
        Response response = propertyService.propertyCreation(property);

        ValidationErrorResponse errorResponse = response.as(ValidationErrorResponse.class);

        // Assert HTTP status
        softAssert.assertEquals(response.getStatusCode(), 400, "Expected 400 Bad Request");

        // Assert response fields
        softAssert.assertEquals(errorResponse.getType(), "https://www.hostfully.com/problems/validation-error", "Type mismatch");
        softAssert.assertEquals(errorResponse.getTitle(), "Validation Error", "Title mismatch");
        softAssert.assertEquals(errorResponse.getStatus(), 400, "Status mismatch");
        softAssert.assertEquals(errorResponse.getDetail(), "Validation failed", "Detail mismatch");
        softAssert.assertEquals(errorResponse.getInstance(), "/properties", "Instance mismatch");

        // Assert validation error details
        ValidationErrorDetail errorDetail = errorResponse.getErrors().get(0);
        softAssert.assertEquals(errorDetail.getCodes(), List.of(
                "NotNull.propertyDTO.alias",
                "NotNull.alias",
                "NotNull.java.lang.String",
                "NotNull"
        ), "Error codes mismatch");

        softAssert.assertEquals(errorDetail.getArguments().get(0).getCodes(), List.of("propertyDTO.alias", "alias"), "Arguments codes mismatch");
        softAssert.assertNull(errorDetail.getRejectedValue(), "Rejected value should be null");
        softAssert.assertEquals(errorDetail.getDefaultMessage(), "Property alias is required", "Default message mismatch");
        softAssert.assertEquals(errorDetail.getObjectName(), "propertyDTO", "Object name mismatch");
        softAssert.assertEquals(errorDetail.getField(), "alias", "Field mismatch");
        softAssert.assertFalse(errorDetail.isBindingFailure(), "Binding failure should be false");
        softAssert.assertEquals(errorDetail.getCode(), "NotNull", "Error code mismatch");

        // Collect all assertion failures at the end
        softAssert.assertAll();
    }

    @Test(dataProvider = "invalidUUIDs", groups = {"negative", "regression"})
    public void shouldReturn400WhenGettingPropertyRetrievalWithInvalidUUIDTest(String propertyId) {
        initSoftAssert();

        Response response = propertyService.propertyRetrieval(propertyId);
        softAssert.assertEquals(response.getStatusCode(), 400, "Expected 400 Bad Request");

        ValidationErrorResponse validationErrorResponse = response.as(ValidationErrorResponse.class);
        softAssert.assertEquals(validationErrorResponse.getType(), "about:blank");
        softAssert.assertEquals(validationErrorResponse.getTitle(), "Bad Request");
        softAssert.assertEquals(validationErrorResponse.getStatus(), 400);
        softAssert.assertEquals(validationErrorResponse.getDetail(), "Failed to convert 'propertyId' with value: '" + propertyId + "'");
        softAssert.assertEquals(validationErrorResponse.getInstance(), "/properties/" + propertyId);

        softAssert.assertAll();
    }

    @Test(dataProvider = "invalidInputs", groups = {"negative", "regression"})
    public void shouldReturn400WhenCreatingPropertyWithInvalidInputsTest(String id, String alias, String countryCode, String createdAt) {
        initSoftAssert();

        Property property = Property.builder()
                .id(id.isEmpty() ? null : id) // Simulating missing field
                .alias(alias.isEmpty() ? null : alias)
                .countryCode(countryCode.isEmpty() ? null : countryCode)
                .createdAt(createdAt.isEmpty() ? null : createdAt)
                .build();

        Response response = propertyService.propertyCreation(property);
        ValidationErrorResponse validationErrorResponse = response.as(ValidationErrorResponse.class);

        softAssert.assertEquals(response.getStatusCode(), 400, "Expected 400 Bad Request");
        softAssert.assertEquals(validationErrorResponse.getTitle(), "Bad Request", "Incorrect error title");

        softAssert.assertAll();
    }


    @Test(dataProvider = "forbiddenRoles", groups = {"negative", "security", "regression"})
    public void shouldReturn403WhenUserLacksPermissionToAccessPropertyRetrievalTest(AuthRole role){
        initSoftAssert();

        AuthenticationService.setAuthRole(role);
        String propertyId = "4814adee-cd2e-4c70-921d-19b4f0cd527d";

        Response response = propertyService.propertyRetrieval(propertyId);

        softAssert.assertEquals(response.getStatusCode(), 403, "Expected 403 Forbidden");
        softAssert.assertAll();
    }

    @Test(dataProvider = "forbiddenRoles", groups = {"negative", "security", "regression"})
    public void shouldReturn403WhenUserLacksPermissionToAccessPropertyRetrievalAllTest(AuthRole role){
        initSoftAssert();

        AuthenticationService.setAuthRole(role);
        Response response = propertyService.propertyRetrievalAll();

        softAssert.assertEquals(response.getStatusCode(), 403, "Expected 403 Forbidden");
        softAssert.assertAll();
    }

    @Test(groups = {"negative", "security", "regression"})
    public void shouldReturn401WhenGettingPropertyRetrievalWithNoCredentialsTest() {
        initSoftAssert();

        AuthenticationService.removeAuth();
        String propertyId = "4814adee-cd2e-4c70-921d-19b4f0cd527d";

        Response response = propertyService.propertyRetrieval(propertyId);
        ValidationErrorResponse validationErrorResponse = response.as(ValidationErrorResponse.class);

        softAssert.assertEquals(response.getStatusCode(), 401, "Expected 401 Unauthorized");
        softAssert.assertEquals(validationErrorResponse.getException(), "Full authentication is required to access this resource");
        softAssert.assertEquals(validationErrorResponse.getPath(), "/properties/" + propertyId);
        softAssert.assertEquals(validationErrorResponse.getError(), "Unauthorized");
        softAssert.assertEquals(validationErrorResponse.getMessage(), "Error while authenticating your access");
        softAssert.assertNotNull(validationErrorResponse.getTimestamp());

        softAssert.assertAll();
    }

    @Test(groups = {"negative", "security", "regression"})
    public void shouldReturn401WhenGettingPropertyRetrievalAllWithNoCredentialsTest() {
        initSoftAssert();

        AuthenticationService.removeAuth();

        Response response = propertyService.propertyRetrievalAll();
        ValidationErrorResponse validationErrorResponse = response.as(ValidationErrorResponse.class);

        softAssert.assertEquals(response.getStatusCode(), 401, "Expected 401 Unauthorized");
        softAssert.assertEquals(validationErrorResponse.getException(), "Full authentication is required to access this resource");
        softAssert.assertEquals(validationErrorResponse.getPath(), "/properties");
        softAssert.assertEquals(validationErrorResponse.getError(), "Unauthorized");
        softAssert.assertEquals(validationErrorResponse.getMessage(), "Error while authenticating your access");
        softAssert.assertNotNull(validationErrorResponse.getTimestamp());

        softAssert.assertAll();
    }

    @Test(groups = {"negative", "security", "regression"})
    public void shouldReturn401WhenGettingPropertyCreationWithNoCredentialsTest() {
        initSoftAssert();

        AuthenticationService.removeAuth();

        String expectedUUID = UUID.randomUUID().toString();
        String expectedAlias = "Property for Hostfully: " + expectedUUID;
        String dateTime = DateTimeUtils.getCurrentUtcTimestamp();
        String expectedCountryCode = "BR";

        Property property = Property.builder()
                .id(expectedUUID)
                .alias(expectedAlias)
                .countryCode(expectedCountryCode)
                .createdAt(dateTime)
                .build();

        Response response = propertyService.propertyCreation(property);
        ValidationErrorResponse validationErrorResponse = response.as(ValidationErrorResponse.class);

        softAssert.assertEquals(response.getStatusCode(), 401, "Expected 401 Unauthorized");
        softAssert.assertEquals(validationErrorResponse.getException(), "Full authentication is required to access this resource");
        softAssert.assertEquals(validationErrorResponse.getPath(), "/properties");
        softAssert.assertEquals(validationErrorResponse.getError(), "Unauthorized");
        softAssert.assertEquals(validationErrorResponse.getMessage(), "Error while authenticating your access");
        softAssert.assertNotNull(validationErrorResponse.getTimestamp());

        softAssert.assertAll();
    }

    @Test(groups = {"negative", "security", "regression"})
    public void shouldReturn401WhenGettingPropertyCreationWithInvalidCredentialsTest() {
        initSoftAssert();

        AuthenticationService.setInvalidAuth();

        String expectedUUID = UUID.randomUUID().toString();
        String expectedAlias = "Property for Hostfully: " + expectedUUID;
        String dateTime = DateTimeUtils.getCurrentUtcTimestamp();
        String expectedCountryCode = "BR";

        Property property = Property.builder()
                .id(expectedUUID)
                .alias(expectedAlias)
                .countryCode(expectedCountryCode)
                .createdAt(dateTime)
                .build();

        Response response = propertyService.propertyCreation(property);

        ValidationErrorResponse validationErrorResponse = response.as(ValidationErrorResponse.class);

        softAssert.assertEquals(response.getStatusCode(), 401, "Expected 401 Unauthorized");
        softAssert.assertEquals(validationErrorResponse.getException(), "Bad credentials");
        softAssert.assertEquals(validationErrorResponse.getPath(), "/properties");
        softAssert.assertEquals(validationErrorResponse.getError(), "Unauthorized");
        softAssert.assertEquals(validationErrorResponse.getMessage(), "Error while authenticating your access");
        softAssert.assertNotNull(validationErrorResponse.getTimestamp());

        softAssert.assertAll();
    }

    @Test(groups = {"negative", "security", "regression"})
    public void shouldReturn401WhenGettingPropertyRetrievalAllWithInvalidCredentialsTest() {
        initSoftAssert();

        AuthenticationService.setInvalidAuth();

        Response response = propertyService.propertyRetrievalAll();
        ValidationErrorResponse validationErrorResponse = response.as(ValidationErrorResponse.class);

        softAssert.assertEquals(response.getStatusCode(), 401, "Expected 401 Unauthorized");
        softAssert.assertEquals(validationErrorResponse.getException(), "Bad credentials");
        softAssert.assertEquals(validationErrorResponse.getPath(), "/properties");
        softAssert.assertEquals(validationErrorResponse.getError(), "Unauthorized");
        softAssert.assertEquals(validationErrorResponse.getMessage(), "Error while authenticating your access");
        softAssert.assertNotNull(validationErrorResponse.getTimestamp());

        softAssert.assertAll();
    }

    @Test(groups = {"negative", "security", "regression"})
    public void shouldReturn401WhenGettingPropertyRetrievalWithInvalidCredentialsTest() {
        initSoftAssert();

        AuthenticationService.setInvalidAuth();
        String propertyId = "4814adee-cd2e-4c70-921d-19b4f0cd527d";

        Response response = propertyService.propertyRetrieval(propertyId);
        ValidationErrorResponse validationErrorResponse = response.as(ValidationErrorResponse.class);

        softAssert.assertEquals(response.getStatusCode(), 401, "Expected 401 Unauthorized");
        softAssert.assertEquals(validationErrorResponse.getException(), "Bad credentials");
        softAssert.assertEquals(validationErrorResponse.getPath(), "/properties/" + propertyId);
        softAssert.assertEquals(validationErrorResponse.getError(), "Unauthorized");
        softAssert.assertEquals(validationErrorResponse.getMessage(), "Error while authenticating your access");
        softAssert.assertNotNull(validationErrorResponse.getTimestamp());

        softAssert.assertAll();
    }
}