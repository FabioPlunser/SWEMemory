package at.ac.uibk.swa.Models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "Cards")
public class Card implements Serializable {

    @Id
    @Column(name = "CardId", nullable = false)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID cardId;

    @Setter
    @Column(name = "FrontText", nullable = false)
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    private String frontText;

    @Setter
    @Column(name = "BackText", nullable = false)
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    private String backText;

    @Setter
    @Column(name = "IsFlipped", nullable = false)
    @JdbcTypeCode(SqlTypes.BOOLEAN)
    private boolean isFlipped;

    // TODO: Create Deck Reference

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
