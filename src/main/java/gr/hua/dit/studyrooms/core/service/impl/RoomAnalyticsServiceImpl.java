package gr.hua.dit.studyrooms.core.service.impl;

import gr.hua.dit.studyrooms.core.model.Reservation;
import gr.hua.dit.studyrooms.core.model.ReservationStatus;
import gr.hua.dit.studyrooms.core.repository.ReservationRepository;
import gr.hua.dit.studyrooms.core.repository.StudyRoomRepository;
import gr.hua.dit.studyrooms.core.service.ReservationDataService;
import gr.hua.dit.studyrooms.core.service.RoomAnalyticsService;
import gr.hua.dit.studyrooms.core.service.model.RoomOccupancyView;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class RoomAnalyticsServiceImpl implements RoomAnalyticsService {

    private final StudyRoomRepository roomRepo;
    private final ReservationRepository reservationRepo;

    public RoomAnalyticsServiceImpl(StudyRoomRepository roomRepo,
                                ReservationRepository reservationRepo) {
        this.roomRepo = roomRepo;
        this.reservationRepo = reservationRepo;
    }

    public List<RoomOccupancyView> getOccupancy(LocalDate date) {
        return roomRepo.findAll().stream().map(room -> {
            int booked = reservationRepo
                    .findByRoomIdAndDateAndStatus(room.getId(), date, ReservationStatus.ACTIVE)
                    .stream()
                    .mapToInt(Reservation::getSeatsReserved)
                    .sum();

            return new RoomOccupancyView(
                    room.getId(),
                    room.getName(),
                    room.getCapacity(),
                    booked,
                    room.getCapacity() - booked
            );
        }).toList();
    }
}
