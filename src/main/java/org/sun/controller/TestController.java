package org.sun.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.sun.pojo.Test;
import org.sun.response.ResponseResult;
import org.sun.response.ResponseState;

@Slf4j
@RestController
@RequestMapping("/test")
public class TestController {


    @GetMapping("/hello")
    public ResponseResult helloWorld(){
        System.out.println("hello world...");
        return ResponseResult.SUCCESS().setData("hello world");
    }

    @PostMapping("/test-login")
    public ResponseResult testLogin(@RequestBody Test user){
        log.info("user name -== > " + user.getUserName());
        log.info("password -== > " + user.getPassword());
        ResponseResult loginSuccess = new ResponseResult(ResponseState.LOGIN_SUCCESS);
        return loginSuccess.setData(user);
    }
}
