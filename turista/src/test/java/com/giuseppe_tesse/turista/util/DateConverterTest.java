package com.giuseppe_tesse.turista.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.Test;

public class DateConverterTest {

    @Test
    public void testDate2LocalDate() {
        Date sqlDate = Date.valueOf("2023-10-27");
        LocalDate localDate = DateConverter.date2LocalDate(sqlDate);
        assertNotNull(localDate);
        assertEquals(2023, localDate.getYear());
        assertEquals(10, localDate.getMonthValue());
        assertEquals(27, localDate.getDayOfMonth());
    }

    @Test
    public void testToSqlDate() {
        LocalDate localDate = LocalDate.of(2023, 10, 27);
        Date sqlDate = DateConverter.toSqlDate(localDate);
        assertNotNull(sqlDate);
        assertEquals("2023-10-27", sqlDate.toString());
    }

    @Test
    public void testConvertLocalDateTimeFromTimestamp() {
        Timestamp ts = Timestamp.valueOf("2023-10-27 10:30:00");
        LocalDateTime ldt = DateConverter.convertLocalDateTimeFromTimestamp(ts);
        assertNotNull(ldt);
        assertEquals(2023, ldt.getYear());
        assertEquals(10, ldt.getMonthValue());
        assertEquals(27, ldt.getDayOfMonth());
        assertEquals(10, ldt.getHour());
        assertEquals(30, ldt.getMinute());
    }

    @Test
    public void testConvertTimestampFromLocalDateTime() {
        LocalDateTime ldt = LocalDateTime.of(2023, 10, 27, 10, 30, 0);
        Timestamp ts = DateConverter.convertTimestampFromLocalDateTime(ldt);
        assertNotNull(ts);
        assertEquals(Timestamp.valueOf("2023-10-27 10:30:00"), ts);
    }
}
