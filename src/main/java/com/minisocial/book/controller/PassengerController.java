package com.minisocial.book.controller;

import com.minisocial.book.entity.Passenger;
import com.minisocial.book.service.PassengerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.util.Map;

@Controller
//@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping(path = "/")
public class PassengerController {

    @Autowired
    private PassengerService passengerService;

    @GetMapping(path="/passenger/{id}")
    @Produces({MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public @ResponseBody
    Response getUser(@PathVariable("id") int id,@RequestParam Map<String, String> params ) {
        // This returns a JSON with the users
        Response.ResponseBuilder rBuild = null;
        Passenger p = passengerService.getPassengerById(id);
        System.out.println(passengerService.getPassengerById(id));
        System.out.println(params.containsKey("xml"));

        if(params.containsKey("xml")){
             rBuild = Response.ok(p, MediaType.APPLICATION_XML_VALUE);
        }else{
             rBuild = Response.ok(p, MediaType.APPLICATION_JSON_VALUE);
        }
        return rBuild.build();

    }

//    @GetMapping(path="/passenger/{id}?xml=true")
//    @Produces(MediaType.APPLICATION_XML_VALUE)
//    public @ResponseBody Passenger getUserXML(@PathVariable("id") int id) {
//        // This returns a JSON with the users
//        System.out.println(passengerService.getPassengerById(params.get("id")));
//
//        return passengerService.getPassengerById(params.get("id"));
//    }

    @PostMapping(path="/passenger", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Passenger login(@RequestParam Map<String, String> params)
    {

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

        passengerService.createPassenger(p);
        return p;
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
