package kg.edu.manas.cloud.repository;

import kg.edu.manas.cloud.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    <T> Optional<T> findByUsername(String username, Class<T> type);
    Optional<Customer> findByUsername(String username);
}
