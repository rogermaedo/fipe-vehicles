package org.acme.vehicle.infrastructure.persistence;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "brand")
public class BrandEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "fipe_code", nullable = false, unique = true, length = 32)
    public String fipeCode;

    @Column(name = "name", nullable = false, length = 255)
    public String name;

    @Column(name = "created_at", nullable = false)
    public Instant createdAt;

    public static Optional<BrandEntity> findByFipeCode(String fipeCode) {
        return find("fipeCode", fipeCode).firstResultOptional();
    }

    public static List<BrandEntity> listAllOrderedByName() {
        return list("ORDER BY name");
    }
}
