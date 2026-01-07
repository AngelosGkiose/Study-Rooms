package gr.hua.dit.studyrooms.web.ui;

import gr.hua.dit.studyrooms.core.service.ReservationBusinessLogicService;
import gr.hua.dit.studyrooms.core.service.model.CancelReservationRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/bookings")
public class StaffBookingController {

    private final ReservationBusinessLogicService reservationBusinessLogicService;

    public StaffBookingController(ReservationBusinessLogicService reservationBusinessLogicService) {
        this.reservationBusinessLogicService = reservationBusinessLogicService;
    }

    @PreAuthorize("hasRole('STAFF')")
    @GetMapping("/manage")
    public String manageBookings(Model model) {

        // όλες οι ACTIVE κρατήσεις για ακύρωση από staff
        var reservations = reservationBusinessLogicService.getAllActiveReservationsForStaff();

        model.addAttribute("reservations", reservations);

        return "staff/manage_bookings";
    }

    @PreAuthorize("hasRole('STAFF')")
    @PostMapping("/cancel")
    public String cancelByStaff(@RequestParam long reservationId, Model model) {

        var view = reservationBusinessLogicService.cancelReservationByStaff(new CancelReservationRequest(reservationId));

        model.addAttribute("reservation", view);

        return "staff/reservation_cancelled_by_staff";
    }
}
