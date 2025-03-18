package com.example.busticket.repository;

import com.example.busticket.model.Passenger;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PassengerRepository extends MongoRepository<Passenger, String> {
    Passenger findByEmail(String email);
}
