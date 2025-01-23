package kg.edu.manas.cloud.model.repository;

import kg.edu.manas.cloud.model.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    <T> Optional<T> findByUsername(String username, Class<T> type);
    Optional<Customer> findByUsername(String username);
    @Query("SELECT c.birthDate FROM Device d JOIN FETCH d.customer c WHERE d.id = :deviceId")
    LocalDate getBirthDate(String deviceId);
}
