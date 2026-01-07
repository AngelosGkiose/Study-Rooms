package gr.hua.dit.studyrooms.core.service.impl;

import gr.hua.dit.studyrooms.core.model.PersonType;
import gr.hua.dit.studyrooms.core.model.StudyRoom;
import gr.hua.dit.studyrooms.core.repository.StudyRoomRepository;
import gr.hua.dit.studyrooms.core.security.CurrentUser;
import gr.hua.dit.studyrooms.core.security.CurrentUserProvider;
import gr.hua.dit.studyrooms.core.service.StudyRoomService;
import gr.hua.dit.studyrooms.core.service.mapper.StudyRoomMapper;
import gr.hua.dit.studyrooms.core.service.model.CreateStudyRoomRequest;
import gr.hua.dit.studyrooms.core.service.model.CreateStudyRoomResult;
import gr.hua.dit.studyrooms.core.service.model.StudyRoomView;
import gr.hua.dit.studyrooms.core.service.model.UpdateStudyRoomResult;
import gr.hua.dit.studyrooms.core.service.model.UpdateStudyRoomRequest;
import java.util.List;

import org.springframework.stereotype.Service;

import java.time.LocalTime;

/**
 * Default implementation of {@link StudyRoomService}.
 */
@Service
public class StudyRoomServiceImpl implements StudyRoomService {

    private final StudyRoomRepository studyRoomRepository;
    private final StudyRoomMapper studyRoomMapper;
    private final CurrentUserProvider currentUserProvider;

    public StudyRoomServiceImpl(final StudyRoomRepository studyRoomRepository,
                                final StudyRoomMapper studyRoomMapper,
                                final CurrentUserProvider currentUserProvider) {
        if (studyRoomRepository == null) throw new NullPointerException();
        if (studyRoomMapper == null) throw new NullPointerException();

        this.studyRoomRepository = studyRoomRepository;
        this.studyRoomMapper = studyRoomMapper;
        if (currentUserProvider == null) throw new NullPointerException();
        this.currentUserProvider = currentUserProvider;
    }
    @Override
    public UpdateStudyRoomResult updateStudyRoom(Long id,
                                                 UpdateStudyRoomRequest req,
                                                 boolean notify) {

        if (id == null) {
            return UpdateStudyRoomResult.fail("Invalid room ID.");
        }

        var roomOpt = studyRoomRepository.findById(id);
        if (roomOpt.isEmpty()) {
            return UpdateStudyRoomResult.fail("Room not found.");
        }

        StudyRoom room = roomOpt.get();

        // Validate capacity
        if (req.capacity() <= 0) {
            return UpdateStudyRoomResult.fail("Capacity must be greater than 0");
        }

        LocalTime opening;
        LocalTime closing;

        try {
            opening = LocalTime.parse(req.openingHour());
            closing = LocalTime.parse(req.closingHour());
        } catch (Exception e) {
            return UpdateStudyRoomResult.fail("Invalid time format (HH:MM)");
        }

        if (!opening.isBefore(closing)) {
            return UpdateStudyRoomResult.fail("Opening time must be before closing time");
        }

        // If name changed -> check uniqueness
        if (!room.getName().equalsIgnoreCase(req.name()) &&
                studyRoomRepository.existsByNameIgnoreCase(req.name())) {
            return UpdateStudyRoomResult.fail("A room with this name already exists.");
        }

        // Update entity
        room.setName(req.name());
        room.setCapacity(req.capacity());
        room.setOpeningTime(opening);
        room.setClosingTime(closing);

        // Save
        room = studyRoomRepository.save(room);

        // Convert to view
        StudyRoomView studyRoomView = studyRoomMapper.convertStudyRoomToStudyRoomView(room);

        return UpdateStudyRoomResult.success(studyRoomView);
    }

    @Override
    public CreateStudyRoomResult createStudyRoom(final CreateStudyRoomRequest createStudyRoomRequest,
                                                 final boolean notify) {
        if (createStudyRoomRequest == null) throw new NullPointerException();

        final String name = createStudyRoomRequest.name().strip();
        final int capacity = createStudyRoomRequest.capacity();
        final String openingStr = createStudyRoomRequest.openingHour().strip();
        final String closingStr = createStudyRoomRequest.closingHour().strip();

        final LocalTime openingTime;
        final LocalTime closingTime;

        final CurrentUser currentUser = this.currentUserProvider.requireCurrentUser();
        if (currentUser.type() != PersonType.STAFF) {
            throw new SecurityException("Staff type/role required");
        }

        try {
            openingTime = LocalTime.parse(openingStr);
            closingTime = LocalTime.parse(closingStr);
        } catch (Exception e) {
            return CreateStudyRoomResult.fail("Opening/Closing hour format must be HH:MM");
        }

        // Validations
        // ------------------------------------------------------
        if (name.isEmpty()) {
            return CreateStudyRoomResult.fail("Room name cannot be empty");
        }

        if (capacity <= 0) {
            return CreateStudyRoomResult.fail("Capacity must be greater than 0");
        }

        if (!openingTime.isBefore(closingTime)) {
            return CreateStudyRoomResult.fail("Opening hour must be before closing hour");
        }

        // Unique constraint validation
        if (this.studyRoomRepository.existsByNameIgnoreCase(name)) {
            return CreateStudyRoomResult.fail("A study room with that name already exists");
        }

        // Create instance
        // ------------------------------------------------------
        StudyRoom room = new StudyRoom();
        room.setId(null);  // auto generated
        room.setName(name);
        room.setCapacity(capacity);
        room.setOpeningTime(openingTime);
        room.setClosingTime(closingTime);

        // Persist to DB
        // ------------------------------------------------------
        room = this.studyRoomRepository.save(room);

        // Convert to View
        // ------------------------------------------------------
        final StudyRoomView studyRoomView = this.studyRoomMapper.convertStudyRoomToStudyRoomView(room);

        return CreateStudyRoomResult.success(studyRoomView);

    }
}
