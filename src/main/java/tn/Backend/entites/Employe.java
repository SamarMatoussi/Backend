package tn.Backend.entites;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@Entity
@Table
@SuperBuilder
@DiscriminatorValue("Employe")
@NoArgsConstructor
public class Employe extends User {
   /* @ManyToOne
    @JoinColumn(name = "poste_id", nullable = false)
    private Poste poste;
   */

    @OneToMany(mappedBy = "employe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Note> notes;

}
