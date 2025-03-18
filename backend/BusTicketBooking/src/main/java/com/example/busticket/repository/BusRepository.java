package com.example.busticket.repository;

import com.example.busticket.model.Bus;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface BusRepository extends MongoRepository<Bus, String> {
    List<Bus> findByFromAndTo(String from, String to);
}
