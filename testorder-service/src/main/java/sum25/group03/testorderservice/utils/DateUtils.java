package sum25.group03.testorderservice.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    public static final Integer _15_DAYS = 15;

    // get LocalDate from "yyyy-MM-dd" string:
    public static LocalDate getLocalDateFromYYYYMMDDStr(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(dateString, formatter);
    }

    public static LocalDate getLocalDateFromDDMMYYYYStr(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return LocalDate.parse(dateString, formatter);
    }

    public static LocalDate getLocalDateFromMMDDYYYYStr(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
        return LocalDate.parse(dateString, formatter);
    }

    // get date of 15 days ago:
    public static LocalDate getDateByNumberOfDaysAgo(Integer previousDays) {
        LocalDate now = LocalDate.now();
        return now.minusDays(previousDays);
    }
}
