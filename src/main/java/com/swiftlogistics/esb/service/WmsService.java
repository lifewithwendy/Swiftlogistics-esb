package com.swiftlogistics.esb.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

@Service
public class WmsService {

    private static final String WMS_HOST = "localhost";
    private static final int WMS_PORT = 5003;
    private static final int WAREHOUSE_STATUS_REQ = 0x06;
    private static final int WAREHOUSE_STATUS_RESP = 0x07;

    private final ObjectMapper objectMapper;

    public WmsService() {
        this.objectMapper = new ObjectMapper();
    }

    public String checkWarehouseStatus() {
        try (Socket socket = new Socket(WMS_HOST, WMS_PORT)) {

            // Prepare request
            Map<String, Object> requestData = new HashMap<>();
            requestData.put("request_id", System.currentTimeMillis());

            String jsonPayload = objectMapper.writeValueAsString(requestData);
            byte[] payloadBytes = jsonPayload.getBytes("UTF-8");

            // Create header (message type + payload length)
            ByteBuffer header = ByteBuffer.allocate(8);
            header.putInt(WAREHOUSE_STATUS_REQ);
            header.putInt(payloadBytes.length);

            // Send message
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.write(header.array());
            out.write(payloadBytes);
            out.flush();

            // Read response
            DataInputStream in = new DataInputStream(socket.getInputStream());

            // Read header
            int responseType = in.readInt();
            int responseLength = in.readInt();

            // Read payload
            byte[] responsePayload = new byte[responseLength];
            in.readFully(responsePayload);

            if (responseType == WAREHOUSE_STATUS_RESP) {
                String responseJson = new String(responsePayload, "UTF-8");
                return extractWarehouseInfo(responseJson);
            } else {
                return "Unexpected response from WMS";
            }

        } catch (Exception e) {
            return "Error connecting to WMS: " + e.getMessage();
        }
    }

    private String extractWarehouseInfo(String jsonResponse) {
        try {
            Map<String, Object> response = objectMapper.readValue(jsonResponse, new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {});
            if (response.containsKey("total_packages")) {
                return "Warehouse status: " + response.get("total_packages") + " packages";
            }
            return "Warehouse operational";
        } catch (Exception e) {
            return "Warehouse status retrieved";
        }
    }
}