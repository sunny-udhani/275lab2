package com.minisocial.book.repository;

import org.springframework.data.repository.CrudRepository;

import com.minisocial.book.entity.Flight;
public interface FlightRepository  extends CrudRepository<Flight, String> {
    Flight findByFlightNumberEquals(String id);
   
}
