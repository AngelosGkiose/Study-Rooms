package gr.hua.dit.studyrooms.core.port;

import java.time.LocalDate;

/**
 * Port to external service for checking public holidays.
 */
public interface HolidayPort {

    /**
     * @return true if the given date is a public holiday
     */
    boolean isHoliday(LocalDate date);
}
