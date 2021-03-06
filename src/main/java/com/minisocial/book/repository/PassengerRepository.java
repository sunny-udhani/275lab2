package com.minisocial.book.repository;

import com.minisocial.book.entity.Passenger;
import org.springframework.data.repository.CrudRepository;

public interface PassengerRepository  extends CrudRepository<Passenger, String> {
    Passenger findByIdEquals(String id);
//   Passenger findOne(String id);
    void deleteById(String id);
	//Passenger findOne(String passengerId);
}