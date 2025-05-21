package kg.edu.manas.cloud.model.repository;

import kg.edu.manas.cloud.model.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Device, String> {
    @Query("select d.id from Device d where d.customer.username = :username")
    String getDeviceIdByUsername(String username);
    <T> Optional<T> findByCustomerId(Long customerId, Class<T> type);
}
