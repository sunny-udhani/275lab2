package com.minisocial.book.service;

import com.minisocial.book.entity.Passenger;
import com.minisocial.book.repository.PassengerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class PassengerService {

    @Autowired
    private PassengerRepository passengerRepository;

    public Passenger getPassengerById(int id){
        return passengerRepository.findById(id);
    }

    @Transactional
    public boolean createPassenger(Passenger p){
        passengerRepository.save(p);
        return true;
    }

//    public Iterable<Users> getAllUsers(){
//        return pr.findAll();
//    }


}
