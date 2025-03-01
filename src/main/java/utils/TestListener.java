package utils;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TestListener implements ITestListener {
    private static final Logger logger = LoggerFactory.getLogger(TestListener.class);
    private static final String LOG_FILE = "test-results.log";

    private void logToFile(String message) {
        try (FileWriter fileWriter = new FileWriter(LOG_FILE, true);
             PrintWriter printWriter = new PrintWriter(fileWriter)) {
            printWriter.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " - " + message);
        } catch (IOException e) {
            logger.error("Error writing to log file: ", e);
        }
    }

    @Override
    public void onTestStart(ITestResult result) {
        String message = "Starting Test: " + result.getMethod().getMethodName();
        logger.info(message);
        logToFile(message);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        String message = "Test Passed: " + result.getMethod().getMethodName();
        logger.info(message);
        logToFile(message);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String message = "Test Failed: " + result.getMethod().getMethodName() + " - " + result.getThrowable();
        logger.error(message);
        logToFile(message);
        sendFailureNotification(message);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        String message = "Test Skipped: " + result.getMethod().getMethodName();
        logger.warn(message);
        logToFile(message);
    }

    @Override
    public void onStart(ITestContext context) {
        String message = "Starting Test Suite: " + context.getName();
        logger.info(message);
        logToFile(message);
    }

    @Override
    public void onFinish(ITestContext context) {
        String message = "Finished Test Suite: " + context.getName();
        logger.info(message);
        logToFile(message);
    }

    private void sendFailureNotification(String message) {
        // Placeholder for email notification logic
        logger.info("Sending email notification for test failure: {}", message);

    }
}
