package kg.edu.manas.cloud.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Otp {
    @Id
    private Long customerId;
    private String value;
    private LocalDateTime expiryTime;
    @OneToOne
    @MapsId
    @JoinColumn(name = "customer_id")
    private Customer customer;

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryTime);
    }
}
