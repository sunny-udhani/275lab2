package com.minisocial.book.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.minisocial.book.entity.Passenger;
import com.minisocial.book.repository.PassengerRepository;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.awt.*;
import java.io.Console;

@Service
public class PassengerService {

    @Autowired
    private PassengerRepository passengerRepository;

    public String getPassengerById(String id, MediaType mediaType) {
        Passenger p = passengerRepository.findByIdEquals(id);
        return (mediaType == MediaType.APPLICATION_XML) ? objToXML(p) : objToJson(p);
    }

    public String getPassengerById(String id) {
        Passenger p = passengerRepository.findByIdEquals(id);
        return objToJson(p);
    }

    @Transactional
    public String createPassenger(Passenger p) {
        passengerRepository.save(p);
        return objToJson(p);
    }

//    public Iterable<Users> getAllUsers(){
//        return pr.findAll();
//    }

    public String objToJson(Passenger passenger) {
        String jsonInString = null;
        try {
            ObjectWriter writer = new ObjectMapper().writer();

            jsonInString = writer.writeValueAsString(passenger);
        } catch (JsonProcessingException ex) {
            return null;
        }
//        System.out.println("result");
//        System.out.println(jsonInString);
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
    
    

    public String objToXML(Passenger passenger) {

        try {
			return XML.toString(new JSONObject(objToJson(passenger)));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
    }

	public String updatePassenger(String id, String fname, String lname, String age, String gender,
			String phone) throws JsonProcessingException, JSONException {
		// TODO Auto-generated method stub
		Passenger p = passengerRepository.findOne(id);
		p.setFirstname(fname);
		p.setLastname(lname);
		p.setAge(Integer.parseInt(age));
		p.setGender(gender);
		p.setPhone(phone);
		passengerRepository.save(p);
		ObjectMapper mapperObj = new ObjectMapper();
		String jso = mapperObj.writeValueAsString(p);
		JSONObject j = new JSONObject("{\"passenger\":"+jso+"}");
		return j.toString();
	}

	public String delete(String id) throws JSONException {
		// TODO Auto-generated method stub
		passengerRepository.delete(id);
		JSONObject inner = new JSONObject();
		JSONObject outer = new JSONObject();
		inner.put("code", "200");
		inner.put("msg", "Passenger with id " +id+" is deleted successfully");
		outer.put("Response", inner);
		return XML.toString(outer);
	}
	
	
    

}
