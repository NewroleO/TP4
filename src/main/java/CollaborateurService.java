import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import java.util.List;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CollaborateurService {

    private static final Logger logger = LoggerFactory.getLogger(CollaborateurService.class);

    private final EntityManagerFactory fabrique;

    public CollaborateurService(EntityManagerFactory fabrique) {
        this.fabrique = fabrique;
    }

    public void initialiserDonneesDemoSiVide() {
        try (EntityManager em = fabrique.createEntityManager()) {
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();
            try {
                Long nombre = em.createQuery(
                        "select count(c) from Collaborateur c", Long.class
                ).getSingleResult();
                if (nombre == 0) {
                    for (Collaborateur collaborateur : DonneesDemo.creerCollaborateurs()) {
                        em.persist(collaborateur);
                    }
                    logger.info("Données de démonstration ajoutées à la base");
                }
                transaction.commit();
            } catch (RuntimeException e) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }
                logger.error("Échec de l'initialisation des données de démonstration", e);
                throw e;
            }
        }
    }

    public void ajouter(Collaborateur collaborateur) {
        try (EntityManager em = fabrique.createEntityManager()) {
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();
            try {
                if (em.find(Collaborateur.class, collaborateur.getIdentifiant()) != null) {
                    logger.warn("Ajout refusé : l'identifiant {} existe déjà",
                            collaborateur.getIdentifiant());
                    throw new CollaborateurDejaExistantException(
                            collaborateur.getIdentifiant()
                    );
                }

                em.persist(collaborateur);
                transaction.commit();
                logger.info("Collaborateur {} ajouté", collaborateur.getIdentifiant());
            } catch (RuntimeException e) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }
                if (!(e instanceof CollaborateurDejaExistantException)) {
                    logger.error("Échec de l'ajout du collaborateur {}",
                            collaborateur.getIdentifiant(), e);
                }
                throw e;
            }
        }
    }

    public Collaborateur rechercherParIdentifiant(String identifiant) {
        try (EntityManager em = fabrique.createEntityManager()) {
            return em.find(Collaborateur.class, identifiant);
        }
    }

    public List<Collaborateur> tous() {
        try (EntityManager em = fabrique.createEntityManager()) {
            return em.createQuery(
                    "select c from Collaborateur c order by c.identifiant",
                    Collaborateur.class
            ).getResultList();
        }
    }

    public List<Programmeur> programmeurs() {
        try (EntityManager em = fabrique.createEntityManager()) {
            return em.createQuery(
                    "select p from Programmeur p order by p.identifiant",
                    Programmeur.class
            ).getResultList();
        }
    }

    public List<Collaborateur> nomContenant(String fragment) {
        try (EntityManager em = fabrique.createEntityManager()) {
            return em.createQuery(
                    "select c from Collaborateur c "
                            + "where lower(c.nom) like :motif order by c.nom, c.prenom",
                    Collaborateur.class
            ).setParameter("motif", "%" + fragment.toLowerCase(Locale.ROOT) + "%")
                    .getResultList();
        }
    }

    public List<Collaborateur> salaireSuperieurA(double seuil) {
        try (EntityManager em = fabrique.createEntityManager()) {
            return em.createQuery(
                    "select c from Collaborateur c "
                            + "where c.salaire > :seuil order by c.salaire",
                    Collaborateur.class
            ).setParameter("seuil", seuil).getResultList();
        }
    }

    public List<Collaborateur> triesParNom() {
        try (EntityManager em = fabrique.createEntityManager()) {
            return em.createQuery(
                    "select c from Collaborateur c order by c.nom",
                    Collaborateur.class
            ).getResultList();
        }
    }

    public List<Collaborateur> triesParSalaire() {
        try (EntityManager em = fabrique.createEntityManager()) {
            return em.createQuery(
                    "select c from Collaborateur c order by c.salaire",
                    Collaborateur.class
            ).getResultList();
        }
    }

    public List<Collaborateur> triesParNomPuisPrenom() {
        try (EntityManager em = fabrique.createEntityManager()) {
            return em.createQuery(
                    "select c from Collaborateur c order by c.nom, c.prenom",
                    Collaborateur.class
            ).getResultList();
        }
    }

    public Collaborateur augmenterSalaire(String identifiant, double pourcentage) {
        try (EntityManager em = fabrique.createEntityManager()) {
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();
            try {
                Collaborateur collaborateur = em.find(Collaborateur.class, identifiant);
                if (collaborateur == null) {
                    transaction.commit();
                    return null;
                }

                collaborateur.augmenterSalaire(pourcentage);
                transaction.commit();
                logger.info("Salaire du collaborateur {} modifié", identifiant);
                return collaborateur;
            } catch (RuntimeException e) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }
                logger.error("Échec de la modification du salaire de {}", identifiant, e);
                throw e;
            }
        }
    }
}
