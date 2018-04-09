package com.minisocial.book.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.minisocial.book.entity.Passenger;
import com.minisocial.book.repository.PassengerRepository;
import com.minisocial.book.service.PassengerService;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
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
    ResponseEntity<?> getUser(@PathVariable("id") String id) {
        // This returns a JSON with the users
        Response.ResponseBuilder rBuild = null;
        String p = passengerService.getPassengerById(id);
        System.out.println(passengerService.getPassengerById(id)+"adhjbnsdjhbashj");

        return new ResponseEntity<Object>(p, HttpStatus.OK);

    }

    @GetMapping(path="/{id}", params = "xml", produces = MediaType.APPLICATION_XML_VALUE)
//    @Consumes(MediaType.ALL_VALUE)
    @Produces(MediaType.APPLICATION_XML_VALUE)
    public @ResponseBody ResponseEntity<?> getUserXML(@PathVariable("id") String id, @RequestParam Map<String, String> params) {
        // This returns a XML/JSON based on contentconfig.
        String resp = passengerService.getPassengerById(id, MediaType.APPLICATION_XML);
        return new ResponseEntity<Object>(resp, HttpStatus.OK);
    }

    @PostMapping(path = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<?> create(@RequestParam Map<String, String> params) {

        String fname = params.get("firstname");
        String lname = params.get("lastname");
        String age = params.get("age");
        String gender = params.get("gender");
        String phone = params.get("phone");

        Passenger p = new Passenger();
        p.setAge(Integer.parseInt(age));
        p.setFirstname(fname);
        p.setLastname(lname);
        p.setGender(gender);
        p.setPhone(phone);

        String resp = passengerService.createPassenger(p);
        return new ResponseEntity<Object>(resp, HttpStatus.OK);
    }
    
    @PutMapping(path="/{id}",produces=MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<?> updatePassanger(@PathVariable("id") String id,@RequestParam Map<String, String> params) throws JsonProcessingException, JSONException {

        String fname = params.get("firstname");
        String lname = params.get("lastname");
        int age = Integer.parseInt(params.get("age"));
        String gender = params.get("gender");
        String phone = params.get("phone");

        String resp=passengerService.updatePassenger(id, fname, lname, age, gender, phone);
       
        return new ResponseEntity<Object>(resp, HttpStatus.OK);
    }
    
    @DeleteMapping(path="/{id}",produces=MediaType.APPLICATION_JSON_VALUE)
	public String deletePassenger(@PathVariable("id") String id) throws JSONException{
    
    	return passengerService.delete(id);
	}
    
    
    
//    @PostMapping(path = "/checkLogin", consumes = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<?> login(@RequestBody String user, HttpSession session) {
//        try {
//
//        JSONObject userObj = new JSONObject(user);
//        System.out.println(userObj);
//        session.setAttribute("name", userObj.getString("inputUsername"));
//
//        Users userVO = userService.login(userObj.getString("inputUsername"), userObj.getString("inputPassword"))
//                .orElseThrow(IllegalArgumentException::new);
//        System.out.println(userVO.getUserEmail());
//
//            session.setAttribute("userId", userVO.getId());
//        return new ResponseEntity(userVO, HttpStatus.OK);
//        }catch (Exception ex){
//            System.out.println(ex);
//            return new ResponseEntity(ex, HttpStatus.BAD_REQUEST);
//        }
//    }
//
//    @PostMapping(path = "/listFiles", consumes = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<?> listFiles(@RequestBody String req, HttpSession session) {
//        try {
//
//        JSONObject userObj = new JSONObject(req);
//        System.out.println(userObj);
//        int userId = Integer.parseInt(session.getAttribute("userId").toString()) ;
//        Users user = userService.findUserById(userId).orElse(new Users());
//        List<FileDetails> fileDetailsList = fileDetailsService.listFiles(user);
//
//        for(FileDetails file : fileDetailsList){
//            System.out.println(file.getFileName());
//        }
//
//        return new ResponseEntity(fileDetailsList, HttpStatus.OK);
//        }catch (Exception ex){
//            System.out.println(ex);
//            return new ResponseEntity(ex, HttpStatus.BAD_REQUEST);
//        }
//    }
//
//    @PostMapping(path = "/registerUser", consumes = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<?> registration(@RequestBody String user, HttpSession session) {
//        try {
//            JSONObject userObj = new JSONObject(user);
//            Users newuser = new Users();
//            newuser.setUserEmail(userObj.getString("userEmail"));
//            newuser.setUserPassword(userObj.getString("password"));
//            newuser.setUserFirstName(userObj.getString("firstName"));
//            newuser.setUserLastName(userObj.getString("lastName"));
//
//            DateFormat formatter = new SimpleDateFormat("YYYY-MM-DD");
//            newuser.setUserBDate((Date) formatter.parse(userObj.getString("dob")));
//            System.out.println(newuser.getUserBDate());
//            System.out.println(userObj.getString("dob"));
//
//            userService.addUser(newuser);
//            return new ResponseEntity(newuser,HttpStatus.OK);
//        } catch (ParseException err) {
//            System.out.println(err);
//            return new ResponseEntity<Object>(err,HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//        catch (Exception err) {
//            System.out.println(err);
//            return new ResponseEntity<Object>(err,HttpStatus.BAD_REQUEST);
//        }
//    }
}
