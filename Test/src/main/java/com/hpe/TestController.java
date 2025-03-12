package com.hpe;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test/")
public class TestController {
    @Autowired
    private TestService testService;

    @PostMapping("/test1/{testVariable}")
    public String test(@PathVariable String testVariable){
        return testService.testFunction(testVariable);
    }
}
