// repository/DeviceRepository.java
package com.bluewhale.repository;

import com.bluewhale.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DeviceRepository extends JpaRepository<Device, String> {
    List<Device> findByStatus(String status);
    List<Device> findByMalwareActiveTrue();
    long countByStatus(String status);
}