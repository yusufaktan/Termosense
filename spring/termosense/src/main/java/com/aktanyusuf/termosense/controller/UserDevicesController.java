package com.aktanyusuf.termosense.controller;

import com.aktanyusuf.termosense.model.UserDevices;
import com.aktanyusuf.termosense.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserDevicesController {

    @Autowired
    private DeviceService deviceService;

    @PostMapping("/userdevices")
    public ResponseEntity<Map<String, Object>> getUserDevices(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        List<UserDevices> userDevices = deviceService.getUserDevices(token);

        Map<String, Object> response = new HashMap<>();
        if (userDevices == null) {
            response.put("status", "false");
            response.put("message", "Invalid token");
            return ResponseEntity.badRequest().body(response);
        }

        if (userDevices.isEmpty()) {
            response.put("status", "false");
            response.put("message", "No devices found for the user");
            return ResponseEntity.ok(response);
        }

        Map<String, String> devices = new HashMap<>();
        for (UserDevices device : userDevices) {
            devices.put(device.getDeviceName(), device.getDeviceMac());
        }

        response.put("status", "true");
        response.put("devices", devices);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/deleteDevice")
    public ResponseEntity<Map<String, String>> deleteDevice(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String deviceMac = request.get("deviceMac");
        String result = deviceService.deleteDevice(token, deviceMac);

        Map<String, String> response = new HashMap<>();
        if ("Device deleted successfully".equals(result)) {
            response.put("status", "true");
            response.put("message", result);
        } else {
            response.put("status", "false");
            response.put("message", result);
            return ResponseEntity.badRequest().body(response);
        }

        return ResponseEntity.ok(response);
    }
}
