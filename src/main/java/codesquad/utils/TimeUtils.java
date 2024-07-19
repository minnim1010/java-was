package codesquad.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeUtils {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private TimeUtils() {
    }

    public static LocalDateTime toLocalDateTime(String time) {
        return LocalDateTime.parse(time, formatter);
    }

    public static String format(LocalDateTime time) {
        return formatter.format(time);
    }
}
