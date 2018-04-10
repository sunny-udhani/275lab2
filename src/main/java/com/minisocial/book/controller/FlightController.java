package com.minisocial.book.controller;

import java.util.Date;
import java.util.Map;

import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.minisocial.book.entity.Passenger;
import com.minisocial.book.entity.Plane;
import com.minisocial.book.service.FlightService;
import com.minisocial.book.service.PassengerService;

@Controller
@RequestMapping(path = "/flight")
public class FlightController {

	
	 @Autowired
	 private FlightService flightService;
	 
	 @GetMapping(path = "/{flightNumber}", produces = MediaType.APPLICATION_JSON_VALUE)
	    @Produces(MediaType.APPLICATION_JSON_VALUE)
	    public @ResponseBody
	    ResponseEntity<?> getUser(@PathVariable("flightNumber") String id) {
		 	String resp=flightService.getFlight(id);
	        return new ResponseEntity<Object>(resp, HttpStatus.OK);

	    }
	 
	 @GetMapping(path="/{flightNumber}", params = "xml", produces = MediaType.APPLICATION_XML_VALUE)
   @Produces(MediaType.APPLICATION_XML_VALUE)
   public @ResponseBody ResponseEntity<?> getUserXML(@PathVariable("flightNumber") String id, @RequestParam Map<String, String> params) {
       // This returns a XML/JSON based on contentconfig.
       String resp = flightService.getFlight(id, MediaType.APPLICATION_XML);
       return new ResponseEntity<Object>(resp, HttpStatus.OK);
  	}
	 
	 
	 @PostMapping(path = "/", produces = MediaType.APPLICATION_JSON_VALUE)
	    public @ResponseBody ResponseEntity<?> create(@RequestParam Map<String, String> params) {

	        String flightNumber = params.get("flightNumber");
	        Double price = Double.parseDouble(params.get("price"));
	        String origin = params.get("origin");
	        String to = params.get("to");
	        String departureTime = params.get("departureTime");
	        String arrivalTime = params.get("arrivalTime");
	        int seatsLeft = Integer.parseInt(params.get("seatsLeft"));
	        String description = params.get("description");
	        String model=params.get("model");
	        int capacity=Integer.parseInt(params.get("capacity"));
	        String manufacturer=params.get("manufacturer");
	        int year=Integer.parseInt(params.get("year"));

	        Flight p = new Flight();
	        p.setFlightNumber(flightNumber);
	        p.setPrice(price);
	        p.setOrigin(origin);
	        p.setTo(to);
	        p.setDepartureTime(new Date());
	        p.setArrivalTime(new Date());
	        p.setSeatsLeft(seatsLeft);
	        Plane x=new Plane();
	        x.setModel(model);
	       x.setCapacity(capacity);
	       x.setYear(year);
	        x.setManufacturer(manufacturer);
	        p.setPlane(x);
	        p.setDescription(description);

	        String resp = flightService.createFlight(p);
	        return new ResponseEntity<Object>(resp, HttpStatus.OK);
	    }
	 
	 
	 @DeleteMapping(path="/{flightNumber}",produces=MediaType.APPLICATION_JSON_VALUE)
		public String deleteFlight(@PathVariable("flightNumber") String id) throws JSONException{
	    
	    	return flightService.delete(id);
		}
	 
	 
}
