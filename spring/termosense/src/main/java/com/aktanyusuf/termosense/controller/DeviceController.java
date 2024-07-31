package com.aktanyusuf.termosense.controller;

import com.aktanyusuf.termosense.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    @PostMapping("/addDevice")
    public ResponseEntity<Map<String, String>> addDevice(@RequestBody Map<String, String> deviceDetails) {
        String token = deviceDetails.get("token");
        String deviceName = deviceDetails.get("deviceName");
        String deviceMac = deviceDetails.get("deviceMac");

        String result = deviceService.addDevice(token, deviceName, deviceMac);

        Map<String, String> response = new HashMap<>();
        if ("Device added successfully".equals(result)) {
            response.put("status", "true");
            response.put("message", result);
            return ResponseEntity.ok(response);
        } else {
            response.put("status", "false");
            response.put("message", result);
            return ResponseEntity.badRequest().body(response);
        }
    }
}
