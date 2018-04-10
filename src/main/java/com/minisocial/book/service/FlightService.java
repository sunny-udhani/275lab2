package com.minisocial.book.service;

import javax.transaction.Transactional;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.minisocial.book.entity.Flight;
import com.minisocial.book.entity.Passenger;
import com.minisocial.book.repository.FlightRepository;
import com.minisocial.book.repository.PassengerRepository;

@Service
public class FlightService {

	 @Autowired
	 private FlightRepository flightRepository;
	 public String objToJson(Flight flight) {
	        String jsonInString = null;
	        try {
	            ObjectWriter writer = new ObjectMapper().writer();

	            jsonInString = writer.writeValueAsString(flight);
	        } catch (JsonProcessingException ex) {
	            return null;
	        }
//	        System.out.println("result");
//	        System.out.println(jsonInString);
	        JSONObject jsonobj = null;
			try {
				jsonobj = new JSONObject().put("passenger", new JSONObject(jsonInString));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        System.out.println("result : " + jsonobj.toString());
	        return jsonobj.toString();
	    }
	    
	    

	    public String objToXML(Flight flight) {

	        try {
				return XML.toString(new JSONObject(objToJson(flight)));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
			
	    }

	    public String getFlight(String id) {
	    		Flight p = flightRepository.findByFlightNumberEquals(id);
	        return objToJson(p);
	    }



		public String getFlight(String id, MediaType media) {
			Flight p = flightRepository.findByFlightNumberEquals(id);
	        return (media == MediaType.APPLICATION_XML) ? objToXML(p) : objToJson(p);
		}
		
		@Transactional
	    public String createFlight(Flight p) {
	        flightRepository.save(p);
	        return objToJson(p);
	    }
}
