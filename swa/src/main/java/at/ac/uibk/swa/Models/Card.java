package at.ac.uibk.swa.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "card")
public class Card implements Serializable {

    public Card(String frontText, String backText, boolean isFlipped, Deck deck) {
        this(UUID.randomUUID(), frontText, backText, isFlipped, deck, new HashMap<>());
    }

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name = "card_id", nullable = false)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID cardId;

    @Setter
    @Column(name = "front_text", nullable = false)
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    private String frontText;

    @Setter
    @Column(name = "back_text", nullable = false)
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    private String backText;

    @Setter
    @Column(name = "is_flipped", nullable = false)
    @JdbcTypeCode(SqlTypes.BOOLEAN)
    private boolean isFlipped;

    @JsonIgnore
    @JoinColumn(name = "deck_id", nullable = false)
    @ManyToOne(optional = false, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Deck deck;

    @JsonIgnore
    @OneToMany
    @JoinTable(name = "card_progress_mapping",
            joinColumns = {@JoinColumn(name = "card_id", referencedColumnName = "card_id")},
            inverseJoinColumns = {@JoinColumn(name = "progress_id", referencedColumnName = "learning_progress_id")})
    @MapKeyJoinColumn(name = "person_id")
    private Map<Person, LearningProgress> learningProgresses = new HashMap<>();

    @Override
    public String toString() {
        String f = !isFlipped ? this.frontText : this.backText;
        String b =  isFlipped ? this.frontText : this.backText;
        return String.format("Card: %s - %s", f, b);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Card u)
            return u.cardId == this.cardId;

        return false;
    }

    @Override
    public int hashCode() {
        return this.cardId.hashCode();
    }
}
