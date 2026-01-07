package gr.hua.dit.studyrooms.web.ui;
import gr.hua.dit.studyrooms.core.service.RoomAnalyticsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
@Controller
@RequestMapping("/staff/stats")
@PreAuthorize("hasRole('STAFF')")
public class StatsUIController {

    private final RoomAnalyticsService analyticsService;
    public StatsUIController(RoomAnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/rooms")
    public String roomStats(
            @RequestParam(required = false) LocalDate date,
            Model model
    ) {
        LocalDate targetDate = (date != null) ? date : LocalDate.now();
        model.addAttribute("selectedDate", targetDate);
        model.addAttribute("stats",
                analyticsService.getOccupancy(targetDate));
        return "staff/room_stats";
    }

}
