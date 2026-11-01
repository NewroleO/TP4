package fr.efrei.collaborateurs.modele;

import jakarta.persistence.*;

@Entity
@Table(name = "ticket")
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
    }

    public Ticket(String titre, Priorite priorite) {
        if (titre == null || titre.isBlank()) {
            throw new IllegalArgumentException("Le titre est obligatoire");
        }
        this.titre = titre.trim();
        this.priorite = priorite;
        this.statut = Statut.A_FAIRE;
    }

    public Long getId() { return id; }
    public String getTitre() { return titre; }
    public Priorite getPriorite() { return priorite; }
    public Statut getStatut() { return statut; }
    public Collaborateur getAssigne() { return assigne; }

    public void setTitre(String titre) {
        if (titre == null || titre.isBlank()) {
            throw new IllegalArgumentException("Le titre est obligatoire");
        }
        this.titre = titre.trim();
    }

    public void setPriorite(Priorite priorite) {
        this.priorite = priorite;
    }

    public void setStatut(Statut statut) {
        this.statut = statut;
    }

    public void setAssigne(Collaborateur assigne) {
        this.assigne = assigne;
    }

    @Override
    public String toString() {
        String resp = (assigne != null) ? assigne.getPrenom() + " " + assigne.getNom() : "non assigné";
        return String.format("#%d %s %s [%s] (%s)", id, priorite, titre, statut, resp);
    }
}
