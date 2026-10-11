
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class TestConnexion {
    public static void main(String[] args) {
        try (EntityManagerFactory emf =
                     Persistence.createEntityManagerFactory("collaborateurs-pu")) {

            System.out.println("Connexion Hibernate réussie.");

            try (EntityManager em = emf.createEntityManager()) {
                var collaborateurs = em.createQuery(
                        "SELECT c FROM Collaborateur c",
                        Collaborateur.class
                ).getResultList();

                System.out.println("Nombre de collaborateurs : " + collaborateurs.size());

                for (Collaborateur collaborateur : collaborateurs) {
                    System.out.println(collaborateur);
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur Hibernate : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
