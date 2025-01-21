package kg.edu.manas.cloud.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.edu.manas.cloud.data.record.CreateCustomerRecord;
import kg.edu.manas.cloud.data.record.CreateDeviceRecord;
import kg.edu.manas.cloud.service.DeviceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Device Service")
@RestController
@RequestMapping("/device")
@RequiredArgsConstructor
public class DeviceController {
    private final DeviceService deviceService;

    @Operation(summary = "add device")
    @PostMapping()
    public ResponseEntity<String> createCustomer(@RequestBody CreateDeviceRecord createDeviceRecord) {
        deviceService.save(createDeviceRecord);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
