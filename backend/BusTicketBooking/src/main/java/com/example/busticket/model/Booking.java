package com.example.busticket.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Document(collection = "bookings")
public class Booking {
    @Id
    private String id;
    private String passengerId;
    private String busId;
    private List<String> seatNumbers; // multiple seats can be booked
    private String bookingDate;
    private String status; // e.g., "CONFIRMED"
    private String boardingPoint;
    private String dropPoint;
    
    // Existing bus details fields
    private String busNumber;
    private String departureTime;
    private String arrivalTime;
    
    // NEW: Passenger details embedded in the booking
    private List<PassengerDetail> passengerDetails;

    public Booking() {}

    public Booking(String passengerId, String busId, List<String> seatNumbers,
                   String bookingDate, String status, String boardingPoint, String dropPoint,
                   String busNumber, String departureTime, String arrivalTime,
                   List<PassengerDetail> passengerDetails) {
        this.passengerId = passengerId;
        this.busId = busId;
        this.seatNumbers = seatNumbers;
        this.bookingDate = bookingDate;
        this.status = status;
        this.boardingPoint = boardingPoint;
        this.dropPoint = dropPoint;
        this.busNumber = busNumber;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.passengerDetails = passengerDetails;
    }

    // Getters and setters for all fields

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getPassengerId() {
        return passengerId;
    }
    public void setPassengerId(String passengerId) {
        this.passengerId = passengerId;
    }
    public String getBusId() {
        return busId;
    }
    public void setBusId(String busId) {
        this.busId = busId;
    }
    public List<String> getSeatNumbers() {
        return seatNumbers;
    }
    public void setSeatNumbers(List<String> seatNumbers) {
        this.seatNumbers = seatNumbers;
    }
    public String getBookingDate() {
        return bookingDate;
    }
    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getBoardingPoint() {
        return boardingPoint;
    }
    public void setBoardingPoint(String boardingPoint) {
        this.boardingPoint = boardingPoint;
    }
    public String getDropPoint() {
        return dropPoint;
    }
    public void setDropPoint(String dropPoint) {
        this.dropPoint = dropPoint;
    }
    public String getBusNumber() {
        return busNumber;
    }
    public void setBusNumber(String busNumber) {
        this.busNumber = busNumber;
    }
    public String getDepartureTime() {
        return departureTime;
    }
    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }
    public String getArrivalTime() {
        return arrivalTime;
    }
    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }
    public List<PassengerDetail> getPassengerDetails() {
        return passengerDetails;
    }
    public void setPassengerDetails(List<PassengerDetail> passengerDetails) {
        this.passengerDetails = passengerDetails;
    }
}
