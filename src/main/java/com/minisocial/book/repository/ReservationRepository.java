package com.minisocial.book.repository;

import com.minisocial.book.entity.Passenger;
import com.minisocial.book.entity.Reservation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReservationRepository extends CrudRepository<Reservation, String> {

    Reservation findByReservationNumberEquals(String id);
    @Query("select r from Reservation r where r.passenger.id=(:passengerId)")
    List<Reservation> findByPassengerId(@Param("passengerId")String passengerId);

    @Query("select r from Reservation r inner join r.flights f where f.flightNumber=(:flightNumber)")
    List<Reservation> findByFlightNumber(@Param("flightNumber") String flightNumber);

    @Query("select r from Reservation r inner join r.flights f where f.origin=(:source)")
    List<Reservation> findBySource(@Param("source") String source);

    @Query("select r from Reservation r inner join r.flights f where f.to=(:destination)")
    List<Reservation> findByDestination(@Param("destination") String destination);

    @Query("select r from Reservation r inner join r.flights f where f.to=(:destination)" +
            " and f.origin=(:source) and f.flightNumber=(:flightNumber) and r.passenger.id=(:passengerId)")
    List<Reservation> findByAllParam(@Param("flightNumber") String flightNumber,
                                     @Param("destination") String destination,
                                     @Param("source") String source ,
                                     @Param("passengerId")String passengerId );

    @Query("select r from Reservation r inner join r.flights f where f.flightNumber=(:flightNumber) and r.passenger.id=(:passengerId)")
    List<Reservation> findByPassengerFlight(@Param("passengerId")String passengerId,@Param("flightNumber")String flightNumber);

    @Query("select r from Reservation r inner join r.flights f where f.origin=(:source)and r.passenger.id=(:passengerId)")
    List<Reservation> findByPassengerSource(@Param("passengerId")String passengerId,@Param("source")String source);

    @Query("select r from Reservation r inner join r.flights f where f.to=(:destination)and r.passenger.id=(:passengerId)")
    List<Reservation> findByPassengerDestination(@Param("passengerId")String passengerId,@Param("destination")String destination);

    @Query("select r from Reservation r inner join r.flights f where f.origin=(:source) and f.to=(:destination)")
    List<Reservation> findBySourceDestination(@Param("source")String source,@Param("destination")String destination);

    @Query("select r from Reservation r inner join r.flights f where f.origin=(:source) and f.flightNumber=(:flightNumber)")
    List<Reservation> findBySourceFlight(@Param("source")String source,@Param("flightNumber")String flightNumber);

    @Query("select r from Reservation r inner join r.flights f where f.to=(:destination) and f.flightNumber=(:flightNumber)")
    List<Reservation> findByDestinationFlight(@Param("destination")String destination,@Param("flightNumber")String flightNumber);

    @Query("select r from Reservation r inner join r.flights f where r.passenger.id=(:passengerId) and f.to=(:destination) and f.flightNumber=(:flightNumber)")
    List<Reservation> findByPassengerDestinationFlight(@Param("passengerId")String passengerId,@Param("destination")String destination,@Param("flightNumber")String flightNumber);

    @Query("select r from Reservation r inner join r.flights f where r.passenger.id=(:passengerId) and f.origin=(:source) and f.flightNumber=(:flightNumber)")
    List<Reservation> findByPassengerSourceFlight(@Param("passengerId")String passengerId,@Param("source")String source,@Param("flightNumber")String flightNumber);

    @Query("select r from Reservation r inner join r.flights f where r.passenger.id=(:passengerId) and f.origin=(:source) and f.to=(:destination)")
    List<Reservation> findByPassengerSourceDestination(@Param("passengerId")String passengerId,@Param("source")String source,@Param("destination")String destination);

    @Query("select r from Reservation r inner join r.flights f where f.flightNumber=(:flightNumber) and f.origin=(:source) and f.to=(:destination)")
    List<Reservation> findByFlightSourceDestination(@Param("flightNumber")String flightNumber,@Param("source")String source,@Param("destination")String destination);

}
