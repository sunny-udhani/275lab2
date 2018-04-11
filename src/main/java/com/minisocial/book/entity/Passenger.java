package com.minisocial.book.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import javax.persistence.*;
import java.util.List;

//@XmlRootElement
@Entity
public class Passenger {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid",
            strategy = "uuid")
    private String id;
    private String firstname;
    private String lastname;
    private int age;
    private String gender;

    @Column(unique = true)
    private String phone; // Phone numbers must be unique

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "passenger", cascade = CascadeType.REMOVE)
    private List<Reservation> reservations;

    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "passenger_flights",
            joinColumns = @JoinColumn(name = "passenger_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "flight_number", referencedColumnName = "flight_id"))
    private List<Flight> flights;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public List<Flight> getFlights() {
        return flights;
    }

    public void setFlights(List<Flight> flights) {
        this.flights = flights;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    /**
     * Passenger data inclusive of Reservation details
     *
     * @return JSONObject
     * @throws JSONException 
     */
    public JSONObject getFullJSON() throws JSONException {
        JSONObject result = new JSONObject();
        JSONObject passenger = this.getJSON();
        JSONArray reservationArray = new JSONArray();
        if ( this.getReservations() != null && !this.getReservations().isEmpty()) {
            for (Reservation res : this.getReservations()) {
                JSONObject reservation = res.getFullJSON();
                JSONObject reserv = reservation.getJSONObject("reservation");
                reserv.remove("passenger");
                reservation.put("reservation", reserv);
                reservationArray.put(reservation);
            }
        }
        passenger.put("reservations", reservationArray);
        result.put("passenger", passenger);
        return result;
    }

    /**
     * Passenger data as XML
     *
     * @return String
     * @throws JSONException 
     */
    public String getXML() throws JSONException {
        return XML.toString(this.getFullJSON());
    }

    /**
     * Passenger data excluding Reservation details
     *
     * @return JSONObject
     * @throws JSONException 
     */
    public JSONObject getJSON() throws JSONException {
        JSONObject passenger = new JSONObject();
        passenger.put("id", this.getId());
        passenger.put("firstname", this.getFirstname());
        passenger.put("lastname", this.getLastname());
        passenger.put("age", this.getAge());
        passenger.put("gender", this.getGender());
        passenger.put("phone", this.getPhone());
        return passenger;
    }
}
