package sum25.group03.warehouseservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "manufacturers")
public class Manufacturer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "manufacturer_id")
    private Long ManufacturerId;

    @Column(name = "manufacturer_name", nullable = false)
    private String manufacturerName;

    @Column(name = "country", nullable = false)
    private String country;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "email", nullable = false)
    private String email;

    @OneToMany(mappedBy = "manufacturer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Instrument> instruments;
}
