package sum25.group03.testorderservice.entities.mongodb;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
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
    private ObjectId id;

    private Long testOrderId;
    private List<TemplateParameter> templateParameters;
    private String status; // e.g., "PENDING", "COMPLETED", "DELETED"

    @CreatedDate
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
