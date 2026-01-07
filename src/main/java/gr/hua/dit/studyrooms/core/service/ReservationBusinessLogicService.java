package gr.hua.dit.studyrooms.core.service;

import gr.hua.dit.studyrooms.core.service.model.CancelReservationRequest;
import gr.hua.dit.studyrooms.core.service.model.CreateReservationRequest;
import gr.hua.dit.studyrooms.core.service.model.ReservationView;

import java.util.List;

public interface ReservationBusinessLogicService {

    List<ReservationView> getReservationsOfCurrentUser();
    ReservationView createReservation(CreateReservationRequest req);
    ReservationView cancelReservation(CancelReservationRequest req);
    ReservationView cancelReservationByStaff(CancelReservationRequest req);
    List<ReservationView> getReservationHistoryOfCurrentUser();
    List<ReservationView> getAllActiveReservationsForStaff();
}