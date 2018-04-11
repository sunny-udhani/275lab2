package com.minisocial.book.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

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

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "passenger_id")
    private Passenger passenger;
    private double price; // sum of each flight’s price.

    @ManyToMany(fetch = FetchType.LAZY)
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

    /**
     * JSON representation of Reservation inclusive of Flight details
     *
     * @return JSONObject
     * @throws JSONException 
     */
    public JSONObject getFullJSON() throws JSONException {
        JSONObject result = new JSONObject();
        JSONObject reserv = new JSONObject();
        reserv.put("orderNumber", this.getReservationNumber());
        reserv.put("price", this.getPrice());
        JSONArray flights = new JSONArray();
        for (Flight flight : this.getFlights()) {
            flights.put(flight.getJSON());
        }
        reserv.put("flights", new JSONObject().put("flight", flights));
        reserv.put("passenger", this.getPassenger().getJSON());
        result.put("reservation", reserv);
        return result;
    }

    /**
     * XML representation of Reservation JSONObject
     *
     * @return String
     * @throws JSONException 
     */
    public String getXML() throws JSONException {
        return XML.toString(getFullJSON());
    }
}
