package com.example.busticket.model;

public class Seat {
    private String seatId;   // e.g., "A1", "B2"
    private String status;   // "available", "unavailable", "female", etc.

    public Seat() {}

    public Seat(String seatId, String status) {
        this.seatId = seatId;
        this.status = status;
    }

    public String getSeatId() { return seatId; }
    public void setSeatId(String seatId) { this.seatId = seatId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
