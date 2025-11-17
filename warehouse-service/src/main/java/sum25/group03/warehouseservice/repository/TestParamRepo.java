package sum25.group03.warehouseservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sum25.group03.warehouseservice.entity.TestParameter;

import java.util.List;

@Repository
public interface TestParamRepo extends JpaRepository<TestParameter,Long> {
    @Query("""
    SELECT DISTINCT tp
    FROM TestParameter tp
    LEFT JOIN FETCH tp.normalRanges nr
    WHERE tp.id IN :ids
""")
    List<TestParameter> findAllByIdsWithNormalRanges(@Param("ids") List<Long> testParameterIds);

}
