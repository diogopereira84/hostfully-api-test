package utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class DateTimeUtils {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final DateTimeFormatter FORMATTER_HH = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            .withZone(ZoneOffset.UTC);

    public static String getCurrentUtcTimestamp() {
        return FORMATTER.format(Instant.now());
    }

    /**
     * Adds one day to the given start and end dates.
     *
     * @param startDate Original start date in yyyy-MM-dd format.
     * @param endDate Original end date in yyyy-MM-dd format.
     * @return A string array with updated start and end dates.
     */
    public static String[] addOneDayToInterval(String startDate, String endDate) {
        LocalDate newStartDate = LocalDate.parse(startDate, FORMATTER).plusDays(1);
        LocalDate newEndDate = LocalDate.parse(endDate, FORMATTER).plusDays(1);

        return new String[]{newStartDate.format(FORMATTER), newEndDate.format(FORMATTER)};
    }
}
