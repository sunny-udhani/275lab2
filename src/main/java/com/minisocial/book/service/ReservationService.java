package com.minisocial.book.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.minisocial.book.entity.Flight;
import com.minisocial.book.entity.Passenger;
import com.minisocial.book.entity.Plane;
import com.minisocial.book.entity.Reservation;
import com.minisocial.book.repository.FlightRepository;
import com.minisocial.book.repository.PassengerRepository;
import com.minisocial.book.repository.ReservationRepository;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private PassengerRepository passengerRepository;

    @Autowired
    private FlightRepository flightRepository;

    public String getPassengerById(String id, MediaType mediaType) {
        Reservation p = reservationRepository.findByReservationNumberEquals(id);
        return (mediaType == MediaType.APPLICATION_XML) ? objToXML(p) : objToJson(p);
    }

    public String getPassengerById(String id) {
        Reservation p = reservationRepository.findByReservationNumberEquals(id);
        return objToJson(p);
    }

    @Transactional
    public String createRepository(Reservation p) {
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

    public String objToXML(Reservation reservation) {

        try {
            return XML.toString(new JSONObject(objToJson(reservation)));
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public String createReservation(String passengerId, String[] flightList) throws JSONException, ParseException, JsonProcessingException {
        Reservation reserv = new Reservation();
        List<JSONObject> flightJSONLists = new ArrayList<JSONObject>();
        List<Flight> flightLists = new ArrayList<Flight>();
        int totalPrice = 0;
        Passenger passenger = passengerRepository.findByIdEquals(passengerId);
        //reserv.setPassenger(passenger);
        //Need to append the already existing flightlist of passenger in this
        boolean isOverlapped = checkOverlapping(flightList);
        if (!isOverlapped) {
            for (int i = 0; i < flightList.length; i++) {

                Flight flight = null;
                flight = flightRepository.findByFlightNumberEquals(flightList[i]);

                if (flight.getSeatsLeft() > 0) {
                    totalPrice += flight.getPrice();
                    flight.setSeatsLeft(flight.getSeatsLeft() - 1);
                    JSONObject flightJSON = new JSONObject();
                    flightJSON.put("flightnumber", flight.getFlightNumber());
                    flightJSON.put("price", flight.getPrice());
                    flightJSON.put("origin", flight.getOrigin());
                    flightJSON.put("to", flight.getTo());
                    flightJSON.put("departureTime", flight.getDepartureTime());
                    flightJSON.put("arrivalTime", flight.getArrivalTime());
                    flightJSON.put("seatsLeft", flight.getSeatsLeft());
                    flightJSON.put("description", flight.getDescription());

                    Plane jPlane = new Plane();
                    jPlane = flight.getPlane();
                    ObjectMapper mapperObj = new ObjectMapper();
                    String jp = mapperObj.writeValueAsString(jPlane);

                    JSONObject jsonObject = new JSONObject(jp);

                    flightJSON.put("plane", jsonObject);

                    //flightJSON.put("plane", flight.getPlane());

                    flightJSONLists.add(flightJSON);
                    flightLists.add(flight);
                    flight.getPassengers().add(passenger);

                    flightRepository.save(flight);

                }
            }

            reserv.setPrice(totalPrice);
            reserv.setFlights(flightLists);
            reserv.setPassenger(passenger);
            //passenger.setFlights(flightList);
            //passengerDAO.save(passenger);
            reservationRepository.save(reserv);
            JSONObject jnew = new JSONObject();
            jnew.put("reservationNumber", reserv.getReservationNumber());
            jnew.put("price", reserv.getPrice());
            Passenger jPassenger = new Passenger();
            jPassenger = reserv.getPassenger();
            ObjectMapper mapperObj = new ObjectMapper();
            String jp = mapperObj.writeValueAsString(jPassenger);

            JSONObject jsonObject = new JSONObject(jp);

            jnew.put("passenger", jsonObject);
            JSONObject flightJ = new JSONObject();
            flightJ.put("flight", flightJSONLists);
            jnew.put("flights", flightJ);

            JSONObject returnVal = new JSONObject("{\"reservation\":" + jnew.toString() + "}");
            //returnVal.put("reservation", jnew);
            System.out.println(returnVal);
            //ObjectMapper mapperObj = new ObjectMapper();
            //String jso = mapperObj.writeValueAsString(reserv);

            //JSONObject j = new JSONObject("{\"reservation\":"+jso+"}");
            return XML.toString(returnVal);

        } else {
            System.out.println("<<<<<OVERLAPPING>>>>>>>>>>");
        }
        return null;
    }

    class Interval {
        public Date start;
        public Date end;

    }

    private boolean checkOverlapping(String[] flightList) throws ParseException {
        boolean isOverlapped = false;
        Interval[] schedule = new Interval[flightList.length];
        for (int i = 0; i < flightList.length; i++) {
            Flight flight = null;
            flight = flightRepository.findByFlightNumberEquals(flightList[i]);
            //Flight flight = flightList.get(i);

            Interval interval = new Interval();
            interval.start = formatDate(flight.getDepartureTime());
            interval.end = formatDate(flight.getArrivalTime());

            schedule[i] = interval;

        }
        Date[] dateArr = new Date[2];
        //Collections.sort(schedule);
        for (int i = 0; i < schedule.length; i++) {
            System.out.println("Interval" + i + ":start: " + schedule[i].start);
            System.out.println("Interval" + i + ":end: " + schedule[i].end);

        }
        System.out.println("------------------");
        Arrays.sort(schedule, new Comparator<Interval>() {
            public int compare(Interval a, Interval b) {
                //System.out.println((int)(a.start.getTime()) - (int)(b.start.getTime()));
                return a.end.compareTo(b.start);
                //return (int)(a.start.getTime()) - (int)(b.start.getTime());
            }

        });

        for (int i = 0; i < schedule.length; i++) {
            System.out.println("Interval" + i + ":start: " + schedule[i].start);
            System.out.println("Interval" + i + ":end: " + schedule[i].end);

        }
        for (int i = 1; i < schedule.length; i++) {
            if (schedule[i].start.before(schedule[i - 1].end)) {
                return true;
            }
        }

        return isOverlapped;
    }

    public String makeReservation(String passengerId, String[] flightList) throws ParseException, JsonProcessingException, JSONException {
        // TODO Auto-generated method stub
        Reservation reserv = new Reservation();
        List<JSONObject> flightJSONLists = new ArrayList<JSONObject>();
        List<Flight> flightLists = new ArrayList<Flight>();
        int totalPrice = 0;
        Passenger passenger = passengerRepository.findByIdEquals(passengerId);
        //reserv.setPassenger(passenger);
        //Need to append the already existing flightlist of passenger in this
        boolean isOverlapped = checkOverlapping(flightList);
        if (!isOverlapped) {
            for (int i = 0; i < flightList.length; i++) {

                Flight flight = new Flight();
                flight = flightRepository.findByFlightNumberEquals(flightList[i]);

                if (flight.getSeatsLeft() > 0) {
                    totalPrice += flight.getPrice();
                    flight.setSeatsLeft(flight.getSeatsLeft() - 1);
                    JSONObject flightJSON = new JSONObject();
                    flightJSON.put("number", flight.getFlightNumber());
                    flightJSON.put("price", flight.getPrice());
                    flightJSON.put("origin", flight.getOrigin());
                    flightJSON.put("to", flight.getTo());
                    flightJSON.put("departureTime", flight.getDepartureTime());
                    flightJSON.put("arrivalTime", flight.getArrivalTime());
                    flightJSON.put("seatsLeft", flight.getSeatsLeft());
                    flightJSON.put("description", flight.getDescription());

                    Plane jPlane = new Plane();
                    jPlane = flight.getPlane();
                    ObjectMapper mapperObj = new ObjectMapper();
                    String jp = mapperObj.writeValueAsString(jPlane);

                    JSONObject jsonObject = new JSONObject(jp);

                    flightJSON.put("plane", jsonObject);

                    //flightJSON.put("plane", flight.getPlane());

                    flightJSONLists.add(flightJSON);
                    flightLists.add(flight);
                    flight.getPassengers().add(passenger);

                    flightRepository.save(flight);

                }
            }

            reserv.setPrice(totalPrice);
            reserv.setFlights(flightLists);
            reserv.setPassenger(passenger);
            //passenger.setFlights(flightList);
            //passengerDAO.save(passenger);
            reservationRepository.save(reserv);
            JSONObject jnew = new JSONObject();
            jnew.put("reservationNumber", reserv.getReservationNumber());
            jnew.put("price", reserv.getPrice());
            Passenger jPassenger = new Passenger();
            jPassenger = reserv.getPassenger();
            ObjectMapper mapperObj = new ObjectMapper();
            String jp = mapperObj.writeValueAsString(jPassenger);

            JSONObject jsonObject = new JSONObject(jp);

            jnew.put("passenger", jsonObject);
            JSONObject flightJ = new JSONObject();
            flightJ.put("flight", flightJSONLists);
            jnew.put("flights", flightJ);

            JSONObject returnVal = new JSONObject("{\"reservation\":" + jnew.toString() + "}");
            //returnVal.put("reservation", jnew);
            System.out.println(returnVal);
            //ObjectMapper mapperObj = new ObjectMapper();
            //String jso = mapperObj.writeValueAsString(reserv);

            //JSONObject j = new JSONObject("{\"reservation\":"+jso+"}");
            return XML.toString(returnVal);

        } else {
            System.out.println("<<<<<OVERLAPPING>>>>>>>>>>");
        }
        return null;
    }

    public Date formatDate(String date) throws ParseException {
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd HH:mm:SSS");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH");
        return formatter.parse(formatter.format(parser.parse(date)));
    }

}

