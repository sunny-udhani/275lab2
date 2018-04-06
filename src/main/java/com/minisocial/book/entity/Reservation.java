package com.minisocial.book.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.annotation.Generated;
import javax.persistence.*;
import java.util.List;

@Entity
public class Reservation {
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid",
            strategy = "uuid")
    @Column(name = "reservation_id")
    private String reservationNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "passenger_id")
    private Passenger passenger;
    private double price; // sum of each flight’s price.

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "reservation_flights",
            joinColumns = @JoinColumn(name = "reservation_id", referencedColumnName = "reservation_id"),
            inverseJoinColumns = @JoinColumn(name = "flight_id", referencedColumnName = "flight_id"))
    private List<Flight> flights;

    public String getReservationNumber() {
        return reservationNumber;
    }

    public void setReservationNumber(String reservationNumber) {
        this.reservationNumber = reservationNumber;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public List<Flight> getFlights() {
        return flights;
    }

    public void setFlights(List<Flight> flights) {
        this.flights = flights;
    }
}
