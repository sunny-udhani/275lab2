package com.minisocial.book.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.minisocial.book.entity.Passenger;
import com.minisocial.book.entity.Reservation;
import com.minisocial.book.repository.ReservationRepository;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    public String getPassengerById(String id, MediaType mediaType) {
        Reservation p = reservationRepository.findByReservationNumberEquals(id);
        return (mediaType == MediaType.APPLICATION_XML) ? objToXML(p) : objToJson(p);
    }

    public String getPassengerById(String id) {
        Reservation p = reservationRepository.findByReservationNumberEquals(id);
        return objToJson(p);
    }

    @Transactional
    public String createPassenger(Reservation p) {
        reservationRepository.save(p);
        return objToJson(p);
    }

//    public Iterable<Users> getAllUsers(){
//        return pr.findAll();
//    }

    public String objToJson(Reservation reservation) {
        String jsonInString = null;
        try {
            ObjectWriter writer = new ObjectMapper().writer();

            jsonInString = writer.writeValueAsString(reservation);
        } catch (JsonProcessingException ex) {
            return null;
        }
//        System.out.println("result");
//        System.out.println(jsonInString);
        JSONObject jsonobj = new JSONObject().put("passenger", new JSONObject(jsonInString));
        System.out.println("result : " + jsonobj.toString());
        return jsonobj.toString();
    }

    public String objToXML(Reservation reservation) {

        return XML.toString(new JSONObject(objToJson(reservation)));
    }

}

