Hostfully API Test Automation Framework

📌 Project Overview
This project is an automated test framework for the Hostfully API, designed using TestNG and RestAssured. It supports functional API testing, including test case validation for properties and bookings.

🚀 Steps to Execute Tests

1️⃣ Clone the Repository
 git clone https://github.com/your-repo/hostfully-api-test.git
 cd hostfully-api-test

2️⃣ Install Dependencies  
  Make sure you have Java 17+ and Maven installed.
  Verify Java Version
  java -version
  Verify Maven Version
  mvn -version

3️⃣ Run Tests with Maven

To execute all tests:
  mvn test
  Run Specific Groups
  
  Run only Positive tests:
  mvn test -Dgroups=positive
  
  Run only Negative tests:
  mvn test -Dgroups=negative
  
  Run Regression Suite:
  mvn test -Dsurefire.suiteXmlFiles=testng-regression.xml
  
  Run tests using TestNG XML file:
  mvn test -Dsurefire.suiteXmlFiles=testng.xml
  
  Run Tests Without Maven (TestNG CLI)
Compile the tests:

javac -cp "lib/*;src/test/java" -d target/classes src/test/java/tests/*.java

Run tests:

java -cp "target/classes;lib/*" org.testng.TestNG testng.xml

📦 Dependencies & Libraries

The following dependencies are used in pom.xml:

Dependency Version
TestNG 7.7.0
RestAssured 5.3.0
Gson 2.8.9
Jackson Databind 2.15.0
Lombok 1.18.28

To install them manually:
mvn clean install

📂 Test Case Structure
Project Structure:

/hostfully-api-test
│── src
│   ├── main
│   │   ├── java
│   │   │   ├── services/  # Business logic (e.g., AuthenticationService, PropertyService)
│   │   │   ├── utils/     # Utility classes (DateTimeUtils, UUIDUtils)
│   │   │   ├── interfaces/ # Interface definitions for services
│   ├── test
│   │   ├── java
│   │   │   ├── tests/  # Test classes (BookingTests, PropertyTests)
│   │   │   ├── base/   # BaseTest class for setup/configuration
│   │   │   ├── helpers/ # Assertion & data helpers
│   │   ├── resources
│   │   │   ├── testng.xml  # Main TestNG suite
│   │   │   ├── testng-positive.xml  # Positive tests suite
│   │   │   ├── testng-negative.xml  # Negative tests suite
│── pom.xml  # Maven build file
│── README.md  # Project Documentation

Test Class Structure (Example: BookingTests.java)

Each test class extends BaseTest for shared setup and services.

package tests;
import base.BaseTest;
import org.testng.annotations.Test;
public class BookingTests extends BaseTest {
    
    @Test(groups = {"positive"})
    public void shouldReturn200WhenGettingAllBookings() {
        assert bookingService != null : "❌ bookingService is NULL!";
        bookingService.bookingRetrievalAll();
        System.out.println("✅ Test Passed: shouldReturn200WhenGettingAllBookings");
    }
}

📊 Test Reports & Debugging

View Test Reports (TestNG Reports & Surefire Reports)
After execution, reports are generated in:
/target/surefire-reports/
To open the TestNG report:
start target/surefire-reports/index.html  # Windows
open target/surefire-reports/index.html  # macOS/Linux

Debugging Issues
If tests fail due to missing dependencies:
mvn dependency:resolve
Run tests with detailed logging:
mvn test -X

✅ Conclusion

Use mvn test to run all tests.
Use mvn test -Dgroups=positive for positive tests.
Check logs and reports in /target/surefire-reports/.
Ensure dependencies are installed using mvn install.

🚀 Happy Testing! 🚀

