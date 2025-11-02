package sum25.group03.warehouseservice.service.instumentview;

import sum25.group03.warehouseservice.dto.response.InstrumentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sum25.group03.warehouseservice.dto.response.InstrumentStatusResponse;
import sum25.group03.warehouseservice.dto.response.InternalInstrumentStatusResponse;

public interface InstrumentViewService {
//    Page<InstrumentResponse> getAllInstruments(Pageable pageable);
//    Page<InstrumentResponse> searchInstruments(String name, String model, String status, Pageable pageable);
    InternalInstrumentStatusResponse checkInstrumentStatus(Long id);
}
