package com.example.busticket.controller;

import com.example.busticket.model.Passenger;
import com.example.busticket.repository.PassengerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/busticket/passengers")
@CrossOrigin(origins = "*")
public class PassengerController {

    @Autowired
    private PassengerRepository passengerRepository;

    // @PostMapping("/register")
    // public Passenger registerPassenger(@Valid @RequestBody Passenger passenger) {
    //     return passengerRepository.save(passenger);
    // }
    @PostMapping("/register")
    public ResponseEntity<?> registerPassenger(@Valid @RequestBody Passenger passenger) {
        // Check if a passenger with the same email already exists
        Passenger existingPassenger = passengerRepository.findByEmail(passenger.getEmail());
        if (existingPassenger != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists");
        }
        // If no passenger exists with the given email, create a new one
        Passenger newPassenger = passengerRepository.save(passenger);
        return ResponseEntity.status(HttpStatus.CREATED).body(newPassenger);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginPassenger(@RequestBody Passenger loginRequest) {
        if (loginRequest == null || loginRequest.getEmail() == null || loginRequest.getPassword() == null) {
            return ResponseEntity.badRequest().body("Email and password must be provided.");
        }
        Passenger passenger = passengerRepository.findByEmail(loginRequest.getEmail());
        if (passenger != null && passenger.getPassword().equals(loginRequest.getPassword())) {
            return ResponseEntity.ok(passenger);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }

    @GetMapping("/{id}")
    public Passenger getPassenger(@PathVariable String id) {
        return passengerRepository.findById(id).orElse(null);
    }

    @PutMapping("/{id}")
    public Passenger updatePassenger(@PathVariable String id, @RequestBody Passenger updatedPassenger) {
        updatedPassenger.setId(id);
        return passengerRepository.save(updatedPassenger);
    }
}
