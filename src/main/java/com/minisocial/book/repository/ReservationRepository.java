package com.minisocial.book.repository;

import com.minisocial.book.entity.Passenger;
import com.minisocial.book.entity.Reservation;
import org.springframework.data.repository.CrudRepository;

public interface ReservationRepository extends CrudRepository<Reservation, String> {

    Reservation findByIdEquals(String id);
}
