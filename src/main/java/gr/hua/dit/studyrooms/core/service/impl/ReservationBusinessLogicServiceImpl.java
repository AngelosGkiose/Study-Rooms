package gr.hua.dit.studyrooms.core.service.impl;

import gr.hua.dit.studyrooms.core.model.*;
import gr.hua.dit.studyrooms.core.port.HolidayPort;
import gr.hua.dit.studyrooms.core.repository.PersonRepository;
import gr.hua.dit.studyrooms.core.repository.ReservationRepository;
import gr.hua.dit.studyrooms.core.repository.StudyRoomRepository;
import gr.hua.dit.studyrooms.core.security.CurrentUser;
import gr.hua.dit.studyrooms.core.model.ReservationStatus;
import gr.hua.dit.studyrooms.core.security.CurrentUserProvider;
import gr.hua.dit.studyrooms.core.service.ReservationBusinessLogicService;
import gr.hua.dit.studyrooms.core.service.mapper.ReservationMapper;
import gr.hua.dit.studyrooms.core.service.model.CancelReservationRequest;
import gr.hua.dit.studyrooms.core.service.model.CreateReservationRequest;
import gr.hua.dit.studyrooms.core.service.model.ReservationView;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
/**
 * Default implementation of {@link ReservationBusinessLogicService}.
 */
