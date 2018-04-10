package com.minisocial.book.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.minisocial.book.entity.Reservation;
import com.minisocial.book.service.ReservationService;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import java.text.ParseException;
import java.util.Map;

@Controller
@RequestMapping(path = "/reservation")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Produces(MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    ResponseEntity<?> getUser(@PathVariable("id") String id) {
        // This returns a JSON with the users
        Response.ResponseBuilder rBuild = null;
        String p = reservationService.getPassengerById(id);
        System.out.println(reservationService.getPassengerById(id));

        return new ResponseEntity<Object>(p, HttpStatus.OK);

    }

    @GetMapping(path = "/{id}", params = "xml", produces = MediaType.APPLICATION_XML_VALUE)
//    @Consumes(MediaType.ALL_VALUE)
    @Produces(MediaType.APPLICATION_XML_VALUE)
    public @ResponseBody
    ResponseEntity<?> getUserXML(@PathVariable("id") String id, @RequestParam Map<String, String> params) {
        // This returns a XML/JSON based on contentconfig.
        String resp = reservationService.getPassengerById(id, MediaType.APPLICATION_XML);
        return new ResponseEntity<Object>(resp, HttpStatus.OK);
    }

    @PostMapping( produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    ResponseEntity<?> create(@RequestParam Map<String, String> params) throws JsonProcessingException, JSONException, ParseException {

        String passengerId = params.get("passengerId");
        String flightList = params.get("flightLists");

        String[] flightLists = flightList.split(",");
        String resp = reservationService.createReservation(passengerId, flightLists);
        return new ResponseEntity<Object>(resp, HttpStatus.OK);
    }

    @PutMapping(path = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    ResponseEntity<?> updateReservation(@PathVariable("id") String id, @RequestParam Map<String, String> params) {
        // This returns a XML/JSON based on contentconfig.
        Reservation resp = new Reservation();
        return new ResponseEntity<Object>(resp, HttpStatus.OK);
    }

}
