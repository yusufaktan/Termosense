package com.aktanyusuf.termosense.service;

import com.aktanyusuf.termosense.model.SensorData;
import com.aktanyusuf.termosense.model.UserDevices;
import com.aktanyusuf.termosense.repository.SensorDataRepository;
import com.aktanyusuf.termosense.repository.UserDevicesRepository;
import com.aktanyusuf.termosense.session.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SensorDataService {

    @Autowired
    private SensorDataRepository sensorDataRepository;

    @Autowired
    private UserDevicesRepository userDevicesRepository;

    @Autowired
    private SessionManager sessionManager;

    public Map<String, Object> getSensorData(String token, String deviceMac) {
        String email = sessionManager.getEmailByToken(token);
        if (email == null) {
            return null;
        }

        UserDevices userDevice = userDevicesRepository.findByDeviceMacAndUser_Email(deviceMac, email);
        if (userDevice == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "false");
            response.put("message", "Device not found for the user");
            return response;
        }

        List<SensorData> sensorDataList = sensorDataRepository.findTopByMacOrderByDateDesc(deviceMac);

        Map<String, Object> result = new HashMap<>();
        if (sensorDataList.isEmpty()) {
            result.put("status", "false");
            result.put("message", "No sensor data found for the device");
        } else {
            result.put(deviceMac, sensorDataList);
            result.put("status", "true");
        }
        return result;
    }
}
