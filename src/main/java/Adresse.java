/**
 * Représente l'adresse postale d'un collaborateur.
 * Reprise de TP2 avec ajout de toString() pour les affichages en ligne (Annuaire).
 */
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "adresse")
public class Adresse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rue", length = 100, nullable = false)
    private String rue;

    @Column(name = "code_postal", length = 20, nullable = false)
    private String codePostal;

    @Column(name = "ville", length = 80, nullable = false)
    private String ville;

    @Column(name = "pays", length = 80, nullable = false)
    private String pays;

    protected Adresse() {
    }

    public Adresse(String rue, String codePostal, String ville, String pays) {
        this.rue = rue;
        this.codePostal = codePostal;
        this.ville = ville;
        this.pays = pays;
    }

    public String getRue()        { return rue; }
    public String getCodePostal() { return codePostal; }
    public String getVille()      { return ville; }
    public String getPays()       { return pays; }
    public Long getId()           { return id; }

    /** Affichage multiligne (hérité du TP2 — utilisé dans afficherFiche()). */
    public void afficher() {
        System.out.println(rue);
        System.out.println(codePostal + " " + ville);
        System.out.println(pays);
    }

    /** Représentation compacte sur une ligne (utilisée dans toString() de Collaborateur). */
    @Override
    public String toString() {
        return rue + ", " + codePostal + " " + ville + " (" + pays + ")";
    }
}
