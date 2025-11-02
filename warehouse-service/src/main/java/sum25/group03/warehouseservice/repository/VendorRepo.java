package sum25.group03.warehouseservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sum25.group03.warehouseservice.entity.Vendors;

@Repository
public interface VendorRepo extends JpaRepository<Vendors, Long> {
}