@Service
public class ReservationBusinessLogicServiceImpl implements ReservationBusinessLogicService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReservationBusinessLogicServiceImpl.class);

    private static final int MAX_RESERVATIONS_PER_DAY = 4;
    private static final Set<ReservationStatus> ACTIVE = Set.of(ReservationStatus.ACTIVE);
    private final ReservationMapper reservationMapper;
    private final ReservationRepository reservationRepository;
    private final PersonRepository personRepository;
    private final StudyRoomRepository studyRoomRepository;
    private final CurrentUserProvider currentUserProvider;
    private final HolidayPort holidayPort;


    public ReservationBusinessLogicServiceImpl(
            final ReservationMapper reservationMapper,
            final ReservationRepository reservationRepository,
            final CurrentUserProvider currentUserProvider,
            PersonRepository personRepository,
            StudyRoomRepository studyRoomRepository,
            HolidayPort holidayPort
    ) {
        if (reservationMapper == null) throw new NullPointerException();
        if (reservationRepository == null) throw new NullPointerException();
        if (personRepository == null) throw new NullPointerException();
        if (currentUserProvider == null) throw new NullPointerException();
        if (studyRoomRepository == null) throw new NullPointerException();
        if (holidayPort == null) throw new NullPointerException();

        this.reservationMapper = reservationMapper;
        this.reservationRepository = reservationRepository;
        this.personRepository = personRepository;
        this.currentUserProvider = currentUserProvider;
        this.studyRoomRepository = studyRoomRepository;
        this.holidayPort = holidayPort;
    }

    @Override
    public List<ReservationView> getReservationHistoryOfCurrentUser() {

        final CurrentUser currentUser = currentUserProvider.requireCurrentUser();
        final List<Reservation> reservationListList;
        if (currentUser.type() != PersonType.STUDENT) {
            throw new SecurityException("unsupported PersonType");
        }

        long studentId = currentUser.id();

        reservationListList =
                reservationRepository.findByStudentIdOrderByDateDesc(studentId);

        return reservationListList.stream()
                .map(reservation -> {
                    if (reservation.getStudent().getId() != studentId) {
                        throw new SecurityException("User cannot access this reservation");
                    }
                    return reservationMapper.convertReservationToReservationView(reservation);
                })
                .toList();
    }

    @Override
    public List<ReservationView> getReservationsOfCurrentUser() {
        final CurrentUser currentUser = this.currentUserProvider.requireCurrentUser();
        long studentId = currentUser.id();
        final List<Reservation> reservationList;
        LocalDate today = LocalDate.now();
        if (currentUser.type() == PersonType.STUDENT) {
            reservationList = this.reservationRepository.findByStudentIdAndStatusAndDateGreaterThanEqualOrderByDateAsc(
                    studentId,
                    ReservationStatus.ACTIVE,
                    today
            );
        }else {
            throw new SecurityException("unsupported PersonType");
        }
        return reservationList.stream()
                .map(this.reservationMapper::convertReservationToReservationView)
                .toList();
    }

    @Transactional
    @Override
    public ReservationView createReservation(final CreateReservationRequest req) {
        if (req == null) throw new NullPointerException();

        // --------------------------------------------------
        // Security
        // --------------------------------------------------

        final CurrentUser currentUser = this.currentUserProvider.requireCurrentUser();
        if (currentUser.type() != PersonType.STUDENT) {
            throw new SecurityException("Student type/role required");
        }

        final long studentId = currentUser.id();
        final LocalDate date = req.date();

        if (date.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Reservation date must be today or later");
        }

        // --------------------------------------------------
        // Domain
        // --------------------------------------------------

        final Person student = this.personRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("student not found"));

        if (student.getType() != PersonType.STUDENT) {
            throw new IllegalArgumentException("studentId must refer to a STUDENT");
        }

        final StudyRoom room = this.studyRoomRepository.findById(req.roomId())
                .orElseThrow(() -> new IllegalArgumentException("room not found"));

        // --------------------------------------------------
        // External rule: holidays
        // --------------------------------------------------

        if (holidayPort.isHoliday(date)) {
            throw new RuntimeException("Reservations are not allowed on public holidays");
        }

        // --------------------------------------------------
        // Business rules
        // --------------------------------------------------

        // --------------------------------------------------
        // Penalty rule
        // --------------------------------------------------

        if (student.isUnderPenalty()) {
            throw new RuntimeException(
                    "You cannot make a reservation due to an active penalty"
            );
        }
        final long activeCount =
                this.reservationRepository.countByStudentIdAndDateAndStatusIn(
                        studentId, date, ACTIVE);

        // --------------------------------------------------
        // Student cannot have more than 4 active Reservations per day
        // --------------------------------------------------

        if (activeCount >= MAX_RESERVATIONS_PER_DAY) {
            throw new RuntimeException("Student has reached the daily reservation limit");
        }

        final List<Reservation> activeReservations =
                this.reservationRepository.findByRoomIdAndDateAndStatus(
                        room.getId(), date, ReservationStatus.ACTIVE);

        final int seatsBooked = activeReservations.stream()
                .mapToInt(Reservation::getSeatsReserved)
                .sum();

        if (seatsBooked + req.seatsRequested() > room.getCapacity()) {
            throw new RuntimeException("Not enough seats available");
        }

        if (room.getOpeningTime() == null || room.getClosingTime() == null) {
            throw new RuntimeException("Room has no valid opening/closing hours");
        }

        // --------------------------------------------------
        // Create
        // --------------------------------------------------

        Reservation reservation = new Reservation();
        reservation.setStudent(student);
        reservation.setRoom(room);
        reservation.setDate(date);
        reservation.setStartTime(room.getOpeningTime());
        reservation.setEndTime(room.getClosingTime());
        reservation.setSeatsReserved(req.seatsRequested());
        reservation.setStatus(ReservationStatus.ACTIVE);

        reservation = this.reservationRepository.save(reservation);

        return this.reservationMapper.convertReservationToReservationView(reservation);
    }



    @Override
    public ReservationView cancelReservation(CancelReservationRequest req) {
        if (req == null) throw new NullPointerException();

        final long reservationid = req.reservationId();

        final Reservation reservation = this.reservationRepository.findById(reservationid)
                .orElseThrow(() -> new IllegalArgumentException("Reservation does not exist"));

        final long studentId = reservation.getStudent().getId();
        final CurrentUser currentUser = this.currentUserProvider.requireCurrentUser();

        if (currentUser.type() != PersonType.STUDENT) {
            throw new SecurityException("Student role/type required");
        }
        if (currentUser.id() != studentId) {
            throw new SecurityException("Authenticated student does not match the ticket's studentId");
        }
        if (reservation.getStatus() != ReservationStatus.ACTIVE) {
            throw new IllegalArgumentException("Only ACTIVE reservations can be cancelled");
        }
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservation.setCancelledAt(Instant.now());
        final Reservation savedReservation = this.reservationRepository.save(reservation);
        final ReservationView reservationView = this.reservationMapper.convertReservationToReservationView(savedReservation);

        return reservationView;
    }
    @Override
    public List<ReservationView> getAllActiveReservationsForStaff() {

        final CurrentUser currentUser = currentUserProvider.requireCurrentUser();
        if (currentUser.type() != PersonType.STAFF) {
            throw new SecurityException("Staff required");
        }

        List<Reservation> reservations =
                reservationRepository.findByStatusOrderByDateAsc(ReservationStatus.ACTIVE);

        return reservations.stream()
                .map(reservationMapper::convertReservationToReservationView)
                .toList();
    }

    @Override
    public ReservationView cancelReservationByStaff(CancelReservationRequest req) {
        if (req == null) throw new NullPointerException();

        final CurrentUser currentUser = this.currentUserProvider.requireCurrentUser();


        if (currentUser.type() != PersonType.STAFF) {
            throw new SecurityException("Staff role/type required");
        }

        final long reservationId = req.reservationId();

        final Reservation reservation = this.reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation does not exist"));

        // Επιτρέπουμε ακύρωση μόνο ACTIVE κρατήσεων
        if (reservation.getStatus() != ReservationStatus.ACTIVE) {
            throw new IllegalArgumentException("Only ACTIVE reservations can be cancelled by staff");
        }

        reservation.setStatus(ReservationStatus.CANCELLED_BY_STAFF);
        reservation.setCancelledAt(Instant.now());

        final Reservation savedReservation = this.reservationRepository.save(reservation);
        final ReservationView reservationView = this.reservationMapper.convertReservationToReservationView(savedReservation);

        return reservationView;
    }
}
