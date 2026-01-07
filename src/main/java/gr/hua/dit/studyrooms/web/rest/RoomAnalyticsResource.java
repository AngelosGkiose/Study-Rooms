package gr.hua.dit.studyrooms.web.rest;

import gr.hua.dit.studyrooms.core.service.RoomAnalyticsService;
import gr.hua.dit.studyrooms.core.service.model.RoomOccupancyView;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(value="/api/v1/analytics/rooms",produces = MediaType.APPLICATION_JSON_VALUE)
public class RoomAnalyticsResource {

    private final RoomAnalyticsService analyticsService;

    public RoomAnalyticsResource(RoomAnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }
    @PreAuthorize("hasRole('INTEGRATION_READ')")
    @GetMapping("/occupancy")
    public List<RoomOccupancyView> occupancy(@RequestParam LocalDate date) {
        return analyticsService.getOccupancy(date);
    }
}
