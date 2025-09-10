package com.swiftlogistics.esb.controller;

import com.swiftlogistics.esb.service.CmsService;
import com.swiftlogistics.esb.service.RosService;
import com.swiftlogistics.esb.service.WmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EsbController {

    private static final Logger logger = LoggerFactory.getLogger(EsbController.class);

    private final CmsService cmsService;
    private final RosService rosService;
    private final WmsService wmsService;

    public EsbController(CmsService cmsService, RosService rosService, WmsService wmsService) {
        this.cmsService = cmsService;
        this.rosService = rosService;
        this.wmsService = wmsService;
    }

    @GetMapping("/esb/processOrder")
    public String processOrder(@RequestParam("clientId") String clientId,
                               @RequestParam("address") String address) {
        logger.info("Processing order for clientId: {} and address: {}", clientId, address);

        try {
            String cmsResponse = cmsService.fetchClientData(clientId);
            logger.info("CMS Response: {}", cmsResponse);

            String rosResponse = rosService.optimizeRoute(address);
            logger.info("ROS Response: {}", rosResponse);

            String wmsResponse = wmsService.checkWarehouseStatus();
            logger.info("WMS Response: {}", wmsResponse);

            String result = "CMS: " + cmsResponse + " | ROS: " + rosResponse + " | WMS: " + wmsResponse;
            logger.info("Final response: {}", result);

            return result;
        } catch (Exception e) {
            logger.error("Error processing order: ", e);
            return "Error processing order: " + e.getMessage();
        }
    }
}