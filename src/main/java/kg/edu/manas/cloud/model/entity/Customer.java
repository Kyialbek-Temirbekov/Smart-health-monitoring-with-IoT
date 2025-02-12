package kg.edu.manas.cloud.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Customer implements UserDetails {
    @Id
    @UuidGenerator
    private String id;
    private String username;
    private String password;
    private String name;
    private LocalDate birthDate;
    private boolean isEnabled;
    @OneToMany(mappedBy = "customer")
    private List<Device> devices;
    @OneToMany(mappedBy = "doctor")
    private List<Customer> patients = new ArrayList<>();
    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Customer doctor;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }
}
