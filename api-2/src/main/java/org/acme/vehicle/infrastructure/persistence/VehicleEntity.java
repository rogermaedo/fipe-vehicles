package org.acme.vehicle.infrastructure.persistence;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "vehicle")
public class VehicleEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "brand_id", nullable = false)
    public BrandEntity brand;

    @Column(name = "fipe_model_code", nullable = false)
    public Integer fipeModelCode;

    @Column(name = "model_name", nullable = false, length = 500)
    public String modelName;

    @Column(name = "notes")
    public String notes;

    @Column(name = "created_at", nullable = false)
    public Instant createdAt;

    public static boolean existsByBrandAndFipeModel(Long brandId, int fipeModelCode) {
        return count("brand.id = ?1 AND fipeModelCode = ?2", brandId, fipeModelCode) > 0;
    }
}
