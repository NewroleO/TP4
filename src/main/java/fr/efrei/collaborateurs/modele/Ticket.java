package fr.efrei.collaborateurs.modele;

import jakarta.persistence.*;

public class Ticket {
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