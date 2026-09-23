// repository/CommandRepository.java
package com.bluewhale.repository;

import com.bluewhale.model.Command;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CommandRepository extends JpaRepository<Command, Long> {
    List<Command> findByDeviceIdAndStatusOrderByIdAsc(String deviceId, String status);
    List<Command> findByDeviceIdOrderByIssuedAtDesc(String deviceId);
}