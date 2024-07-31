package com.aktanyusuf.termosense.repository;

import com.aktanyusuf.termosense.model.UserDevices;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDevicesRepository extends JpaRepository<UserDevices, Long> {
    boolean existsByDeviceMacAndUser_Email(String deviceMac, String email);
    UserDevices findByDeviceMacAndUser_Email(String deviceMac, String email);
    List<UserDevices> findByUser_Email(String email);
}
