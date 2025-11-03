package sum25.group03.patientservice.entities.elasticsearch;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "user_snapshots")
public class ESUserSnapshot {

    @Id
    private Long id;
    private String fullName;

    @Field(type = FieldType.Keyword) // to ensure exact match searches
    private String email;

    @Field(type = FieldType.Keyword)
    private String phoneNumber;
    private Long externalUserId;

    @Field(type = FieldType.Keyword)
    private List<String> roles;

    @Field(type = FieldType.Date, format= DateFormat.date_optional_time)
    private String lastUpdated;

    // nested documents:
    @Field(type = FieldType.Nested)
    private List<ESMedicalRecord> patientRecords;

    @Field(type = FieldType.Nested)
    private List<ESMedicalRecord> assignedRecords;

    @Field(type = FieldType.Nested)
    private List<ESMedicalRecord> createdRecords;

    @Field(type = FieldType.Nested)
    private List<ESMedicalRecord> updatedRecords;

    @Field(type = FieldType.Nested)
    private List<ESClinicalNote> authoredNotes;
}
