package com.minisocial.book.repository;

import com.minisocial.book.entity.Passenger;
import org.springframework.data.repository.CrudRepository;

public interface PassengerRepository  extends CrudRepository<Passenger, Long> {
    Passenger findById(int id);
}