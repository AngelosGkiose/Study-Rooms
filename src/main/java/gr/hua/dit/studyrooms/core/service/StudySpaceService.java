/*
Author: Sotirios Ilias
Student ID: it21925
*/

package gr.hua.dit.studyrooms.core.service;

import gr.hua.dit.studyrooms.core.model.OpeningHour;
import gr.hua.dit.studyrooms.core.model.StudySpace;
import gr.hua.dit.studyrooms.core.repository.StudySpaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.time.LocalTime;
import java.time.DayOfWeek;

@Service
@Transactional
public class StudySpaceService {

    private final StudySpaceRepository repo;

    public StudySpaceService(StudySpaceRepository repo) {
        this.repo = repo;
    }

    public List<StudySpace> findAll() {
        return repo.findAll();
    }

    public Optional<StudySpace> findById(Long id) {
        return repo.findById(id);
    }

    public StudySpace create(StudySpace space) {
        validateStudySpace(space);
        return repo.save(space);
    }

    public StudySpace update(Long id, StudySpace updated) {
        validateStudySpace(updated);
        StudySpace existing = repo.findById(id).orElseThrow(() ->
                new NoSuchElementException("StudySpace not found: " + id));
        existing.setName(updated.getName());
        existing.setCapacity(updated.getCapacity());
        existing.setOpeningHours(updated.getOpeningHours());
        return repo.save(existing);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    // Business validations
    private void validateStudySpace(StudySpace s) {
        if (s.getCapacity() <= 0) {
            throw new IllegalArgumentException("Capacity must be > 0");
        }
        // ensure each opening hour has open < close
        for (OpeningHour oh : s.getOpeningHours()) {
            if (oh.getOpenTime() == null || oh.getCloseTime() == null || oh.getDayOfWeek() == null) {
                throw new IllegalArgumentException("Opening hours must have day and times");
            }
            if (!oh.getOpenTime().isBefore(oh.getCloseTime())) {
                throw new IllegalArgumentException("Open time must be before close time for " + oh.getDayOfWeek());
            }
        }
        // ensure no overlapping hours for the same day
        Map<DayOfWeek, List<OpeningHour>> grouped = new HashMap<>();
        for (OpeningHour oh : s.getOpeningHours()) {
            grouped.computeIfAbsent(oh.getDayOfWeek(), k -> new ArrayList<>()).add(oh);
        }
        for (Map.Entry<DayOfWeek, List<OpeningHour>> e : grouped.entrySet()) {
            List<OpeningHour> list = e.getValue();
            list.sort(Comparator.comparing(OpeningHour::getOpenTime));
            for (int i = 1; i < list.size(); i++) {
                if (!list.get(i).getOpenTime().isAfter(list.get(i-1).getCloseTime())) {
                    throw new IllegalArgumentException("Opening hours overlap on " + e.getKey());
                }
            }
        }
    }
}
