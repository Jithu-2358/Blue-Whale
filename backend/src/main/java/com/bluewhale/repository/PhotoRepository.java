// repository/PhotoRepository.java
package com.bluewhale.repository;

import com.bluewhale.model.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {
    List<Photo> findByDeviceIdOrderByCapturedAtDesc(String deviceId);
    long countByDeviceId(String deviceId);
}