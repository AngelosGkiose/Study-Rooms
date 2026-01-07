package gr.hua.dit.studyrooms.web.ui;

import gr.hua.dit.studyrooms.core.service.StudyRoomDataService;
import gr.hua.dit.studyrooms.core.service.StudyRoomService;
import gr.hua.dit.studyrooms.core.service.model.CreateStudyRoomRequest;
import gr.hua.dit.studyrooms.core.service.model.CreateStudyRoomResult;
import gr.hua.dit.studyrooms.core.service.model.UpdateStudyRoomResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import gr.hua.dit.studyrooms.core.service.model.UpdateStudyRoomRequest;
import gr.hua.dit.studyrooms.core.repository.StudyRoomRepository;
import gr.hua.dit.studyrooms.core.model.StudyRoom;

@Controller
public class StudyRoomController {

    private final StudyRoomService studyRoomService;
    private final StudyRoomRepository studyRoomRepository;
    private final StudyRoomDataService studyRoomDataService;

    public StudyRoomController(final StudyRoomRepository studyRoomRepository,final StudyRoomService studyRoomService,final StudyRoomDataService studyRoomDataService) {
        if (studyRoomService == null) throw new NullPointerException();
        if (studyRoomDataService == null) throw new NullPointerException();
        this.studyRoomService = studyRoomService;
        this.studyRoomRepository = studyRoomRepository;
        this.studyRoomDataService = studyRoomDataService;
    }

    /** GET → Show creation form */
    @PreAuthorize("hasRole('STAFF')")
    @GetMapping("/room/create")
    public String showCreateRoomForm(Model model) {

        CreateStudyRoomRequest request =
                new CreateStudyRoomRequest("", 0, "08:00", "20:00");

        model.addAttribute("createStudyRoomRequest", request);
        return "room/create-room";
    }

    /** POST → Handle creation */
    @PreAuthorize("hasRole('STAFF')")
    @PostMapping("/room/create")
    public String handleCreateRoom(
            @ModelAttribute("createStudyRoomRequest") CreateStudyRoomRequest request,
            Model model
    ) {
        CreateStudyRoomResult result = studyRoomService.createStudyRoom(request);

        if (result.created()) {
            return "redirect:/room/list?created=1";
        }

        model.addAttribute("errorMessage", result.reason());
        model.addAttribute("createStudyRoomRequest", request);
        return "room/create-room"; // η φόρμα ξανά με error
    }
    @PreAuthorize("hasRole('STAFF')")
    @GetMapping("/room/list")
    public String listRooms(@RequestParam(required = false) String updated,
                            @RequestParam(required = false) String created,
                            Model model) {

        model.addAttribute("rooms", studyRoomDataService.getAllRooms());

        if (updated != null) {
            model.addAttribute("successMessage", "Το δωμάτιο ενημερώθηκε με επιτυχία!");
        }
        if (created != null) {
            model.addAttribute("successMessage", "Το δωμάτιο δημιουργήθηκε με επιτυχία!");
        }

        return "room/list";
    }

    // -------------------------------------------
    // EDIT ROOM BY NAME
    // -------------------------------------------
    @PreAuthorize("hasRole('STAFF')")
    @GetMapping("/room/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {

        // Βρίσκουμε το StudyRoom με βάση το ID
        var roomOpt = studyRoomRepository.findById(id);
        if (roomOpt.isEmpty()) {
            return "redirect:/room/list"; // αν δεν βρεθεί, επιστροφή στη λίστα
        }

        StudyRoom room = roomOpt.get();

        // Δημιουργούμε ένα UpdateStudyRoomRequest για να γεμίσει η φόρμα
        UpdateStudyRoomRequest editRequest = new UpdateStudyRoomRequest(
                room.getId(),
                room.getName(),
                room.getCapacity(),
                room.getOpeningTime().toString(),
                room.getClosingTime().toString()
        );

        model.addAttribute("editStudyRoomRequest", editRequest);

        return "room/edit-room";
    }

    @PreAuthorize("hasRole('STAFF')")
    @PostMapping("/room/edit/{id}")
    public String handleEditRoom(
            @PathVariable Long id,
            @ModelAttribute("editStudyRoomRequest") UpdateStudyRoomRequest req,
            Model model
    ) {

        UpdateStudyRoomResult result = studyRoomService.updateStudyRoom(id, req);

        if (result.updated()) {
            return "redirect:/room/list?updated=1";
        }

        model.addAttribute("errorMessage", result.reason());
        return "room/edit-room";
    }

}
