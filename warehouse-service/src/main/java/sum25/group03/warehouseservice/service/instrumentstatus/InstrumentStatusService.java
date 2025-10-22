package sum25.group03.warehouseservice.service.instrumentstatus;

public interface InstrumentStatusService {
    void activateInstrument(Long id, String username);
    void deactivateInstrument(Long id, String username);
    void deleteInstrument(Long id, String username);
}
