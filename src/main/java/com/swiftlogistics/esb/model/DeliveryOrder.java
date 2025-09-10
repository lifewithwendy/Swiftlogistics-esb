package com.swiftlogistics.esb.model;

import java.util.Map;

public class DeliveryOrder {
    private String orderId;
    private String clientId;
    private String pickupAddress;
    private String deliveryAddress;
    private String packageWeight;
    private String status;
    private Map<String, Object> metadata;

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }
    public String getPickupAddress() { return pickupAddress; }
    public void setPickupAddress(String pickupAddress) { this.pickupAddress = pickupAddress; }
    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }
    public String getPackageWeight() { return packageWeight; }
    public void setPackageWeight(String packageWeight) { this.packageWeight = packageWeight; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }

    @Override
    public String toString() {
        return "DeliveryOrder{" +
                "orderId='" + orderId + '\'' +
                ", clientId='" + clientId + '\'' +
                ", pickupAddress='" + pickupAddress + '\'' +
                ", deliveryAddress='" + deliveryAddress + '\'' +
                '}';
    }
}
