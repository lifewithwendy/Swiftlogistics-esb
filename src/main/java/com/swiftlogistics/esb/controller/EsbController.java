package com.swiftlogistics.esb.controller;

import com.swiftlogistics.esb.service.CmsService;
import com.swiftlogistics.esb.service.RosService;
import com.swiftlogistics.esb.service.WmsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EsbController {

    private final CmsService cmsService;
    private final RosService rosService;
    private final WmsService wmsService;

    public EsbController(CmsService cmsService, RosService rosService, WmsService wmsService) {
        this.cmsService = cmsService;
        this.rosService = rosService;
        this.wmsService = wmsService;
    }

    @GetMapping("/esb/processOrder")
    public String processOrder(@RequestParam String clientId, @RequestParam String address) {
        String cmsResponse = cmsService.fetchClientData(clientId);
        String rosResponse = rosService.optimizeRoute(address);
        String wmsResponse = wmsService.checkWarehouseStatus();

        return "CMS: " + cmsResponse + " | ROS: " + rosResponse + " | WMS: " + wmsResponse;
    }
}
