package com.minisocial.book.service;

import javax.transaction.Transactional;

import com.minisocial.book.controller.ReservationController;
import com.minisocial.book.entity.Message;
import com.minisocial.book.entity.Reservation;
import com.minisocial.book.repository.ReservationRepository;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.minisocial.book.entity.Flight;
import com.minisocial.book.entity.Passenger;
import com.minisocial.book.repository.FlightRepository;
import com.minisocial.book.repository.PassengerRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class FlightService {

    @Autowired
    private FlightRepository flightRepository;
    @Autowired
    private PassengerRepository passengerRepository;
    @Autowired
    private ReservationRepository reservationRepository;

    public String objToJson(Flight flight) {
        String jsonInString = null;
        try {
            ObjectWriter writer = new ObjectMapper().writer();

            jsonInString = writer.writeValueAsString(flight);
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
            return null;
        }
//	        System.out.println("result");
//	        System.out.println(jsonInString);
        JSONObject jsonobj = null;
        try {
            jsonobj = new JSONObject().put("flight", new JSONObject(jsonInString));
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("result : " + jsonobj.toString());
        return jsonobj.toString();
    }

    public String objToXML(Flight flight) {

        try {
            return XML.toString(flight.getFullJson());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }

    }

    public String getFlight(String id) {
        if (flightRepository.existsById(id)) {
            Flight p = flightRepository.findByFlightNumberEquals(id);
            return p.getFullJson().toString();
        } else
            return new Message("Sorry, the requested flight with number " + id + " does not exist", "404").getMessageJSON().toString();
    }


    public String getFlight(String id, MediaType media) {
        if (flightRepository.existsById(id)) {
            Flight p = flightRepository.findByFlightNumberEquals(id);
            return (media == MediaType.APPLICATION_XML) ? p.getXML() : objToJson(p);
        } else
            return (media == MediaType.APPLICATION_XML) ? new Message("Sorry, the requested flight with number " + id + " does not exist", "404").getXML() : new Message("Sorry, the requested flight with number " + id + " does not exist", "404").getMessageJSON().toString();
    }

//    @Transactional
    public String createFlight(Flight flight) {

        try {
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH");
//            Date depTime = sdf.parse(flight.getDepartureTime());
//            Date arrTime = sdf.parse(flight.getArrivalTime());
//
            System.out.println(flight.getFlightNumber());
            if (!flightRepository.existsById(flight.getFlightNumber())) {
                flightRepository.save(flight);
                return flight.getXML();
            } else {
                Flight existingFlight = flightRepository.findByFlightNumberEquals(flight.getFlightNumber());
                if (!checkFlightCapacity(existingFlight, flight.getPlane().getCapacity())) {
                    Message capacityMessage = new Message("Cannot reduce capacity,active reservation count for this flight is higher than the target capacity ", "400");
                    return capacityMessage.getMessageJSON().toString();
                }
                List<Passenger> passengers = existingFlight.getPassengers();
                for (Passenger passenger : passengers) {
                    List<Flight> temp = new ArrayList<>();
                    temp.add(flight);
                    if (ReservationService.checkOverlap(passenger, temp)) {
                        Message error = new Message("Flight overlap occurred", "400");
                        return error.getMessageJSON().toString();
                    }
                    List<Flight> passengerFlights = passenger.getFlights();
                    passengerFlights.remove(existingFlight);
                    passengerFlights.add(flight);
                    passenger.setFlights(passengerFlights);
                    passengerRepository.save(passenger);
                }
                List<Reservation> reservations = existingFlight.getReservation();
                for (Reservation reservation : reservations) {
                    List<Flight> reservedFlights = reservation.getFlights();
                    reservedFlights.remove(existingFlight);
                    reservedFlights.add(flight);
                    reservation.setFlights(reservedFlights);
                    reservationRepository.save(reservation);
                }
                int oldPlaneCap = existingFlight.getPlane().getCapacity();
                int newPlaneCap = flight.getPlane().getCapacity();
                int changeInCapacity = Math.abs(oldPlaneCap - newPlaneCap);
                if (oldPlaneCap > newPlaneCap)
                    flight.setSeatsLeft(existingFlight.getSeatsLeft() - changeInCapacity);
                else
                    flight.setSeatsLeft(existingFlight.getSeatsLeft() + changeInCapacity);
                flightRepository.save(flight);
                return flight.getXML();

            }
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        }
    }


    @Transactional
    public String deleteFlight(String id) throws JSONException {
        // TODO Auto-generated method stub

        Message message = new Message("", "404");
        if (!flightRepository.existsById(id)) {
            message.setMessage("Flight does not exist");
            return message.getMessageJSON().toString();
        } else {
            Flight flight = flightRepository.findByFlightNumberEquals(id);
            if (flight.getReservation().size() > 0) {
                message.setMessage("Cannot delete flight, has one or more reservations");
                message.setCode("400");
                return message.getMessageJSON().toString();
            } else {
                message.setCode("200");
                message.setMessage("Flight with number " + id + " is deleted successfully ");
                flightRepository.deleteByFlightNumberEquals(id);
                return message.getMessageJSON().toString();
            }
        }

    }

    private boolean checkFlightCapacity(Flight flight, int newCap) {
        int currCap = flight.getPlane().getCapacity();

        if (currCap < newCap)
            return true;
        else {
            if (newCap < (currCap - flight.getSeatsLeft()))
                return false;
        }
        return true;
    }
}