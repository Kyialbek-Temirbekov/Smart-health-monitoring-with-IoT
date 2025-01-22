package kg.edu.manas.cloud.model.repository;

import kg.edu.manas.cloud.model.entity.Config;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigRepository extends JpaRepository<Config, Long> {
}
