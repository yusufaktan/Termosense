package com.aktanyusuf.termosense.repository;

import com.aktanyusuf.termosense.model.AllDevices;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AllDevicesRepository extends JpaRepository<AllDevices, Long> {
    boolean existsByDeviceMac(String deviceMac);
}
