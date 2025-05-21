package kg.edu.manas.cloud.service;

import kg.edu.manas.cloud.model.data.record.CreateDeviceRecord;
import kg.edu.manas.cloud.model.entity.Device;
import kg.edu.manas.cloud.model.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeviceService {
    private final DeviceRepository deviceRepository;
    private final CustomerService customerService;
    private final EncryptionService encryptionService;

    public void save(CreateDeviceRecord createDeviceRecord) {
        Device device = Device.builder()
                .id(encryptionService.encrypt(createDeviceRecord.id()))
                .brand(createDeviceRecord.brand())
                .model(createDeviceRecord.model())
                .releaseDate(createDeviceRecord.releaseDate())
                .batteryLife(createDeviceRecord.batteryLife())
                .customer(customerService.getLoggedInUser()).build();
        deviceRepository.save(device);
    }
    public CreateDeviceRecord getDevice() {
        return deviceRepository.findByCustomerId(customerService.getLoggedInUser().getId(), CreateDeviceRecord.class)
                .orElseThrow();
    }
}
