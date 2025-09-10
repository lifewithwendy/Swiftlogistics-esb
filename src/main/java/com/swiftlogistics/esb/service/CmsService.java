package com.swiftlogistics.esb.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CmsService {
    private final RestTemplate restTemplate;

    public CmsService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String fetchClientData(String clientId) {
        // In real case: SOAP -> REST adapter
        return restTemplate.getForObject("http://localhost:5001/cms?clientId=" + clientId, String.class);
    }
}
