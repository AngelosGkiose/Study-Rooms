package gr.hua.dit.studyrooms.web.ui;

import gr.hua.dit.studyrooms.core.service.AvailabilityService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;

@Controller
public class StudyRoomAvailabilityController {

    private final AvailabilityService availabilityService;

    public StudyRoomAvailabilityController(AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }
    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/room/calendar")
    public String showAvailabilityCalendar(Model model) {
        return "room/calendar";
    }
}