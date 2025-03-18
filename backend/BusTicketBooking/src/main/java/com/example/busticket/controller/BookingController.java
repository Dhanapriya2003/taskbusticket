package com.example.busticket.controller;

import com.example.busticket.model.Booking;
import com.example.busticket.model.Bus;
import com.example.busticket.model.Seat;
import com.example.busticket.repository.BookingRepository;
import com.example.busticket.repository.BusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/busticket/bookings")
@CrossOrigin(origins = "*")
public class BookingController {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BusRepository busRepository;

    @PostMapping
    public Booking createBooking(@RequestBody Booking booking) {
        // Save booking first
        Booking savedBooking = bookingRepository.save(booking);

        // Update bus seat statuses if seatNumbers are provided and embed bus details
        if (booking.getSeatNumbers() != null && !booking.getSeatNumbers().isEmpty()) {
            Optional<Bus> busOpt = busRepository.findById(booking.getBusId());
            if (busOpt.isPresent()) {
                Bus bus = busOpt.get();
                List<Seat> seats = bus.getSeats();
                for (String seatNumber : booking.getSeatNumbers()) {
                    for (Seat seat : seats) {
                        if (seat.getSeatId().equals(seatNumber)) {
                            seat.setStatus("unavailable");
                            break;
                        }
                    }
                }
                bus.setAvailableSeats(bus.getAvailableSeats() - booking.getSeatNumbers().size());
                busRepository.save(bus);
                
                // Embed bus details into the booking
                savedBooking.setBusNumber(bus.getBusNumber());
                savedBooking.setDepartureTime(bus.getDepartureTime());
                savedBooking.setArrivalTime(bus.getArrivalTime());
                
                // Save the booking again to update it with bus details
                savedBooking = bookingRepository.save(savedBooking);
            }
        }
        return savedBooking;
    }

    @GetMapping("/passenger/{passengerId}")
    public List<Booking> getBookingsByPassenger(@PathVariable String passengerId) {
        return bookingRepository.findByPassengerId(passengerId);
    }
    
    // Retrieve booking details by booking ID
    @GetMapping("/{bookingId}")
    public Booking getBookingById(@PathVariable String bookingId) {
        return bookingRepository.findById(bookingId).orElse(null);
    }
}
