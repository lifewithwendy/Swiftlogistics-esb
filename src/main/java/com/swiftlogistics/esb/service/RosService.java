package com.swiftlogistics.esb.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

@Service
public class RosService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private static final String ROS_API_URL = "http://localhost:5002/api/v1/routes/optimize";

    public RosService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.objectMapper = new ObjectMapper();
    }

    public String optimizeRoute(String address) {
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("vehicle_id", "VEH001");
            requestBody.put("delivery_addresses", createDeliveryAddresses(address));
            requestBody.put("priority", "normal");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            String response = restTemplate.postForObject(ROS_API_URL, request, String.class);

            return extractRouteInfo(response);
        } catch (Exception e) {
            return "Error optimizing route: " + e.getMessage();
        }
    }

    private Object[] createDeliveryAddresses(String address) {
        Map<String, Object> deliveryAddress = new HashMap<>();
        deliveryAddress.put("address", address);
        deliveryAddress.put("lat", 6.9271); // Default Colombo coordinates
        deliveryAddress.put("lng", 79.8612);
        deliveryAddress.put("order_id", "ORD" + System.currentTimeMillis());

        return new Object[] { deliveryAddress };
    }

    private String extractRouteInfo(String jsonResponse) {
        try {
            Map<String, Object> response = objectMapper.readValue(jsonResponse, new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {});
            if (response.containsKey("route_id")) {
                return "Route optimized: " + response.get("route_id");
            }
            return "Route optimization completed";
        } catch (Exception e) {
            return "Route processed";
        }
    }
}