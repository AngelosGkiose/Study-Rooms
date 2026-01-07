package gr.hua.dit.studyrooms.web.ui;

import gr.hua.dit.studyrooms.core.repository.StudyRoomRepository;
import gr.hua.dit.studyrooms.core.security.CurrentUserProvider;
import gr.hua.dit.studyrooms.core.service.ReservationBusinessLogicService;
import gr.hua.dit.studyrooms.core.service.model.CreateReservationRequest;
import gr.hua.dit.studyrooms.core.service.model.ReservationView;
import gr.hua.dit.studyrooms.web.ui.model.ReservationForm;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;


@Controller
@RequestMapping("/rooms")
public class StudyRoomUIController {

    private final ReservationBusinessLogicService reservationBusinessLogicService;
    private final CurrentUserProvider currentUserProvider;
    private final StudyRoomRepository roomRepo;

    public StudyRoomUIController(
            ReservationBusinessLogicService reservationBusinessLogicService,
            CurrentUserProvider currentUserProvider,
            StudyRoomRepository roomRepo
    ) {
        this.reservationBusinessLogicService = reservationBusinessLogicService;
        this.currentUserProvider = currentUserProvider;
        this.roomRepo = roomRepo;
    }
    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/reserve")
    public String showReservationForm(
            @RequestParam long roomId,
            @RequestParam String date,
            Model model
    ) {
        var user = currentUserProvider.requireCurrentUser();
        var room = roomRepo.findById(roomId).orElseThrow();

        ReservationForm form = new ReservationForm();
        form.setRoomId(roomId);
        form.setDate(LocalDate.parse(date));
        form.setSeatsRequested(1);

        model.addAttribute("form", form);
        model.addAttribute("room", room);
        model.addAttribute("me", user);

        return "reservations/reservation_form";
    }

    @PostMapping("/reserve")
    public String handleReservationForm(
            @ModelAttribute("form") @Valid ReservationForm form,
            BindingResult result,
            Model model
    ) {
        if (result.hasErrors()) {
            return "reservations/reservation_form";
        }

        CreateReservationRequest req = new CreateReservationRequest(
                form.getRoomId(),
                form.getDate(),
                form.getSeatsRequested()
        );

        ReservationView view = reservationBusinessLogicService.createReservation(req);

        model.addAttribute("reservation", view);
        return "reservations/reservation_success";
    }
}

