package io.github.haidarim.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

@Builder
@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(
        name = "REVOKED_TOKEN",
        uniqueConstraints = {
                @UniqueConstraint(name = "_jti_unique_", columnNames = {"JTI"})
        }
)
public class RevokedToken implements Serializable {
    @Id
    @GeneratedValue
    @Column(name = "ID")
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "JTI", nullable = false)
    private String jti;

    @Column(name = "EXPIRES_AT", nullable = false)
    private Date expiresAt;
}
