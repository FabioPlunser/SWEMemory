package at.ac.uibk.swa.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "learning_progress")
public class LearningProgress implements Serializable {

    /**
     * Deep Copy Constructor for LearningProgress.
     *
     * @param lp The LearningProgress to copy.
     */
    public LearningProgress(LearningProgress lp) {
        // NOTE: Using the same reference to the UUID is safe, because UUID is immutable.
        this.learningProgressId = lp.learningProgressId;

        // These are primitive Types and therefore will be implicitly cloned.
        this.interval = lp.interval;
        this.eFactor = lp.eFactor;
        this.repetitions = lp.repetitions;

        // NOTE: Using the same reference to the LocalDateTime is safe, because LocalDateTime is immutable.
        this.nextLearn = lp.nextLearn;
    }

    @Id
    @Setter(AccessLevel.PACKAGE)
    @Column(name = "progress_id", nullable = false)
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    @GeneratedValue(strategy=GenerationType.AUTO)
    @JsonProperty(access = JsonProperty.Access.READ_WRITE)
    private UUID learningProgressId;

    @Builder.Default
    @Column(name = "learning_interval", nullable = false)
    @JdbcTypeCode(SqlTypes.INTEGER)
    @JsonProperty(access = JsonProperty.Access.READ_WRITE)
    private int interval = 0;

    @Builder.Default
    @Column(name = "e_factor", nullable = false)
    @JdbcTypeCode(SqlTypes.DOUBLE)
    @JsonProperty(access = JsonProperty.Access.READ_WRITE)
    private double eFactor = 2.5;

    @Setter(AccessLevel.PRIVATE)
    @Builder.Default
    @Column(name = "num_repetitions", nullable = false)
    @JdbcTypeCode(SqlTypes.BIGINT)
    @JsonProperty(access = JsonProperty.Access.READ_WRITE)
    private int repetitions = 0;

    @Builder.Default
    @JsonProperty(access = JsonProperty.Access.READ_WRITE)
    @Column(name = "next_learn", nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime nextLearn = LocalDateTime.now();

    public void incrementRepetitions() {
        this.repetitions++;
    }

    public void update(LearningProgress newLearningProgress) {
        this.interval = newLearningProgress.interval;
        this.eFactor = newLearningProgress.eFactor;
        this.repetitions = newLearningProgress.repetitions;
        this.nextLearn = newLearningProgress.nextLearn;
    }

    @Override
    public boolean equals(Object o) {
        return (this == o) || ((o instanceof LearningProgress a) &&
                (this.learningProgressId != null) &&
                (this.learningProgressId.equals(a.learningProgressId)));
    }

    @Override
    public int hashCode() {
        // NOTE: This will intentionally throw an Exception if learningProgressId is null.
        return this.learningProgressId.hashCode();
    }

    @Override
    public String toString() {
        // Auto-Generated
        return "LearningProgress{" +
                "learningProgressId=" + learningProgressId +
                ", interval=" + interval +
                ", eFactor=" + eFactor +
                ", repetitions=" + repetitions +
                '}';
    }
}
