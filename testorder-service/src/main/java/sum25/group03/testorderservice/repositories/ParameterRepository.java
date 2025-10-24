package sum25.group03.testorderservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sum25.group03.testorderservice.entities.Parameter;
import sum25.group03.testorderservice.enums.ParameterStatus;
import jakarta.validation.constraints.NotBlank;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParameterRepository extends JpaRepository<Parameter,Long> {
    List<Parameter> findByStatus(ParameterStatus status);
    Optional<Parameter> findByParamCodeAndStatus(String paramCode, ParameterStatus status);
    boolean existsByParamCode(String paramCode);
    Parameter findByParamCode(@NotBlank(message = "Config key cannot be blank") String configKey);
    Parameter findByAbbreviation(String abbreviation);
}
