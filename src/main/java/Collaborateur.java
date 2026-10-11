
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.CascadeType;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
@Table(name = "collaborateur")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "metier")
public abstract class Collaborateur {

    private static final Logger logger = LoggerFactory.getLogger(Collaborateur.class);

    @Id
    @Column(name = "id_collaborateur", length = 10, nullable = false)
    private String identifiant;

    @Column(name = "prenom", length = 50, nullable = false)
    private String prenom;

    @Column(name = "nom", length = 50, nullable = false)
    private String nom;

    @Column(name = "salaire", nullable = false)
    private double salaire;

    @ManyToOne(optional = false, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "adresse_id", nullable = false)
    private Adresse adresse;

    protected Collaborateur() {
    }

    protected Collaborateur(
            String identifiant,
            String prenom,
            String nom,
            double salaire,
            Adresse adresse) {
        if (!Double.isFinite(salaire) || salaire < 0) {
            throw new IllegalArgumentException("Le salaire doit être un nombre positif ou nul.");
        }
        this.identifiant = identifiant;
        this.prenom = prenom;
        this.nom = nom;
        this.salaire = salaire;
        this.adresse = adresse;
    }

    public String getIdentifiant() { return identifiant; }
    public String getPrenom() { return prenom; }
    public String getNom() { return nom; }
    public double getSalaire() { return salaire; }
    public Adresse getAdresse() { return adresse; }

    public void augmenterSalaire(double pourcentage) {
        if (pourcentage > 0) {
            double ancienSalaire = salaire;
            salaire = salaire * (1 + pourcentage / 100);
            logger.debug("Salaire de {} : {} -> {}", identifiant, ancienSalaire, salaire);
        }
    }

    public abstract String getMetier();

    public abstract void travailler();

    public void afficherFiche() {
        System.out.println("[" + identifiant + "] " + prenom + " " + nom);
        System.out.println("Métier  : " + getMetier());
        System.out.println("Salaire : " + salaire + " €");
        System.out.println("Adresse :");
        if (adresse != null) {
            adresse.afficher();
        }
    }

    @Override
    public boolean equals(Object autre) {
        if (this == autre) return true;
        if (!(autre instanceof Collaborateur collaborateur)) return false;
        return Objects.equals(identifiant, collaborateur.identifiant);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifiant);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s %s (%s) - %.2f €",
                identifiant, prenom, nom, getMetier(), salaire);
    }
}
