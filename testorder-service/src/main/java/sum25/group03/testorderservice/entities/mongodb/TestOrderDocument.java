package sum25.group03.testorderservice.entities.mongodb;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document("template_kits")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TestOrderDocument {
    @Id
    private Long testOrderId;
    private List<TemplateParameter> templateParameters;
    private String status; // e.g., "PENDING", "COMPLETED", "DELETED"

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
