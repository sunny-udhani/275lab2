package com.minisocial.book.entity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

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

    private Date departureTime;
    private Date arrivalTime;
    private int seatsLeft;
    private String description;

    @Embedded
    private Plane plane;  // Embedded

    //    @JsonIgnore
    @ManyToMany(mappedBy = "flights")
    private List<Passenger> passengers;

    //    @JsonIgnore
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

    public String getDepartureTime() {
        return departureTime.toString();
    }

    public void setDepartureTime(Date departureTime) {
        this.departureTime = departureTime;
    }

    public String getArrivalTime() {
        return arrivalTime.toString();
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

    /**
     * Flight Data as JSONObject excluding Passengers
     *
     * @return JSONObject
     * @throws JSONException 
     */
    public JSONObject getJSON() throws JSONException {
        JSONObject flightJson = new JSONObject();
        flightJson.put("number", this.getFlightNumber());
        flightJson.put("price", this.getPrice());
        flightJson.put("from", this.getOrigin());
        flightJson.put("to", this.getTo());
        flightJson.put("departureTime", this.getDepartureTime());
        flightJson.put("arrivalTime", this.getArrivalTime());
        flightJson.put("seatsLeft", this.getSeatsLeft());
        flightJson.put("description", this.getDescription());
        flightJson.put("plane", this.getPlane().getJSON());
        return flightJson;
    }

    /**
     * Flight Data as JSONObject inclusive of all Passengers details
     *
     * @return JSONObject
     * @throws JSONException 
     */
    public JSONObject getFullJson() throws JSONException {
        JSONObject resultObject = new JSONObject();
        JSONObject flight = this.getJSON();
        JSONObject passengers = new JSONObject();
        JSONArray passengerArray = new JSONArray();

        if (this.getPassengers() != null && !this.getPassengers().isEmpty()) {
            for (Passenger passenger : this.getPassengers()) {
                JSONObject pass = passenger.getJSON();
                passengerArray.put(pass);
            }
        }
        passengers.put("passenger", passengerArray);
        flight.put("passengers", passengers);
        resultObject.put("flight", flight);
        return resultObject;
    }

    /**
     * XML representation of flight JSONObject
     *
     * @return String
     * @throws JSONException 
     */
    public String getXML() throws JSONException {
        return XML.toString(this.getFullJson());
    }

}
