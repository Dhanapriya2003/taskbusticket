package com.example.busticket.controller;

import com.example.busticket.model.Bus;
import com.example.busticket.repository.BusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/busticket/buses")
@CrossOrigin(origins = "*")
public class BusController {

    @Autowired
    private BusRepository busRepository;

    @GetMapping
    public List<Bus> getAllBuses() {
        return busRepository.findAll();
    }

    @GetMapping("/search")
    public List<Bus> searchBuses(@RequestParam("from") String from, @RequestParam("to") String to) {
        return busRepository.findByFromAndTo(from, to);
    }

    @GetMapping("/{busId}")
    public Bus getBus(@PathVariable String busId) {
        return busRepository.findById(busId).orElse(null);
    }
}
