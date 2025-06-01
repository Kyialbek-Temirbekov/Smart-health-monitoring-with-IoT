package kg.edu.manas.cloud.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.edu.manas.cloud.model.data.record.CreateCustomerRecord;
import kg.edu.manas.cloud.model.data.record.CustomerRecord;
import kg.edu.manas.cloud.model.data.record.OtpRecord;
import kg.edu.manas.cloud.model.data.record.UpdateCustomerRecord;
import kg.edu.manas.cloud.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Customer Service")
@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @Operation(summary = "create customer")
    @PostMapping("/signUp")
    public ResponseEntity<String> createCustomer(@RequestBody CreateCustomerRecord createCustomerRecord) {
        customerService.createCustomer(createCustomerRecord);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @Operation(summary = "confirm email")
    @PatchMapping("/confirmEmail")
    public ResponseEntity<String> confirmEmail(@RequestBody OtpRecord otpRecord) {
        customerService.confirmEmail(otpRecord);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @Operation(summary = "update customer (JWT)")
    @PatchMapping()
    public ResponseEntity<String> updateCustomer(@RequestBody UpdateCustomerRecord customerRecord) {
        customerService.updateCustomer(customerRecord);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @Operation(summary = "sign in (Basic Authentication)")
    @PostMapping("/signIn")
    public ResponseEntity<String> singIn(Authentication authentication) {
        return ResponseEntity.status(HttpStatus.OK).body(customerService.signIn(authentication));
    }
    @Operation(summary = "retrieve user data (JWT)")
    @GetMapping
    public ResponseEntity<CustomerRecord> getCustomer() {
        return new ResponseEntity<>(customerService.getCustomer(), HttpStatus.OK);
    }
    @Operation(summary = "delete user")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        customerService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @Operation(summary = "subscribe to patient (JWT)")
    @PatchMapping("/subscribe")
    public ResponseEntity<String> subscribe(@RequestParam Long userId) {
        return new ResponseEntity<>(customerService.subscribe(userId), HttpStatus.OK);
    }
    @Operation(summary = "get patients (JWT)")
    @GetMapping("/patients")
    public ResponseEntity<?> getPatients() {
        return new ResponseEntity<>(customerService.getPatients(), HttpStatus.OK);
    }
    @Operation(summary = "get doctor (JWT)")
    @GetMapping("/doctor")
    public ResponseEntity<?> getDoctor() {
        return new ResponseEntity<>(customerService.getDoctor(), HttpStatus.OK);
    }
}
