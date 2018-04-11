package com.minisocial.book.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.minisocial.book.entity.Message;
import com.minisocial.book.entity.Reservation;
import com.minisocial.book.repository.ReservationRepository;
import com.minisocial.book.service.ReservationService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.Produces;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(path = "/reservation")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;
    @Autowired
    private ReservationRepository reservationRepository;

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Produces(MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    ResponseEntity<?> getReservation(@PathVariable("id") String id) throws JSONException {
        // This returns a JSON with the users
        String p = reservationService.getReservationById(id);
        System.out.println(reservationService.getReservationById(id));

        return new ResponseEntity<Object>(p, HttpStatus.OK);

    }

    @GetMapping(path = "/{id}", params = "xml", produces = MediaType.APPLICATION_XML_VALUE)
//    @Consumes(MediaType.ALL_VALUE)
    @Produces(MediaType.APPLICATION_XML_VALUE)
    public @ResponseBody
    ResponseEntity<?> getReservationXML(@PathVariable("id") String id, @RequestParam Map<String, String> params) throws JSONException {
        // This returns a XML/JSON based on contentconfig.
        String resp = reservationService.getReservationById(id, MediaType.APPLICATION_XML);
        return new ResponseEntity<Object>(resp, HttpStatus.OK);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    ResponseEntity<?> createReservation(@RequestParam Map<String, String> params) throws JsonProcessingException, JSONException, ParseException {

        String passengerId = params.get("passengerId");
        String flightList = params.get("flightLists");

        String[] flightLists = flightList.split(",");
        List<String> flightListss = Arrays.asList(flightLists);
        String resp = reservationService.createReservation(passengerId, flightListss);
        return new ResponseEntity<Object>(resp, HttpStatus.OK);
    }

    @PostMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    ResponseEntity<?> updateReservation(@PathVariable("id") String id, @RequestParam(required = false) List<String> flightsAdded,
                                        @RequestParam(required = false) List<String> flightsRemoved) throws JSONException {
        // This returns a XML/JSON based on contentconfig.

        ResponseEntity<Object> response = reservationService.updateReservation(id, flightsAdded, flightsRemoved);
        return response;
    }

    @DeleteMapping(path = "/{id}", produces = MediaType.APPLICATION_XML_VALUE)
    public @ResponseBody
    ResponseEntity<?> deleteReservation(@PathVariable("id") String id) throws JSONException {
        Message success = new Message("Reservation with number " + id + " is canceled successfully", "200");
        Message error = new Message("Reservation with number " + id + " does not exist", "404");

        if (!reservationService.deleteReservation(id))
            return new ResponseEntity<Object>(error.getMessageJSON().toString(), HttpStatus.OK);

        return new ResponseEntity<Object>(success.getXML(), HttpStatus.OK);
    }


    /**
     * Search for existing reservation based on any combination of PassengerId,from,to,FlightNumber
     *
     * @param passengerId  PassengerId
     * @param from         Source
     * @param to           Destination
     * @param flightNumber Flight Number
     * @throws JSONException 
     */
    @GetMapping(produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<Object> searchReservationByAllParam(@RequestParam(required = false) String passengerId,
                                                              @RequestParam(required = false) String from,
                                                              @RequestParam(required = false) String to,
                                                              @RequestParam(required = false) String flightNumber) throws JSONException {
        List<Reservation> reservations = null;
        JSONObject result = new JSONObject();
        JSONArray reservationArray = new JSONArray();

        boolean passengerIdExists = passengerId.isEmpty();
        boolean toExists = to.isEmpty();
        boolean fromExists = from.isEmpty();
        boolean flightNumberExists = flightNumber.isEmpty();
        if (!passengerIdExists && !toExists && !fromExists && !flightNumberExists) {
            Message message = new Message("Please pass valid search params", "400");
            return new ResponseEntity<Object>(message.getXML(), HttpStatus.BAD_REQUEST);
        } else {
            if (passengerIdExists && fromExists && toExists && flightNumberExists)
                reservations = reservationRepository.findByAllParam(flightNumber, to, from, passengerId);
            else if (passengerIdExists) {
                if (toExists && fromExists)
                    reservations = reservationRepository.findByPassengerSourceDestination(passengerId, from, to);
                else if (toExists && flightNumberExists)
                    reservations = reservationRepository.findByPassengerDestinationFlight(passengerId, to, flightNumber);
                else if (fromExists && flightNumberExists) {
                    System.out.println(passengerId);
                    System.out.println(from);
                    System.out.println(flightNumber);
                    reservations = reservationRepository.findByPassengerSourceFlight(passengerId, from, flightNumber);
                } else if (toExists)
                    reservations = reservationRepository.findByPassengerDestination(passengerId, to);
                else if (fromExists)
                    reservations = reservationRepository.findByPassengerSource(passengerId, from);
                else if (flightNumberExists)
                    reservations = reservationRepository.findByPassengerFlight(passengerId, flightNumber);
                else
                    reservations = reservationRepository.findByPassengerId(passengerId);
            } else if (flightNumberExists) {
                if (fromExists && toExists)
                    reservations = reservationRepository.findByFlightSourceDestination(flightNumber, from, to);
                else if (fromExists)
                    reservations = reservationRepository.findBySourceFlight(from, flightNumber);
                else if (toExists)
                    reservations = reservationRepository.findByDestinationFlight(to, flightNumber);
                else
                    reservations = reservationRepository.findByFlightNumber(flightNumber);
            } else if (fromExists) {
                if (toExists)
                    reservations = reservationRepository.findBySourceDestination(from, to);
                else
                    reservations = reservationRepository.findBySource(from);
            } else if (toExists) {
                reservations = reservationRepository.findByDestination(to);
            }

            for (Reservation res : reservations)
                reservationArray.put(res.getFullJSON());
            result.put("reservations", reservationArray);
            if (reservations.size() < 1 || reservations == null || reservations.isEmpty()) {
                Message message = new Message("No results found", "200");
                return new ResponseEntity<Object>(message.getXML(), HttpStatus.OK);

            } else
                return new ResponseEntity<Object>(XML.toString(result), HttpStatus.OK);
        }

    }

}
