package sum25.group03.warehouseservice.service.instumentview;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sum25.group03.warehouseservice.dto.response.InstrumentResponse;
import sum25.group03.warehouseservice.dto.response.InstrumentStatusResponse;
import sum25.group03.warehouseservice.entity.Instrument;
import sum25.group03.warehouseservice.entity.enums.InstrumentStatus;
import sum25.group03.warehouseservice.exception.InvalidArgumentException;
import sum25.group03.warehouseservice.exception.NotFoundException;
import sum25.group03.warehouseservice.mapper.InstrumentMapper;
import sum25.group03.warehouseservice.repository.InstrumentRepo;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "InstrumentService")
@Transactional(readOnly = true)
public class InstrumentViewServiceImpl implements InstrumentViewService {

    private final InstrumentRepo instrumentRepo;
    private final InstrumentMapper instrumentMapper;

    @Override
    public Page<InstrumentResponse> getAllInstruments(Pageable pageable) {
        return instrumentRepo.findAll(pageable)
                .map(instrumentMapper::toResponse);
    }

    @Override
    public Page<InstrumentResponse> searchInstruments(String name, String model, String status, Pageable pageable) {
        log.info("Searching instruments: name='{}', model='{}', status='{}'", name, model, status);

        InstrumentStatus enumStatus = null;
        if(status != null && !status.isBlank()) {
            try {
                enumStatus = convertToStatus(status);
            } catch (InvalidArgumentException e) {
                log.warn("Invalid status '{}' provided", status);
                throw e;
            }
        }

        try {
            return instrumentRepo.searchInstruments(name, model, enumStatus, pageable)
                    .map(instrumentMapper::toResponse);
        } catch (Exception e) {
            log.error("Unexpected error during instrument search: {}", e.getMessage(), e);
            throw new InvalidArgumentException("An unexpected error occurred while searching instruments");
        }
    }

    @Override
    public InstrumentStatusResponse checkInstrumentStatus(Long id) {
        Instrument instrument = instrumentRepo.findById(id).orElseThrow(() -> new NotFoundException("Instrument not found with id: " + id));

        String message = switch (instrument.getStatus()) {
            case ACTIVE -> "Instrument is Active";
            case INACTIVE -> "Instrument is Inactive";
            case DELETED -> "Instrument has been removed from the system.";
        };
        return InstrumentStatusResponse.builder()
                .instrumentName(instrument.getInstrumentName())
                .instrumentModel(instrument.getModel())
                .currentStatus(instrument.getStatus().name())
                .message(message)
                .checkedAt(LocalDate.now())
                .build();
    }

    private InstrumentStatus convertToStatus(String status) {
        try {
            return InstrumentStatus.valueOf(status.trim().toUpperCase());
        } catch (Exception e) {
            log.warn("Invalid status value : {}", status);
            throw new InvalidArgumentException("Invalid status value: " + status);
        }
    }

}
