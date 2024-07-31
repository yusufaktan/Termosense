package com.aktanyusuf.termosense.service;

import com.aktanyusuf.termosense.model.UserDevices;
import com.aktanyusuf.termosense.repository.AllDevicesRepository;
import com.aktanyusuf.termosense.repository.UserDevicesRepository;
import com.aktanyusuf.termosense.repository.UserRepository;
import com.aktanyusuf.termosense.session.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeviceService {

    @Autowired
    private AllDevicesRepository allDevicesRepository;

    @Autowired
    private UserDevicesRepository userDevicesRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SessionManager sessionManager;

    public String addDevice(String token, String deviceName, String deviceMac) {
        String email = sessionManager.getEmailByToken(token);
        if (email == null) {
            return "Invalid token";
        }

        if (!allDevicesRepository.existsByDeviceMac(deviceMac)) {
            return "Device not found in all devices";
        }

        if (userDevicesRepository.existsByDeviceMacAndUser_Email(deviceMac, email)) {
            return "Device already added to your account";
        }

        UserDevices userDevice = new UserDevices();
        userDevice.setDeviceName(deviceName);
        userDevice.setDeviceMac(deviceMac);
        userDevice.setUser(userRepository.findByEmail(email));

        userDevicesRepository.save(userDevice);
        return "Device added successfully";
    }

    public List<UserDevices> getUserDevices(String token) {
        String email = sessionManager.getEmailByToken(token);
        if (email == null) {
            return null;
        }
        return userDevicesRepository.findByUser_Email(email);
    }

    public String deleteDevice(String token, String deviceMac) {
        String email = sessionManager.getEmailByToken(token);
        if (email == null) {
            return "Invalid token";
        }

        UserDevices userDevice = userDevicesRepository.findByDeviceMacAndUser_Email(deviceMac, email);
        if (userDevice == null) {
            return "Device not found in user's account";
        }

        userDevicesRepository.delete(userDevice);
        return "Device deleted successfully";
    }
}
