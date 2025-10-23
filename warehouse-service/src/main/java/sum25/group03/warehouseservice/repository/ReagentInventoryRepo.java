package sum25.group03.warehouseservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sum25.group03.warehouseservice.entity.ReagentInventory;
import sum25.group03.warehouseservice.entity.Reagents;

import java.util.Optional;

@Repository
public interface ReagentInventoryRepo extends JpaRepository<ReagentInventory, Long> {
    abstract Optional<ReagentInventory> findByLotNumber(String lotNumber);

}
