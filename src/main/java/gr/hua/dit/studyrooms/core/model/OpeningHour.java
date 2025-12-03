/*
Author: Sotirios Ilias
Student ID: it21925
*/

package gr.hua.dit.studyrooms.core.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;
import java.time.DayOfWeek;
import java.util.Objects;

@Embeddable
public class OpeningHour {

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false, length = 12)
    private DayOfWeek dayOfWeek;

    @Column(name = "open_time", nullable = false)
    private LocalTime openTime;

    @Column(name = "close_time", nullable = false)
    private LocalTime closeTime;

    public OpeningHour() {}

    public OpeningHour(DayOfWeek dayOfWeek, LocalTime openTime, LocalTime closeTime) {
        this.dayOfWeek = dayOfWeek;
        this.openTime = openTime;
        this.closeTime = closeTime;
    }

    public DayOfWeek getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(DayOfWeek dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public LocalTime getOpenTime() { return openTime; }
    public void setOpenTime(LocalTime openTime) { this.openTime = openTime; }

    public LocalTime getCloseTime() { return closeTime; }
    public void setCloseTime(LocalTime closeTime) { this.closeTime = closeTime; }

    // equals/hashCode required for Set behavior
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OpeningHour)) return false;
        OpeningHour that = (OpeningHour) o;
        return dayOfWeek == that.dayOfWeek &&
                Objects.equals(openTime, that.openTime) &&
                Objects.equals(closeTime, that.closeTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dayOfWeek, openTime, closeTime);
    }
}
