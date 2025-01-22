package kg.edu.manas.cloud.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import kg.edu.manas.cloud.model.data.record.CreateCustomerRecord;
import kg.edu.manas.cloud.model.data.record.CustomerRecord;
import kg.edu.manas.cloud.model.data.record.EmailMessageRecord;
import kg.edu.manas.cloud.model.data.record.OtpRecord;
import kg.edu.manas.cloud.model.entity.Customer;
import kg.edu.manas.cloud.model.entity.Otp;
import kg.edu.manas.cloud.exception.ConflictException;
import kg.edu.manas.cloud.model.repository.CustomerRepository;
import kg.edu.manas.cloud.model.repository.OtpRepository;
import kg.edu.manas.cloud.security.JwtService;
import kg.edu.manas.cloud.util.NumericTokenGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final OtpRepository otpRepository;
    private final EmailNotificationService emailNotificationService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    private static final String REGISTRATION_OTP_SUB = "One time password for registration";
    private static final long OTP_EXPIRY_HOUR = 3;
    private static final int OTP_LENGTH = 4;

    @Transactional
    public void createCustomer(CreateCustomerRecord createCustomerRecord) {
        Optional<Customer> optionalCustomer = customerRepository.findByUsername(createCustomerRecord.username());

        if(optionalCustomer.isPresent() && optionalCustomer.get().isEnabled()) {
            throw new ConflictException("User already exist: " + createCustomerRecord.username());
        } else {
            String otpValue = NumericTokenGenerator.generateToken(OTP_LENGTH);
            CompletableFuture.runAsync(() ->
                    emailNotificationService.sendMessage(new EmailMessageRecord(createCustomerRecord.username(), REGISTRATION_OTP_SUB, otpValue))
            );
            if(optionalCustomer.isPresent()) {
                Customer customer = optionalCustomer.get();
                customer.setUsername(createCustomerRecord.username());
                customer.setPassword(passwordEncoder.encode(createCustomerRecord.password()));
                customer.setName(createCustomerRecord.name());
                customer.setBirthDate(createCustomerRecord.birthDate());

                /*Otp otp = optionalCustomer.get().getOtp();*/
                Otp otp = otpRepository.findById(optionalCustomer.get().getId()).orElseThrow(EntityNotFoundException::new);
                otp.setValue(otpValue);
                otp.setExpiryTime(LocalDateTime.now().plusHours(OTP_EXPIRY_HOUR));
            } else {
                Customer customer = createCustomerRecord.toCustomer(passwordEncoder);
                Otp otp = Otp.builder()
                        .value(otpValue)
                        .expiryTime(LocalDateTime.now().plusHours(OTP_EXPIRY_HOUR)).build();
                /*customer.setOtp(otp);*/
                otp.setCustomer(customer);
                customerRepository.save(customer);
                otpRepository.save(otp);
            }
        }
    }

    @Transactional
    public void confirmEmail(OtpRecord otpRecord) {
        Optional<Customer> optionalCustomer = customerRepository.findByUsername(otpRecord.email());
        if(optionalCustomer.isPresent()) {
            Customer customer = optionalCustomer.get();
            Otp otp = otpRepository.findById(customer.getId()).orElseThrow(EntityNotFoundException::new);
            if(otp.isExpired()) {
                throw new CredentialsExpiredException("One time password expired");
            }
            else if(/*customer.getOtp().getValue().*/"0000".equals(otpRecord.otp())) {
                /*customer.setOtp(null);*/
                otpRepository.deleteById(customer.getId());
                customer.setEnabled(true);
            } else {
                throw new BadCredentialsException("One time password is invalid");
            }
        } else {
            throw new EntityNotFoundException("User not found by email: " + otpRecord.email());
        }
    }

    public String signIn(Authentication authentication) {
        return jwtService.createAccessToken(authentication.getName(), populateAuthorities(authentication.getAuthorities()));
    }

    private String populateAuthorities(Collection<? extends GrantedAuthority> authorities) {
        Set<String> authoritiesSet = new HashSet<>();
        for(GrantedAuthority authority : authorities)
            if (authority.getAuthority().startsWith("ROLE_")) {
                authoritiesSet.add(authority.getAuthority());
            }
        return String.join(",", authoritiesSet);
    }

    public Customer getLoggedInUser() {
        String username = getPrincipal().getUsername();
        return customerRepository.findByUsername(username).orElseThrow(
                () -> new EntityNotFoundException("User not found by email")
        );
    }

    public CustomerRecord getCustomer() {
        String username = getPrincipal().getUsername();
        return customerRepository.findByUsername(username, CustomerRecord.class).orElseThrow(
                () -> new EntityNotFoundException("User not found by email")
        );
    }

    public UserDetails getPrincipal() {
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(principal instanceof UserDetails) {
            return (UserDetails) principal;
        } else {
            throw new AuthenticationException("Unauthorized") {
            };
        }
    }

    public void delete(Long id) {
        customerRepository.deleteById(id);
    }
}
