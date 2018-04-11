package com.minisocial.book.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.ws.rs.Produces;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.minisocial.book.entity.Flight;
import com.minisocial.book.entity.Plane;
import com.minisocial.book.service.FlightService;

@Controller
@RequestMapping(path = "/flight")
public class FlightController {


    @Autowired
    private FlightService flightService;

    @GetMapping(path = "/{flightNumber}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Produces(MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    ResponseEntity<?> getFlight(@PathVariable("flightNumber") String id) {
        String resp = flightService.getFlight(id);
        return new ResponseEntity<Object>(resp, HttpStatus.OK);

    }

    @GetMapping(path = "/{flightNumber}", params = "xml", produces = MediaType.APPLICATION_XML_VALUE)
    @Produces(MediaType.APPLICATION_XML_VALUE)
    public @ResponseBody
    ResponseEntity<?> getFlightXML(@PathVariable("flightNumber") String id, @RequestParam Map<String, String> params) {
        // This returns a XML/JSON based on contentconfig.
        String resp = flightService.getFlight(id, MediaType.APPLICATION_XML);
        return new ResponseEntity<Object>(resp, HttpStatus.OK);
    }


    @PostMapping(path = "/{flightNumber}", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    ResponseEntity<?> createFlight(@PathVariable("flightNumber") String flightNumber, @RequestParam Map<String, String> params) {

        try {

//        String flightNumber = params.get("flightNumber");
            Double price = Double.parseDouble(params.get("price"));
            String origin = params.get("origin");
            String to = params.get("to");

            String departureTime = params.get("departureTime");
            String arrivalTime = params.get("arrivalTime");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH");
            Date depTime = formatDate(departureTime);
            Date arrTime = formatDate(arrivalTime);
            int seatsLeft = (params.containsKey("seatsLeft")) ? Integer.parseInt(params.get("seatsLeft")) : 0;
            String description = params.get("description");
            String model = params.get("model");
            int capacity = Integer.parseInt(params.get("capacity"));
            if(seatsLeft ==0){
                seatsLeft = capacity;
            }
            String manufacturer = params.get("manufacturer");
            int year = Integer.parseInt(params.get("year"));
            Flight flight = new Flight();
            flight.setFlightNumber(flightNumber);
            flight.setPrice(price);
            flight.setOrigin(origin);
            flight.setTo(to);
            flight.setDepartureTime(depTime);
            flight.setArrivalTime(arrTime);
            flight.setSeatsLeft(seatsLeft);
            Plane plane = new Plane();
            plane.setModel(model);
            plane.setCapacity(capacity);
            plane.setYear(year);
            plane.setManufacturer(manufacturer);
            flight.setPlane(plane);
            flight.setDescription(description);

            String resp = flightService.createFlight(flight);
            return new ResponseEntity<Object>(resp, HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ResponseEntity<Object>(ex, HttpStatus.NOT_FOUND);
        }
    }


    @DeleteMapping(path = "/{flightNumber}", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    ResponseEntity<?> deleteFlight(@PathVariable("flightNumber") String id) throws JSONException {
        String resp = flightService.deleteFlight(id);
        return new ResponseEntity<Object>(resp, HttpStatus.OK);
    }

    public static Date formatDate(String date) throws ParseException {
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd-HH");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH");
        return formatter.parse(formatter.format(parser.parse(date)));
    }


}
