package com.minisocial.book.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.minisocial.book.entity.*;
import com.minisocial.book.repository.FlightRepository;
import com.minisocial.book.repository.PassengerRepository;
import com.minisocial.book.repository.ReservationRepository;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private PassengerRepository passengerRepository;

    @Autowired
    private FlightRepository flightRepository;

    public String getReservationById(String id, MediaType mediaType) throws JSONException {
        if (reservationRepository.existsById(id)) {
            Reservation p = reservationRepository.findByReservationNumberEquals(id);
            return (mediaType == MediaType.APPLICATION_XML) ? p.getXML() : p.getFullJSON().toString();
        }
        return (mediaType == MediaType.APPLICATION_XML) ? new Message(" Reservation with number " + id + " does not exist", "404").getXML() : new Message(" Reservation with number " + id + " does not exist", "404").getMessageJSON().toString();
    }

    public String getReservationById(String id) throws JSONException {
        if (reservationRepository.existsById(id)) {
            Reservation p = reservationRepository.findByReservationNumberEquals(id);
            return p.getFullJSON().toString();
        }
        return new Message(" Reservation with number " + id + " does not exist", "404").getMessageJSON().toString();
    }

    public String createReservation(String passengerId, List<String> flightList) throws JSONException, ParseException, JsonProcessingException {
        Message message = new Message("", "400");
        if (flightList != null && !flightList.isEmpty()) {
            try {
                if (validatePassenger(passengerId) && validateFlights(flightList)) {
                    int total = 0;
                    List<Flight> flights = new ArrayList<Flight>();
                    Passenger passenger = passengerRepository.findByIdEquals(passengerId);
                    for (String flightNumber : flightList) {
                        Flight tempFlight = flightRepository.findByFlightNumberEquals(flightNumber);
                        total += tempFlight.getPrice();
                        flights.add(tempFlight);
                    }
                    if (!checkOverlap(passenger, flights) && checkSeatsLeft(flights)) {
                        List<Flight> passengerFlights = passenger.getFlights();
                        passengerFlights.addAll(flights);
                        passenger.setFlights(passengerFlights);
                        passengerRepository.save(passenger);
                        Reservation newReservation = new Reservation();
                        newReservation.setPassenger(passenger);
                        newReservation.setPrice(total);
                        newReservation.setFlights(flights);
                        reservationRepository.save(newReservation);
                        for (Flight flight : flights) {
                            flight.setSeatsLeft(flight.getSeatsLeft() - 1);
                            flightRepository.save(flight);
                        }
                        return newReservation.getXML();
//                        response.sendRedirect("/reservation/" + newReservation.getOrderNumber() + "?xml=true");
                    } else
                        message.setMessage("Flights Overlap occurred");
                } else
                    message.setMessage("Flight or Passenger doesnt exist");
            } catch (Exception e) {
                e.printStackTrace();
                message.setMessage(e.toString());
            }
        } else {
            message.setMessage("Please specify flights");
        }
        return message.getMessageJSON().toString();
    }

    public boolean deleteReservation(String id) {
        if (!reservationRepository.existsById(id)) {
            return false;
        } else {
            Reservation reservation = reservationRepository.findByReservationNumberEquals(id);
            Passenger passenger = reservation.getPassenger();
            List<Flight> passengerFlights = passenger.getFlights();
            List<Flight> flights = reservation.getFlights();
            for (Flight flight : flights) {
                flight.setSeatsLeft(flight.getSeatsLeft() + 1);
                flightRepository.save(flight);
                passengerFlights.remove(flight);
            }
            passenger.setFlights(passengerFlights);
            passengerRepository.save(passenger);
            reservationRepository.delete(reservation);
            return true;
        }
    }

    public ResponseEntity<Object> updateReservation(String id, List<String> flightsAdded, List<String> flightsRemoved) throws JSONException {
        try {
            System.out.println("Entered Try");
            if ((flightsAdded == null || flightsAdded.isEmpty()) && (flightsRemoved == null || flightsRemoved.isEmpty())) {
                Message error = new Message("flightsAdded/flightsRemoved parameters are empty", "404");
                return new ResponseEntity(error.getMessageJSON().toString(), HttpStatus.NOT_FOUND);
            } else if (flightsAdded != null && flightsRemoved != null && flightsAdded.equals(flightsRemoved)) {
                Message error = new Message("Both flightsAdded and flightsRemoved parameters are identical", "404");
                return new ResponseEntity(error.getMessageJSON().toString(), HttpStatus.NOT_FOUND);
            } else if (!reservationRepository.existsById(id)) {
                Message error = new Message("Reservation with number " + id + " does not exist", "404");
                return new ResponseEntity(error.getMessageJSON().toString(), HttpStatus.NOT_FOUND);
            } else {
                System.out.println("In main else");
                Reservation reservation = reservationRepository.findByReservationNumberEquals(id);
                Passenger passenger = reservation.getPassenger();
                if (flightsRemoved != null && !flightsRemoved.isEmpty()) {
                    System.out.println("Removed flight block");
                    List<Flight> flightsToBeRemoved = getFlights(flightsRemoved);
                    if (!validateFlights(flightsRemoved)) {
                        Message error = new Message("Flights to be removed does'nt exist", "404");
                        return new ResponseEntity(error.getMessageJSON().toString(), HttpStatus.NOT_FOUND);
                    } else if (checkReservationForExistingFlights(reservation, flightsToBeRemoved)) {
                        List<Flight> oldFlights = reservation.getFlights();
                        passenger = reservation.getPassenger();
                        List<Flight> passengerFlights = passenger.getFlights();
                        double oldTotal = reservation.getPrice();
                        double newTotal = oldTotal;
                        for (Flight flight : flightsToBeRemoved) {
                            flight.setSeatsLeft(flight.getSeatsLeft() + 1);
                            flightRepository.save(flight);
                            oldFlights.remove(flight);
                            passengerFlights.remove(flight);
                            newTotal -= flight.getPrice();
                        }
                        passenger.setFlights(passengerFlights);
                        reservation.setPrice(newTotal);
                        reservation.setFlights(oldFlights);
                    }
                }
                if (flightsAdded != null && !flightsAdded.isEmpty()) {
                    System.out.println("Flight added block");
                    if (!validateFlights(flightsAdded)) {
                        System.out.println("if flight to be added doesnt exist");
                        Message error = new Message("Flights to be added does'nt exist", "404");
                        return new ResponseEntity(error.getMessageJSON().toString(), HttpStatus.NOT_FOUND);
                    } else {
                        List<Flight> flightsToBeAdded = getFlights(flightsAdded);
                        passenger = reservation.getPassenger();
                        List<Flight> passengerFlights = passenger.getFlights();

                        if (!checkOverlap(passenger, flightsToBeAdded)) {
                            System.out.println("No overlap");
                            double newTotal = reservation.getPrice();
                            List<Flight> flightList = reservation.getFlights();
                            for (Flight flight : flightsToBeAdded) {
                                flight.setSeatsLeft(flight.getSeatsLeft() - 1);
                                flightRepository.save(flight);
                                flightList.add(flight);
                                passengerFlights.add(flight);
                                newTotal += flight.getPrice();
                            }
                            passenger.setFlights(passengerFlights);
                            reservation.setFlights(flightList);
                            reservation.setPrice(newTotal);
                        } else {
                            System.out.println("In overlap else");
                            Message error = new Message("Flights to be added overlaps with existing flight/s", "404");
                            return new ResponseEntity(error.getMessageJSON().toString(), HttpStatus.NOT_FOUND);
                        }
                    }
                }
                passengerRepository.save(passenger);
                reservationRepository.save(reservation);
               return new ResponseEntity(reservation.getFullJSON().toString(), HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            System.out.println("In catch");
            e.printStackTrace();
            Message error = new Message(e.toString(), "404");
            return new ResponseEntity(error.getMessageJSON().toString(), HttpStatus.NOT_FOUND);
        }
//        Message error = new Message("not working", "404");

//        return new ResponseEntity(error.getMessageJSON().toString(), HttpStatus.NOT_FOUND);
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
            ex.printStackTrace();
            return null;
        }
//        System.out.println("result");
//        System.out.println(jsonInString);
        JSONObject jsonobj = null;
        try {
            jsonobj = new JSONObject().put("reservation", new JSONObject(jsonInString));

            JSONArray flights = new JSONArray();
//            for (Flight flight:reservation.getFlights())
//            {
//                flights.put(flight.);
//            }
//            reserv.put("flights",new JSONObject().put("flight",flights));
//            reserv.put("passenger",this.getPassenger().getJSON());
//            result.put("reservation",reserv);
//            return result;
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

    public static Date formatDate(String date) throws ParseException {
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd HH:mm:SSS");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH");
        return formatter.parse(formatter.format(parser.parse(date)));
    }

    public static boolean checkOverlap(Passenger passenger, List<Flight> flights) throws ParseException {
        for (Flight flight : flights) {
            Date departureDate = formatDate(flight.getDepartureTime());
            Date arrivalDate = formatDate(flight.getDepartureTime());
            List<Flight> bookedFlights = passenger.getFlights();
            for (Flight booked : bookedFlights) {
                if (!booked.equals(flight)) {
                    Date bookedDeparture = formatDate(booked.getDepartureTime());
                    Date bookedArrival = formatDate(booked.getArrivalTime());
//                    System.out.println("bookedD:" + bookedDeparture.toString());
//                    System.out.println("bookedA:" + bookedArrival.toString());
//                    System.out.println("newD:" + departureDate.toString());
//                    System.out.println("newA:" + arrivalDate.toString());
                    System.out.println(checkFlightDatesClash(bookedDeparture, bookedArrival, departureDate, arrivalDate));
                    if (ReservationService.checkFlightDatesClash(bookedDeparture, bookedArrival, departureDate, arrivalDate))
                        return true;
                } else
                    return true;
            }
        }
        return false;
    }

    private static boolean checkFlightDatesClash(Date oldDep, Date oldArr, Date newDep, Date newArr) {
        return (oldDep.compareTo(newArr) <= 0) && (newDep.compareTo(oldArr) <= 0);
    }

    private boolean validatePassenger(String passengerId) {
        return passengerRepository.existsById(passengerId);
    }

    /**
     * Check whether all flights withing flightList exist and neither of them overlap with each other
     *
     * @param flights List of Flights
     * @return return true if all flights are valid else false
     */
    private boolean validateFlights(List<String> flights) throws ParseException {
        if (flights.size() == 1) {
            return flightRepository.existsById(flights.get(0));
        } else {
            for (int i = 0; i < flights.size() - 1; i++) {
                for (int j = i + 1; j < flights.size(); j++) {
                    if (flightRepository.existsById(flights.get(i)) && flightRepository.existsById(flights.get(j))) {
                        Flight one = flightRepository.findByFlightNumberEquals(flights.get(i));
                        Flight two = flightRepository.findByFlightNumberEquals(flights.get(j));
                        if (checkFlightDatesClash(formatDate(one.getDepartureTime()), formatDate(one.getArrivalTime()), formatDate(two.getDepartureTime()), formatDate(two.getArrivalTime()))) {
                            return false;
                        }
                    } else
                        return false;
                }
            }
        }
        return true;
    }

    private boolean checkSeatsLeft(List<Flight> flights) {
        for (Flight flight : flights) {
            if (flight.getSeatsLeft() < 1)
                return false;
        }
        return true;
    }

    private List<Flight> getFlights(List<String> flightIds) {
        List<Flight> flights = new ArrayList<Flight>();
        for (String flightId : flightIds) {
            flights.add(flightRepository.findByFlightNumberEquals(flightId));
        }
        return flights;
    }

    private boolean checkReservationForExistingFlights(Reservation reservation, List<Flight> flightList) {
        List<Flight> existingFlights = reservation.getFlights();
        for (Flight flight : flightList) {
            if (!existingFlights.contains(flight))
                return false;
        }
        return true;
    }

    public List<Reservation> findByAllParam(String flightNumber, String from, String to, String passengerId){
        return reservationRepository.findByAllParam(flightNumber,to,from,passengerId);
    }
}

