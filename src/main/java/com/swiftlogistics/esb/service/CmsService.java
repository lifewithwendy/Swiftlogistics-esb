package com.swiftlogistics.esb.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@Service
public class CmsService {

    private final RestTemplate restTemplate;
    private static final String CMS_SOAP_URL = "http://localhost:5001/cms/soap";

    public CmsService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String fetchClientData(String clientId) {
        try {
            String soapRequest = createGetClientInfoSoapRequest(clientId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_XML);
            headers.set("SOAPAction", "GetClientInfo");

            HttpEntity<String> request = new HttpEntity<>(soapRequest, headers);
            String response = restTemplate.postForObject(CMS_SOAP_URL, request, String.class);

            return extractClientInfo(response);
        } catch (Exception e) {
            return "Error fetching client data: " + e.getMessage();
        }
    }

    private String createGetClientInfoSoapRequest(String clientId) {
        return String.format(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
                "               xmlns:cms=\"http://swiftlogistics.lk/cms\">\n" +
                "    <soap:Header/>\n" +
                "    <soap:Body>\n" +
                "        <cms:GetClientInfo>\n" +
                "            <cms:ClientId>%s</cms:ClientId>\n" +
                "        </cms:GetClientInfo>\n" +
                "    </soap:Body>\n" +
                "</soap:Envelope>\n",
                clientId);
    }

    private String extractClientInfo(String soapResponse) {
        // Simple XML parsing - extract client name
        if (soapResponse != null && soapResponse.contains("<cms:Name>")) {
            int start = soapResponse.indexOf("<cms:Name>") + 10;
            int end = soapResponse.indexOf("</cms:Name>");
            if (end > start) {
                return soapResponse.substring(start, end);
            }
        }
        return "Client data retrieved";
    }
}