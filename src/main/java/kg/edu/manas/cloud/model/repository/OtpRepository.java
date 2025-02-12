package kg.edu.manas.cloud.model.repository;

import kg.edu.manas.cloud.model.entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OtpRepository extends JpaRepository<Otp, String> {
}
