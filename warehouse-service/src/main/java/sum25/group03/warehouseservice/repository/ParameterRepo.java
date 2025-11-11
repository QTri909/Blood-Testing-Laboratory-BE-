package sum25.group03.warehouseservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sum25.group03.warehouseservice.dto.response.ParameterRes;
import sum25.group03.warehouseservice.entity.TestParameter;

import java.util.List;

@Repository
public interface ParameterRepo extends JpaRepository<TestParameter, Long> {
    @Query("""
        SELECT new sum25.group03.warehouseservice.dto.response.ParameterRes(
            p.id,
            p.parameterName,
            p.abbreviation,
            p.description,
            p.normalRange,
            CASE WHEN g.active = true THEN 'ACTIVE' ELSE 'INACTIVE' END
        )
        FROM TestParameter p
        JOIN p.globalParameterConfiguration g
    """)
    List<ParameterRes> findAllParameters();
}
