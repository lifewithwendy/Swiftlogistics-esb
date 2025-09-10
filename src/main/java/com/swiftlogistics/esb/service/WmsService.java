package com.swiftlogistics.esb.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WmsService {
    private final RestTemplate restTemplate;

    public WmsService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String checkWarehouseStatus() {
        return restTemplate.getForObject("http://localhost:5003/wms/status", String.class);
    }
}
