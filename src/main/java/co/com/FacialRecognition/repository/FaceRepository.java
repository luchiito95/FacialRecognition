package co.com.FacialRecognition.repository;

import co.com.FacialRecognition.model.Face;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FaceRepository extends JpaRepository<Face, Long> {
}
