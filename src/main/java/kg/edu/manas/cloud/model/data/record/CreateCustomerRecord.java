package kg.edu.manas.cloud.model.data.record;

import kg.edu.manas.cloud.model.data.enums.Role;
import kg.edu.manas.cloud.model.entity.Customer;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

public record CreateCustomerRecord(String username, String password, String name, LocalDate birthDate, Double height, Double weight, Role role) {
    public Customer toCustomer(PasswordEncoder passwordEncoder) {
        return Customer.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .name(name)
                .birthDate(birthDate)
                .height(height)
                .weight(weight)
                .role(role).build();
    }
}
