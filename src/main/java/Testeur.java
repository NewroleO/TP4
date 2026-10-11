/**
 * Testeur : collaborateur spécialisé dans les tests logiciels.
 *
 * Reprise du TP2 avec ajout de l'identifiant (délégué à Collaborateur).
 */
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("Testeur")
public class Testeur extends Collaborateur {

    protected Testeur() {
    }

    public Testeur(
            String identifiant,
            String prenom,
            String nom,
            double salaire,
            Adresse adresse) {
        super(identifiant, prenom, nom, salaire, adresse);
    }

    @Override
    public String getMetier() {
        return "Testeur";
    }

    @Override
    public void travailler() {
        System.out.println(getPrenom() + " exécute une campagne de tests.");
    }
}
