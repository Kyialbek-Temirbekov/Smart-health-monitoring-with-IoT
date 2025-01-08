package kg.edu.manas.cloud.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customer_id_gen")
    @SequenceGenerator(name = "customer_id_gen", sequenceName = "customer_seq", allocationSize = 1)
    private Long id;
    private String username;
    private String password;
    private String name;
    private LocalDate birthDate;
    private boolean isEnabled;
    @OneToMany(mappedBy = "customer")
    private List<Device> devices;
}
