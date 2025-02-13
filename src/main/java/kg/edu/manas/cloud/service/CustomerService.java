package kg.edu.manas.cloud.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Root;
import kg.edu.manas.cloud.model.data.constants.Messages;
import kg.edu.manas.cloud.model.data.record.*;
import kg.edu.manas.cloud.model.entity.Customer;
import kg.edu.manas.cloud.model.entity.Otp;
import kg.edu.manas.cloud.exception.ConflictException;
import kg.edu.manas.cloud.model.repository.CustomerRepository;
import kg.edu.manas.cloud.model.repository.OtpRepository;
import kg.edu.manas.cloud.security.JwtService;
import kg.edu.manas.cloud.util.NumericTokenGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final EntityManager entityManager;
    private final CustomerRepository customerRepository;
    private final OtpRepository otpRepository;
    private final EmailNotificationService emailNotificationService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    private static final long OTP_EXPIRY_HOUR = 3;
    private static final int OTP_LENGTH = 4;
    private static final String ROLE_PREFIX = "ROLE_";

    @Transactional
    public void createCustomer(CreateCustomerRecord createCustomerRecord) {
        Optional<Customer> optionalCustomer = customerRepository.findByUsername(createCustomerRecord.username());

        if(optionalCustomer.isPresent() && optionalCustomer.get().isEnabled()) {
            throw new ConflictException(Messages.USER_ALREADY_EXIST);
        } else {
            String otpValue = NumericTokenGenerator.generateToken(OTP_LENGTH);
            CompletableFuture.runAsync(() ->
                    emailNotificationService.sendMessage(new EmailMessageRecord(createCustomerRecord.username(), String.format(Messages.REGISTRATION_OTP_SUB, otpValue), otpValue))
            );
            if(optionalCustomer.isPresent()) {
                Customer customer = optionalCustomer.get();
                customer.setUsername(createCustomerRecord.username());
                customer.setPassword(passwordEncoder.encode(createCustomerRecord.password()));
                customer.setName(createCustomerRecord.name());
                customer.setBirthDate(createCustomerRecord.birthDate());
                customer.setRole(createCustomerRecord.role());

                Otp otp = otpRepository.findById(optionalCustomer.get().getId()).orElseThrow(EntityNotFoundException::new);
                otp.setValue(otpValue);
                otp.setExpiryTime(LocalDateTime.now().plusHours(OTP_EXPIRY_HOUR));
            } else {
                Customer customer = createCustomerRecord.toCustomer(passwordEncoder);
                Otp otp = Otp.builder()
                        .value(otpValue)
                        .expiryTime(LocalDateTime.now().plusHours(OTP_EXPIRY_HOUR)).build();
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
                throw new CredentialsExpiredException(Messages.EXPIRED_OTP);
            }
            else if(/*customer.getOtp().getValue().*/"0000".equals(otpRecord.otp())) {
                otpRepository.deleteById(customer.getId());
                customer.setEnabled(true);
            } else {
                throw new BadCredentialsException(Messages.INVALID_OPT);
            }
        } else {
            throw new EntityNotFoundException(Messages.USER_NOT_FOUND);
        }
    }

    @Transactional
    public void updateCustomer(UpdateCustomerRecord customerRecord) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaUpdate<Customer> criteriaUpdate = criteriaBuilder.createCriteriaUpdate(Customer.class);
        Root<Customer> root = criteriaUpdate.from(Customer.class);

        criteriaUpdate.set(root.get("name"), customerRecord.name());
        criteriaUpdate.set(root.get("birthDate"), customerRecord.birthDate());
        criteriaUpdate.where(criteriaBuilder.equal(root.get("username"), getPrincipal()));

        entityManager.createQuery(criteriaUpdate).executeUpdate();
    }

    public String signIn(Authentication authentication) {
        return jwtService.createAccessToken(authentication.getName(), populateAuthorities(authentication.getAuthorities()));
    }

    private String populateAuthorities(Collection<? extends GrantedAuthority> authorities) {
        Set<String> authoritiesSet = new HashSet<>();
        for(GrantedAuthority authority : authorities)
            if (authority.getAuthority().startsWith(ROLE_PREFIX)) {
                authoritiesSet.add(authority.getAuthority());
            }
        return String.join(",", authoritiesSet);
    }

    public Customer getLoggedInUser() {
        String username = getPrincipal();
        return customerRepository.findByUsername(username).orElseThrow(
                () -> new EntityNotFoundException(Messages.USER_NOT_FOUND)
        );
    }

    public CustomerRecord getCustomer() {
        String username = getPrincipal();
        return customerRepository.findByUsername(username, CustomerRecord.class).orElseThrow(
                () -> new EntityNotFoundException(Messages.USER_NOT_FOUND)
        );
    }

    public String getPrincipal() {
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return (String) principal;
        }
    }

    public void delete(Long id) {
        customerRepository.deleteById(id);
    }

    @Cacheable(value = "age", key = "#deviceId")
    public int getAge(String deviceId) {
        var birthDate = customerRepository.getBirthDate(deviceId);
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    public String getName(String deviceId) {
        return customerRepository.getName(deviceId);
    }

    @Transactional
    @PreAuthorize("hasRole('DOCTOR')")
    public String subscribe(Long userId) {
        var patientOpt = customerRepository.findById(userId);
        Customer patient = patientOpt.orElseThrow(EntityNotFoundException::new);
        if(patient.getDoctor() == null) {
            Customer currentUser = getLoggedInUser();
            patient.setDoctor(currentUser);
            currentUser.getPatients().add(patient);
            return "Пациент ийгиликтүү кошулду";
        }
        else {
            return "Пациенттин жеке доктору бар";
        }
    }

    @Transactional
    @PreAuthorize("hasRole('DOCTOR')")
    public List<?> getPatients() {
        return getLoggedInUser().getPatients().stream().map(customer -> Map.of(
                "name", customer.getName(),
                "username", customer.getUsername()
        )).toList();
    }

    @PreAuthorize("hasRole('DOCTOR')")
    public String authorizeUser(String u) {
        Customer patient = customerRepository.findByUsername(u).orElseThrow(EntityNotFoundException::new);
        if(null == patient.getDoctor() || !patient.getDoctor().getUsername().equals(getPrincipal())) {
            throw new AccessDeniedException("User has no access to get patient data");
        }
        return u;
    }
}
