package gr.hua.dit.studyrooms.web.rest;


import gr.hua.dit.studyrooms.core.service.StudyRoomDataService;
import gr.hua.dit.studyrooms.core.service.model.StudyRoomView;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for managing {@code Person} resource.
 */
@RestController
@RequestMapping(value = "/api/v1/studyroom", produces = MediaType.APPLICATION_JSON_VALUE)
public class StudyRoomResource {

    private final StudyRoomDataService studyRoomDataService;

    public StudyRoomResource(final StudyRoomDataService studyRoomDataService) {
        if (studyRoomDataService == null) throw new NullPointerException();
        this.studyRoomDataService = studyRoomDataService;
    }

    @PreAuthorize("hasRole('INTEGRATION_READ')")
    @GetMapping("")
    public List<StudyRoomView> studyRooms() {
        final List<StudyRoomView> studyRoomViewList = this.studyRoomDataService.getAllRooms();
        return studyRoomViewList;
    }
}