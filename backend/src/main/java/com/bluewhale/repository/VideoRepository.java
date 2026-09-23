// repository/VideoRepository.java
package com.bluewhale.repository;

import com.bluewhale.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {
    Optional<Video> findByType(String type);
    Optional<Video> findByFilename(String filename);
}