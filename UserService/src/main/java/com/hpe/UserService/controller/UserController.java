package com.hpe.UserService.controller;


import com.hpe.UserService.dto.UserDTO;
import com.hpe.UserService.dto.UserResponse;
import com.hpe.UserService.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/userDetails")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/createUser")
    public ResponseEntity<UserResponse> createUser(@RequestBody UserDTO userDTO){
        return userService.createUser(userDTO);
    }

    @PostMapping("/updateMembership/{userName}")
    public ResponseEntity<UserResponse> updateMembership(@PathVariable String userName){
        return userService.updateMembership(userName);
    }

    @GetMapping("/membership/{userName}")
    public ResponseEntity<UserResponse> isMember(@PathVariable String userName){
        return userService.isMember(userName);
    }



}
