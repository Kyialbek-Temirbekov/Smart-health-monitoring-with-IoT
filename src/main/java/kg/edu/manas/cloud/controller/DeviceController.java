package kg.edu.manas.cloud.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.edu.manas.cloud.model.data.record.CreateDeviceRecord;
import kg.edu.manas.cloud.service.DeviceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Device Service")
@RestController
@RequestMapping("/device")
@RequiredArgsConstructor
public class DeviceController {
    private final DeviceService deviceService;

    @Operation(summary = "add device (JWT)")
    @PostMapping()
    public ResponseEntity<String> createCustomer(@RequestBody CreateDeviceRecord createDeviceRecord) {
        deviceService.save(createDeviceRecord);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    @Operation(summary = "retrieve device data")
    @GetMapping()
    public ResponseEntity<?> getDeviceData() {
        return new ResponseEntity<>(deviceService.getDevice(), HttpStatus.OK);
    }
}
