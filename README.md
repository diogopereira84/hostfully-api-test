Booking Service Automated Test Suite

Overview

This repository contains an automated test suite for testing the Booking and Property APIs. The test suite is built using TestNG and RestAssured, following best practices for dependency injection and modularity.

Project Structure

📦 booking-test-suite
 ┣ 📂 base
 ┃ ┣ 📜 BaseTest.java
 ┣ 📂 helpers
 ┃ ┣ 📜 AssertionHelper.java
 ┃ ┣ 📜 PropertyHelper.java
 ┣ 📂 interfaces
 ┃ ┣ 📜 IBookingService.java
 ┃ ┣ 📜 IPropertyService.java
 ┣ 📂 models
 ┃ ┣ 📂 request
 ┃ ┣ 📂 response
 ┣ 📂 services
 ┃ ┣ 📜 AuthenticationService.java
 ┃ ┣ 📜 BookingService.java
 ┃ ┣ 📜 PropertyService.java
 ┣ 📂 tests
 ┃ ┣ 📜 BookingTests.java
 ┃ ┣ 📜 PropertyTests.java
 ┣ 📂 utils
 ┃ ┣ 📜 AppSettings.java
 ┃ ┣ 📜 DateTimeUtils.java
 ┃ ┣ 📜 JsonUtils.java
 ┃ ┣ 📜 UUIDUtils.java
 ┃ ┣ 📜 TestListener.java
 ┣ 📜 pom.xml
 ┣ 📜 README.md
 ┗ 📜 testng.xml

Prerequisites

Java 17+
Maven
TestNG
RestAssured
Lombok
SLF4J (for logging)

Installation

Clone the repository:

git clone https://github.com/diogopereira84/hostfully-api-test.git

Install dependencies:

mvn clean install

Update the .env file or appsettings-qa.properties with your credentials:

ADMIN_USERNAME=your_username
ADMIN_PASSWORD=your_password
API_BASE_URI=https://qa-assessment.svc.hostfully.com

Running Tests

Execute all tests:
mvn test

Run specific test groups:

mvn test -Dgroups="regression"
mvn test -Dgroups="positive"
mvn test -Dgroups="negative"

Generate test reports:

mvn surefire-report:report

Test Plan

The test suite is designed to validate the core functionalities of the Booking API. Key scenarios include:
✅ Successful booking retrieval
✅ Booking creation with mandatory and optional fields
❌ Invalid booking scenarios (missing fields, invalid dates, etc.)
🔒 Security checks (401 Unauthorized, 403 Forbidden)
🔄 Rebooking and cancellation tests
📜 Validation of property details and constraints
