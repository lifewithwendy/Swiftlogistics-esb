package com.swiftlogistics.esb.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RosService {
    private final RestTemplate restTemplate;

    public RosService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String optimizeRoute(String address) {
        return restTemplate.getForObject("http://localhost:5002/ros?address=" + address, String.class);
    }
}
