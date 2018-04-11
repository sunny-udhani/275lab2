package com.minisocial.book.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.minisocial.book.entity.Flight;
import com.minisocial.book.entity.Message;
import com.minisocial.book.entity.Passenger;
import com.minisocial.book.repository.FlightRepository;
import com.minisocial.book.repository.PassengerRepository;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class PassengerService {

    @Autowired
    private PassengerRepository passengerRepository;
    @Autowired
    private FlightRepository flightRepository;

    public String getPassengerById(String id, MediaType mediaType) {
        Passenger p = passengerRepository.findByIdEquals(id);
        return (mediaType == MediaType.APPLICATION_XML) ? p.getXML() : p.getFullJSON().toString();
    }

    public String getPassengerById(String id) {
        Passenger p = passengerRepository.findByIdEquals(id);
        return p.getFullJSON().toString();
    }

    @Transactional
    public String createPassenger(Passenger p) {
        passengerRepository.save(p);
        return p.getFullJSON().toString();
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

    public String updatePassenger(String id, String fname, String lname, int age, String gender,
                                  String phone) throws JsonProcessingException, JSONException {
        // TODO Auto-generated method stub
        //Passenger p = passengerRepository.findOne(id);
        Passenger p = passengerRepository.findByIdEquals(id);
        p.setFirstname(fname);
        p.setLastname(lname);
        p.setAge(age);
        p.setGender(gender);
        p.setPhone(phone);
        try {
            passengerRepository.save(p);
        } catch (DataIntegrityViolationException ex) {
            return new Message("Cannot update , passenger with same phone number already exists", "400").getMessageJSON().toString();
        }
        ObjectMapper mapperObj = new ObjectMapper();
        String jso = mapperObj.writeValueAsString(p);
        JSONObject j = new JSONObject("{\"passenger\":" + jso + "}");
        return j.toString();
    }

    public boolean deletePassenger(String id) throws JSONException {
        // TODO Auto-generated method stub

        if (passengerRepository.existsById(id)){
            Passenger passenger=passengerRepository.findByIdEquals(id);
            List<Flight> flights= passenger.getFlights();
            for(Flight flight: flights)
            {
                flight.setSeatsLeft(flight.getSeatsLeft()+1);
                flightRepository.save(flight);
            }
            passengerRepository.deleteById(id);
            return true;
        }else{
            return false;
        }
    }


}
