package com.hpe;

import org.springframework.stereotype.Service;

@Service
public class TestService {
    public String testFunction(String testVariable) {
        System.out.println(testVariable);
        return "I am routing fine and received text is: "+ testVariable + " and I am Working";
    }
}
