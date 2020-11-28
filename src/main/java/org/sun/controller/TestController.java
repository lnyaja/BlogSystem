package org.sun.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {


    @GetMapping("/hello")
    public String helloWorld(){
        System.out.println("hello world...");
        return "hello";
    }

//    @GetMapping("/test-json")
//    public User testJson(){
////        User user = new User("特朗普",73,"male");
////        House house = new House("白宫","白色");
////        user.setHouse(house);
////        return user;
//    }
}
