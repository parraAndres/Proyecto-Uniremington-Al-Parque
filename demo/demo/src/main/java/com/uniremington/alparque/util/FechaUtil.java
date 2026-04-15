package com.uniremington.alparque.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class FechaUtil {

    private static final DateTimeFormatter FORMATTER_ES = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMATTER_ISO = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static String formatLocalDate(LocalDate date) {
        return date != null ? date.format(FORMATTER_ES) : null;
    }

    public static String formatLocalDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) : null;
    }

    public static LocalDate parseLocalDate(String dateStr) {
        return dateStr != null ? LocalDate.parse(dateStr, FORMATTER_ES) : null;
    }

    public static LocalDateTime toLocalDateTime(Date date) {
        return date != null ? date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime() : null;
    }

    public static Date toDate(LocalDateTime dateTime) {
        return dateTime != null ? java.sql.Timestamp.valueOf(dateTime) : null;
    }

    public static boolean isBetweenDates(LocalDate date, LocalDate startDate, LocalDate endDate) {
        if (date == null || startDate == null || endDate == null) {
            return false;
        }
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }
}
