package utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class DateTimeUtils {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final DateTimeFormatter FORMATTER_HH = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            .withZone(ZoneOffset.UTC);

    public static String getCurrentUtcTimestamp() {
        return FORMATTER_HH.format(Instant.now());
    }

    /**
     * Adds one day to the given date string in "YYYY-MM-DD" format.
     *
     * @param dateString The input date string in "YYYY-MM-DD" format.
     * @return The new date string after adding one day, or an error message if parsing fails.
     */
    public static String addOneDay(String dateString) {
        try {
            LocalDate date = LocalDate.parse(dateString, FORMATTER);
            LocalDate nextDay = date.plusDays(1);
            return nextDay.format(FORMATTER);
        } catch (DateTimeParseException e) {
            return "Invalid date format. Please use YYYY-MM-DD.";
        }
    }

    public static String formatDate(List<Integer> dateList) {
        if (dateList != null && dateList.size() == 3) {
            return String.format("%04d-%02d-%02d", dateList.get(0), dateList.get(1), dateList.get(2));
        }
        return "Invalid or missing endDate format.";
    }
}
