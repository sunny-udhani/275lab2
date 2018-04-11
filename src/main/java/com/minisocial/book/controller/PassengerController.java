package com.minisocial.book.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.minisocial.book.entity.Message;
import com.minisocial.book.entity.Passenger;
import com.minisocial.book.service.PassengerService;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.util.Map;

@Controller
@RequestMapping(path = "/passenger")
public class PassengerController {

    @Autowired
    private PassengerService passengerService;


    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Produces(MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    ResponseEntity<?> getPassenger(@PathVariable("id") String id) throws JSONException {

        String p = passengerService.getPassengerById(id);
//        System.out.println(passengerService.getPassengerById(id) + "adhjbnsdjhbashj");

        return new ResponseEntity<Object>(p, HttpStatus.OK);

    }

    @GetMapping(path = "/{id}", params = "xml", produces = MediaType.APPLICATION_XML_VALUE)
//    @Consumes(MediaType.ALL_VALUE)
    @Produces(MediaType.APPLICATION_XML_VALUE)
    public @ResponseBody
    ResponseEntity<?> getPassengerXML(@PathVariable("id") String id, @RequestParam Map<String, String> params) throws JSONException {
        // This returns a XML/JSON based on contentconfig.
        String resp = passengerService.getPassengerById(id, MediaType.APPLICATION_XML);
        return new ResponseEntity<Object>(resp, HttpStatus.OK);
    }

    @PostMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    ResponseEntity<?> create(@RequestParam Map<String, String> params) throws JSONException {
        Message errorMessage = null;
        boolean errorFlag = false;
        String fname = params.get("firstname");
        String lname = params.get("lastname");
        String age = params.get("age");
        String gender = params.get("gender");
        String phone = params.get("phone");
        String resp = null;
        Passenger p = new Passenger();
        p.setAge(Integer.parseInt(age));
        p.setFirstname(fname);
        p.setLastname(lname);
        p.setGender(gender);
        p.setPhone(phone);

        try {
            resp = passengerService.createPassenger(p);
        } catch (DataIntegrityViolationException ex) {
            errorMessage = new Message("Another passenger with the same Phone number already exists", "400");
            errorFlag = true;
        }
        if (errorFlag)
            return new ResponseEntity<Object>(errorMessage.getMessageJSON().toString(), HttpStatus.BAD_REQUEST);
        return new ResponseEntity<Object>(resp, HttpStatus.OK);
    }

    @PutMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    ResponseEntity<?> updatePassenger(@PathVariable("id") String id, @RequestParam Map<String, String> params) throws JsonProcessingException, JSONException {

        Message errorMessage = null;
        boolean errorFlag = false;
        String fname = params.get("firstname");
        String lname = params.get("lastname");
        int age = Integer.parseInt(params.get("age"));
        String gender = params.get("gender");
        String phone = params.get("phone");

        String resp = passengerService.updatePassenger(id, fname, lname, age, gender, phone);

        return new ResponseEntity<Object>(resp, HttpStatus.OK);
    }

    @DeleteMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    ResponseEntity<?> deletePassenger(@PathVariable("id") String id) throws JSONException {
        Message success = new Message("Passenger with id " + id + " is deleted successfully ", "200");
        Message error = new Message("Passenger with id " + id + " does not exist", "404");

        if (!passengerService.deletePassenger(id))
            return new ResponseEntity<Object>(error.getMessageJSON().toString(), HttpStatus.OK);
        return new ResponseEntity<Object>(success.getXML(), HttpStatus.OK);
    }

}
