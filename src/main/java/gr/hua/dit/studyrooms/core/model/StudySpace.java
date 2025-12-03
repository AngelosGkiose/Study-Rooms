/*
Author: Sotirios Ilias
Student ID: it21925
*/

package gr.hua.dit.studyrooms.core.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.util.*;

@Entity
@Table(
        name = "study_space",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_study_space_name",
                        columnNames = "name"
                )
        },
        indexes = {
                @Index(
                        name = "idx_study_space_capacity",
                        columnList = "capacity"
                )
        }
)
public class StudySpace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @Column(name = "capacity", nullable = false)
    private int capacity;

    // Opening hours stored as element collection
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "study_space_opening_hours",
            joinColumns = @JoinColumn(name = "space_id"),
            uniqueConstraints = {
                    @UniqueConstraint(
                            name = "uk_opening_hours_unique",
                            columnNames = {"space_id", "day_of_week", "open_time", "close_time"}
                    )
            },
            indexes = {
                    @Index(name = "idx_opening_hours_day", columnList = "day_of_week")
            }
    )
    private Set<OpeningHour> openingHours = new HashSet<>();

    // constructors, getters, setters
    public StudySpace() {}

    public StudySpace(String name, int capacity) {
        this.name = name;
        this.capacity = capacity;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public Set<OpeningHour> getOpeningHours() { return openingHours; }
    public void setOpeningHours(Set<OpeningHour> openingHours) {
        this.openingHours = openingHours == null ? new HashSet<>() : openingHours;
    }

    // helper
    public void addOpeningHour(OpeningHour oh) { openingHours.add(oh); }
    public void removeOpeningHour(OpeningHour oh) { openingHours.remove(oh); }
}
