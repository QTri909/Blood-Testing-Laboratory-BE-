//package sum25.group03.warehouseservice.repository;
//
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.stereotype.Repository;
//
//@Repository
//public interface GlobalConfigRepo extends JpaRepository<GlobalConfiguration, Long> {
//    @Query("""
//        SELECT c
//        FROM GlobalConfiguration c
//        WHERE c.active = true
//    """)
//    Page<GlobalConfiguration> findAllByActiveTrue(Pageable pageable);
//}
