package gr.hua.dit.studyrooms.core.repository;

import gr.hua.dit.studyrooms.core.model.Reservation;

import gr.hua.dit.studyrooms.core.model.ReservationStatus;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {


    List<Reservation> findByStudentIdOrderByDateDesc(Long studentId);

    List<Reservation> findByStudentIdAndStatusAndDateGreaterThanEqualOrderByDateAsc(
            long studentId,
            ReservationStatus status,
            LocalDate date
    );

    long countByStudentIdAndDateAndStatusIn(Long studentId, LocalDate date,final Collection<ReservationStatus> statuses);

    List<Reservation> findByStatusOrderByDateAsc(ReservationStatus status);
    List<Reservation> findByRoomIdAndDateAndStatus(
            Long roomId,
            LocalDate date,
            ReservationStatus status
    );
    List<Reservation> findByDateAndStatus(
            LocalDate date,
            ReservationStatus status
    );

}
