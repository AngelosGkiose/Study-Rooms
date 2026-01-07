package gr.hua.dit.studyrooms.core.service;

import gr.hua.dit.studyrooms.core.service.model.CreateStudyRoomRequest;
import gr.hua.dit.studyrooms.core.service.model.CreateStudyRoomResult;
import gr.hua.dit.studyrooms.core.service.model.UpdateStudyRoomResult;
import gr.hua.dit.studyrooms.core.service.model.UpdateStudyRoomRequest;
import gr.hua.dit.studyrooms.core.service.model.StudyRoomView;
import java.util.List;

/**
 * Service for managing {@link gr.hua.dit.studyrooms.core.model.StudyRoom}.
 */
public interface StudyRoomService {

    CreateStudyRoomResult createStudyRoom(final CreateStudyRoomRequest createStudyRoomRequest, final boolean notify);

    default CreateStudyRoomResult createStudyRoom(final CreateStudyRoomRequest createStudyRoomRequest) {
        return this.createStudyRoom(createStudyRoomRequest, true);
    }
    UpdateStudyRoomResult updateStudyRoom(Long id, UpdateStudyRoomRequest request, boolean notify);

    default UpdateStudyRoomResult updateStudyRoom(Long id, UpdateStudyRoomRequest request) {
        return updateStudyRoom(id, request, true);
    }

}