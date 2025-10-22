package sum25.group03.testorderservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sum25.group03.testorderservice.entity.Parameter;
import sum25.group03.testorderservice.enums.ParameterStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParameterRepository extends JpaRepository<Parameter,Long> {
    List<Parameter> findByStatus(ParameterStatus status);

    Optional<Parameter> findByParamCodeAndStatus(String paramCode, ParameterStatus status);

    boolean existsByParamCode(String paramCode);
}
