package com.aktanyusuf.termosense.controller;

import com.aktanyusuf.termosense.service.SensorDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class SensorDataController {

    @Autowired
    private SensorDataService sensorDataService;

    @PostMapping("/sensordata")
    public ResponseEntity<Map<String, Object>> getSensorData(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String deviceMac = request.get("deviceMac");
        Map<String, Object> response = sensorDataService.getSensorData(token, deviceMac);

        if (response == null) {
            response = Map.of("status", "false", "message", "Invalid token");
            return ResponseEntity.badRequest().body(response);
        }

        if ("false".equals(response.get("status"))) {
            return ResponseEntity.badRequest().body(response);
        }

        return ResponseEntity.ok(response);
    }
}
