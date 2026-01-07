package gr.hua.dit.studyrooms.core.service.impl;

import gr.hua.dit.studyrooms.core.model.StudyRoom;
import gr.hua.dit.studyrooms.core.repository.StudyRoomRepository;
import gr.hua.dit.studyrooms.core.service.StudyRoomDataService;
import gr.hua.dit.studyrooms.core.service.mapper.StudyRoomMapper;
import gr.hua.dit.studyrooms.core.service.model.StudyRoomView;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class StudyRoomDataServiceImpl implements StudyRoomDataService {
    private final StudyRoomRepository studyRoomRepository;
    private final StudyRoomMapper studyRoomMapper;

    public StudyRoomDataServiceImpl(final StudyRoomRepository studyRoomRepository,
                                      final StudyRoomMapper studyRoomMapper) {
        if (studyRoomRepository == null) throw new NullPointerException();
        if (studyRoomMapper == null) throw new NullPointerException();
        this.studyRoomRepository = studyRoomRepository;
        this.studyRoomMapper = studyRoomMapper;
    }

    @Override
    public List<StudyRoomView> getAllRooms() {
        final List<StudyRoom> reservationList = this.studyRoomRepository.findAll();
        final List<StudyRoomView> studyRoomViewList = reservationList
                .stream()
                .map(this.studyRoomMapper::convertStudyRoomToStudyRoomView)
                .toList();
        return studyRoomViewList;
    }
}
