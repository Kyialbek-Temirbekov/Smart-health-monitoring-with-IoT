package kg.edu.manas.cloud.service;

import kg.edu.manas.cloud.data.record.CreateDeviceRecord;
import kg.edu.manas.cloud.entity.Device;
import kg.edu.manas.cloud.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeviceService {
    private final DeviceRepository deviceRepository;
    private final CustomerService customerService;

    public void save(CreateDeviceRecord createDeviceRecord) {
        Device device = Device.builder()
                .id(createDeviceRecord.id())
                .customer(customerService.getLoggedInUser()).build();
        deviceRepository.save(device);
    }
}
