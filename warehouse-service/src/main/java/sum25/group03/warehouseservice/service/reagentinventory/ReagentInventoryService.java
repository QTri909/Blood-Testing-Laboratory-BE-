package sum25.group03.warehouseservice.service.reagentinventory;

public interface ReagentInventoryService {
    void decreaseQuantity(Long reagentId, String lotNumber, double quantityUsed);
}
