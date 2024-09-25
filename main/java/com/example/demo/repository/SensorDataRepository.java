package com.example.demo.repository;

import com.example.demo.entity.SensorData;  // Rätt import
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SensorDataRepository extends JpaRepository<SensorData, Long> {

}
