package gr.hua.dit.studyrooms.core.service.model;

public record RoomOccupancyView(
        long roomId,
        String roomName,
        int capacity,
        int booked,
        int available
) {}
