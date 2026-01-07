package gr.hua.dit.studyrooms.core.service;

import gr.hua.dit.studyrooms.core.model.Person;
import gr.hua.dit.studyrooms.core.model.Reservation;
import gr.hua.dit.studyrooms.core.model.ReservationStatus;
import gr.hua.dit.studyrooms.core.repository.PersonRepository;
import gr.hua.dit.studyrooms.core.repository.ReservationRepository;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Service
public class NoShowScheduler {

    private static final int PENALTY_DAYS = 3;

    private final ReservationRepository reservationRepository;
    private final PersonRepository personRepository;

    public NoShowScheduler(ReservationRepository reservationRepository,
                           PersonRepository personRepository) {
        this.reservationRepository = reservationRepository;
        this.personRepository = personRepository;
    }

    @Transactional
    @Scheduled(cron = "0 0 1 * * *") // κάθε μέρα 01:00
    public void markNoShows() {

        LocalDate yesterday = LocalDate.now().minusDays(1);

        List<Reservation> expiredReservations =
                reservationRepository.findByDateAndStatus(
                        yesterday,
                        ReservationStatus.ACTIVE
                );

        for (Reservation reservation : expiredReservations) {

            // 1. Μαρκάρουμε την κράτηση ως NO_SHOW
            reservation.setStatus(ReservationStatus.NO_SHOW);

            // 2. Εφαρμόζουμε penalty στον φοιτητή
            Person student = reservation.getStudent();
            student.applyPenalty(
                    Instant.now().plus(Duration.ofDays(PENALTY_DAYS))
            );

            personRepository.save(student);
            reservationRepository.save(reservation);
        }
    }
}
