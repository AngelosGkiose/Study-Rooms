package gr.hua.dit.studyrooms.web.rest;

import gr.hua.dit.studyrooms.core.service.ReservationDataService;
import gr.hua.dit.studyrooms.core.service.model.ReservationView;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for managing {@code Reservation} resource.
 */
@RestController
@RequestMapping(value = "/api/v1/reservation", produces = MediaType.APPLICATION_JSON_VALUE)
public class ReservationResource {

    private final ReservationDataService reservationDataService;

    public ReservationResource(final ReservationDataService reservationDataService) {
        if (reservationDataService == null) throw new NullPointerException();
        this.reservationDataService = reservationDataService;
    }
    @PreAuthorize("hasRole('INTEGRATION_READ')")
    @GetMapping("")
    public List<ReservationView> tickets() {
        final List<ReservationView> ReservationViewList = this.reservationDataService.getAllReservations();
        return ReservationViewList;
    }
}