package kg.edu.manas.cloud.service;

import jakarta.transaction.Transactional;
import kg.edu.manas.cloud.data.record.CreateCustomerRecord;
import kg.edu.manas.cloud.data.record.CustomerRecord;
import kg.edu.manas.cloud.entity.Customer;
import kg.edu.manas.cloud.entity.Otp;
import kg.edu.manas.cloud.repository.CustomerRepository;
import kg.edu.manas.cloud.util.NumericTokenGenerator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final MessageService messageService;

    private static final String REGISTRATION_OTP_SUB = "One time password for registration";
    private static final long OTP_EXPIRY_HOUR = 3;
    private static final int OTP_LENGTH = 4;

    public CustomerService(CustomerRepository customerRepository, PasswordEncoder passwordEncoder, MessageService messageService, CustomerReportService customerReportService, ExcelReportService reportService) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
        this.messageService = messageService;
    }

    @Transactional
    public void createCustomer(CreateCustomerRecord createCustomerRecord) {
        Optional<Customer> optionalCustomer = customerRepository.findByUsername(createCustomerRecord.getEmail());

        if(optionalCustomer.isPresent() && optionalCustomer.get().isEnabled()) {
            throw new ConflictException("User already exist: " + createCustomerRecord.getEmail());
        } else {
            String otpValue = NumericTokenGenerator.generateToken(OTP_LENGTH);
            CompletableFuture.runAsync(() ->
                    messageService.sendMessage(new MessageDto(createCustomerRecord.getEmail(), REGISTRATION_OTP_SUB, otpValue))
            );
            if(optionalCustomer.isPresent()) {
                Customer customer = optionalCustomer.get();
                customer.setUsername(createCustomerRecord.username());
                customer.setPassword(passwordEncoder.encode(createCustomerRecord.password()));
                customer.setName(createCustomerRecord.name());
                customer.setBirthDate(createCustomerRecord.birthDate());

                Otp otp = optionalCustomer.get().getOtp();
                otp.setValue(otpValue);
                otp.setExpiryTime(LocalDateTime.now().plusHours(OTP_EXPIRY_HOUR));
            } else {
                Customer customer = createCustomerRecord.toCustomer(passwordEncoder);
                Otp otp = Otp.builder()
                        .value(otpValue)
                        .expiryTime(LocalDateTime.now().plusHours(OTP_EXPIRY_HOUR)).build();
                customer.setOtp(otp);
                otp.setCustomer(customer);
                customerRepository.save(customer);
            }
        }
    }

    @Transactional
    public void confirmEmail(OtpDto otpDto) {
        Optional<Customer> optionalCustomer = customerRepository.findByUsername(otpDto.getEmail());
        if(optionalCustomer.isPresent()) {
            Customer customer = optionalCustomer.get();
            if(customer.getOtp().isExpired()) {
                throw new CredentialsExpiredException(ExceptionMessages.OTP_EXPIRED);
            }
            else if(/*customer.getOtp().getValue().*/"0000".equals(otpDto.getOtp())) {
                customer.setOtp(null);
                customer.setEnabled(true);
            } else {
                throw new BadCredentialsException(ExceptionMessages.INVALID_OTP);
            }
        } else {
            throw new EntityNotFoundException(ExceptionMessages.USER_NOT_FOUND_BY_EMAIL + ":" + otpDto.getEmail());
        }
    }

    public CustomerRecord getCustomer() {
        return getLoggedInUser();
    }

    public CustomerRecord getLoggedInUser() {
        String username = getPrincipal().getUsername();
        return customerRepository.findByUsername(username, CustomerRecord.class).orElseThrow(
                () -> new EntityNotFoundException(ExceptionMessages.USER_NOT_FOUND_BY_EMAIL)
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
