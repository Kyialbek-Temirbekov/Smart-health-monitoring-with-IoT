package kg.edu.manas.cloud.data.record;

import kg.edu.manas.cloud.entity.Customer;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

public record CreateCustomerRecord(String username, String password, String name, LocalDate birthDate) {
    public Customer toCustomer(PasswordEncoder passwordEncoder) {
        return Customer.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .name(name)
                .birthDate(birthDate).build();
    }
}
