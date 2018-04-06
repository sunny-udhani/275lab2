package com.minisocial.book.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.minisocial.book.entity.Passenger;
import com.minisocial.book.repository.PassengerRepository;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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
        JSONObject jsonobj = new JSONObject(jsonInString);
        return jsonobj.toString();
    }

    public String objToXML(Passenger passenger) {

        return XML.toString(new JSONObject(objToJson(passenger)) , "passenger");
    }

}
