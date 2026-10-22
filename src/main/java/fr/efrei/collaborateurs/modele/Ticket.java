package fr.efrei.collaborateurs.modele;

import jakarta.persistence.*; // javax.persistence si Hibernate < 6



@Entity
@Table(name = "ticket")
public class Ticket {

    public enum Priorite {BASSE, NORMALE, HAUTE}

    public enum Statut {A_FAIRE, EN_COURS, A_VALIDER, TERMINE}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titre;
    @Enumerated(EnumType.STRING)
    private Priorite priorite;
    @Enumerated(EnumType.STRING)
    private Statut statut;
    @ManyToOne
    @JoinColumn(name = "assigne_id")
    private Collaborateur assigne;

    protected Ticket() {
        // exigé par JPA, comme pour vos autres entités
    }
// Constructeur public, accesseurs et autres éléments utiles à compléter.
}