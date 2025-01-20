package kg.edu.manas.cloud.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.edu.manas.cloud.data.record.CreateCustomerRecord;
import kg.edu.manas.cloud.data.record.OtpRecord;
import kg.edu.manas.cloud.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Customer Service")
@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @Operation(summary = "create customer")
    @PostMapping()
    public ResponseEntity<String> createCustomer(@RequestBody CreateCustomerRecord createCustomerRecord) {
        customerService.createCustomer(createCustomerRecord);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    @Operation(summary = "confirm email")
    @PatchMapping("/confirm-email")
    public ResponseEntity<String> confirmEmail(@RequestBody OtpRecord otpRecord) {
        customerService.confirmEmail(otpRecord);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @Operation(summary = "logged in user data")
    @Parameter(name = "Authorization", in = ParameterIn.HEADER, example = "Basic <encoded_credentials>")
    @GetMapping
    public ResponseEntity<CustomerDto> getCustomer() {
        return new ResponseEntity<>(customerService.getCustomer(), HttpStatus.OK);
    }
    @Operation(summary = "delete user")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        customerService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
