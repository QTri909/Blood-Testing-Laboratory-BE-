package sum25.group03.testorderservice.repositories;

import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sum25.group03.testorderservice.entity.Parameter;

@Repository
public interface ParameterRepository extends JpaRepository<Parameter, Long> {
    Parameter findByParamCode(@NotBlank(message = "Config key cannot be blank") String configKey);

    Parameter findByAbbreviation(String abbreviation);
}
