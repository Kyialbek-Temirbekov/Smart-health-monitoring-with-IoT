package kg.edu.manas.cloud.repository;

import kg.edu.manas.cloud.data.enums.MetricType;
import kg.edu.manas.cloud.entity.Config;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConfigRepository extends JpaRepository<Config, Long> {
    List<Config> findAllByName(MetricType name);
}
