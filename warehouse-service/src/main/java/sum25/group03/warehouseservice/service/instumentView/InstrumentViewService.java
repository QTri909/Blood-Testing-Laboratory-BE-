package sum25.group03.warehouseservice.service.instumentView;

import sum25.group03.warehouseservice.dto.response.InstrumentPageResponse;
import sum25.group03.warehouseservice.dto.response.InstrumentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sum25.group03.warehouseservice.dto.response.InstrumentStatusResponse;
import sum25.group03.warehouseservice.entity.Instrument;

public interface InstrumentViewService {
    Page<InstrumentResponse> getAllInstruments(Pageable pageable);
    Page<InstrumentResponse> searchInstruments(String name, String code, String status, Pageable pageable);
    InstrumentStatusResponse getInstrumentStatus(Long id);
}
