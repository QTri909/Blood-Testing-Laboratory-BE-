package sum25.group03.warehouseservice.dto.response;

import sum25.group03.warehouseservice.entity.enums.InstrumentStatus;

public record InstrumentStatusResponse(Long id, String name, InstrumentStatus status) {}
