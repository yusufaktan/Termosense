package com.aktanyusuf.termosense.repository;

import com.aktanyusuf.termosense.model.SensorData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SensorDataRepository extends JpaRepository<SensorData, Long> {
    List<SensorData> findTopByMacOrderByDateDesc(String mac);
}
