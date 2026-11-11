package de.phil.fitness.backend.user.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "USER_SETTINGS")
@Getter
@Setter
public class UserSettings {
    @Id
    @Column(name = "USER_ID")
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "WEIGHT_UNIT", nullable = false, length = 8)
    private WeightUnit weightUnit = WeightUnit.KG;

    @Enumerated(EnumType.STRING)
    @Column(name = "DISTANCE_UNIT", nullable = false, length = 8)
    private DistanceUnit distanceUnit = DistanceUnit.KM;

    @Column(name = "TIME_ZONE", nullable = false, length = 64)
    private String timeZone = "Europe/Berlin";

    protected UserSettings() {

    }

    public UserSettings(Long userId) {
        this.userId = userId;
    }
}
