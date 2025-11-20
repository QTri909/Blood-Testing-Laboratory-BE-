package sum25.group03.testorderservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sum25.group03.testorderservice.entities.Parameter;
import sum25.group03.testorderservice.enums.ParameterStatus;
import jakarta.validation.constraints.NotBlank;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ParameterRepository extends JpaRepository<Parameter,Long> {
    List<Parameter> findByStatus(ParameterStatus status);
    Optional<Parameter> findByParamCodeAndStatus(String paramCode, ParameterStatus status);
    boolean existsByParamCode(String paramCode);
    Parameter findByParamCode(@NotBlank(message = "Config key cannot be blank") String configKey);
    Parameter findByAbbreviation(String abbreviation);

    List<Parameter> findByIdIn(List<Long> ids);

    @Query("""
       SELECT p.paramCode
       FROM TestResult tr
       JOIN tr.parameter p
       WHERE tr.testOrder.id = :testOrderId
       AND p.id = (
           SELECT MIN(p2.id)
           FROM Parameter p2
           JOIN TestResult tr2 ON tr2.parameter.id = p2.id
           WHERE tr2.testOrder.id = :testOrderId
           AND p2.externalId = p.externalId
       )
       """)
    Set<String> findParamCodesFirstPerExternalIdByTestOrder(@Param("testOrderId") Long testOrderId);

    @Query("""
       SELECT p
       FROM Parameter p
       WHERE p.id = (
           SELECT MIN(p2.id)
           FROM Parameter p2
           WHERE p2.externalId = p.externalId
       )
       AND p.externalId IN :externalIds
       """)
    List<Parameter> findFirstPerExternalIdByExternalIds(@Param("externalIds") Set<Long> externalIds);


}
