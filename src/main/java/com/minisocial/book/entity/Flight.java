package com.minisocial.book.entity;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
public class Flight {
    @Id
    @Column(name = "flight_id")
    private String flightNumber; // Each flight has a unique flight number.
    private double price;
    private String origin;
    @Column(name = "destination")
    private String to;

    /*  Date format: yy-mm-dd-hh, do not include minutes and seconds.
     ** Example: 2018-03-22-19
     ** The system only needs to supports PST. You can ignore other time zones.
     */
    private Date departureTime;
    private Date arrivalTime;
    private int seatsLeft;
    private String description;

    @Embedded
    private Plane plane;  // Embedded

    @OneToMany(mappedBy = "flight")
    private List<Passenger> passengers;

    @ManyToMany(mappedBy = "flights")
    private List<Reservation> reservation;

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Date getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(Date departureTime) {
        this.departureTime = departureTime;
    }

    public Date getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(Date arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public int getSeatsLeft() {
        return seatsLeft;
    }

    public void setSeatsLeft(int seatsLeft) {
        this.seatsLeft = seatsLeft;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Plane getPlane() {
        return plane;
    }

    public void setPlane(Plane plane) {
        this.plane = plane;
    }

    public List<Passenger> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<Passenger> passengers) {
        this.passengers = passengers;
    }

    public List<Reservation> getReservation() {
        return reservation;
    }

    public void setReservation(List<Reservation> reservation) {
        this.reservation = reservation;
    }

}