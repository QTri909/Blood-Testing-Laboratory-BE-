package sum25.group03.warehouseservice.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sum25.group03.warehouseservice.entity.GlobalParameterConfiguration;
import sum25.group03.warehouseservice.entity.enums.TestType;

import java.util.List;

@Repository
public interface GlobalTestParameterRepo extends JpaRepository<GlobalParameterConfiguration,Long> {

   @EntityGraph(attributePaths = {"globalTests" })
    @Query("""
        SELECT gpc
        FROM GlobalParameterConfiguration gpc 
        WHERE gpc.testType = :testType AND gpc.active = true
    """)
    List<GlobalParameterConfiguration> findByTestType(TestType testType);


}
