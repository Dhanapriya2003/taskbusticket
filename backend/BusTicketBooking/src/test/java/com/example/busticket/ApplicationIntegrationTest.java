package com.example.busticket;

import com.example.busticket.model.Passenger;
import com.example.busticket.model.Bus;
import com.example.busticket.model.Booking;
import com.example.busticket.model.Seat;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ApplicationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private static String passengerId;
    private static String busId;
    private static String bookingId;
    
    @Test
    @Order(1)
    public void testPassengerRegistration() throws Exception {
        Passenger passenger = new Passenger("Alice Smith", "alice@example.com", "9876543210", "alice123");
        String response = mockMvc.perform(post("/api/passengers/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(passenger)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andReturn().getResponse().getContentAsString();
        Passenger createdPassenger = objectMapper.readValue(response, Passenger.class);
        passengerId = createdPassenger.getId();
    }
    
    @Test
    @Order(2)
    public void testPassengerLogin() throws Exception {
        Passenger loginRequest = new Passenger();
        loginRequest.setEmail("alice@example.com");
        loginRequest.setPassword("alice123");
        mockMvc.perform(post("/api/passengers/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(passengerId));
    }
    
    @Test
    @Order(3)
    public void testBusInsertionAndSearch() throws Exception {
        Bus bus = new Bus();
        bus.setBusNumber("RB1001");
        bus.setFrom("Bangalore");
        bus.setTo("Chennai");
        bus.setDepartureTime("2025-05-01T06:30:00");
        bus.setArrivalTime("2025-05-01T12:30:00");
        bus.setPrice(550);
        bus.setAvailableSeats(40);
        bus.setSeats(Arrays.asList(
            new Seat("A1", "available"),
            new Seat("A2", "available"),
            new Seat("A3", "available"),
            new Seat("B1", "available"),
            new Seat("B2", "available")
        ));
        
        // Create a new bus via BusController
        String busResponse = mockMvc.perform(post("/api/buses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bus)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        Bus createdBus = objectMapper.readValue(busResponse, Bus.class);
        busId = createdBus.getId();
        
        // Verify search endpoint
        mockMvc.perform(get("/api/buses/search")
                .param("from", "Bangalore")
                .param("to", "Chennai"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].busNumber").value("RB1001"));
    }
    
    @Test
    @Order(4)
    public void testBookingCreation() throws Exception {
        // Create a booking using the registered passenger and inserted bus
        Booking booking = new Booking();
        booking.setPassengerId(passengerId);
        booking.setBusId(busId);
        booking.setSeatNumbers(Arrays.asList("A1", "B1"));
        booking.setBookingDate(LocalDateTime.now().plusDays(1).toString());
        booking.setStatus("CONFIRMED");
        booking.setBoardingPoint("Main Bus Stand, Bangalore");
        booking.setDropPoint("Chennai Central");
        
        String bookingResponse = mockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(booking)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andReturn().getResponse().getContentAsString();
        Booking createdBooking = objectMapper.readValue(bookingResponse, Booking.class);
        bookingId = createdBooking.getId();
    }
    
    @Test
    @Order(5)
    public void testBookingHistory() throws Exception {
        mockMvc.perform(get("/api/bookings/passenger/" + passengerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
