import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Point d'entrée de l'application — Annuaire des collaborateurs.
 *
 * Reprend la structure du HelloEfrei du TP1 :
 *   - méthode afficherMenu() statique
 *   - méthodes utilitaires statiques (afficherListe, saisirIdentifiant...)
 *   - boucle principale avec validation hasNextInt() + switch fléché
 */
public class HelloEfrei {

    private static final Logger logger = LoggerFactory.getLogger(HelloEfrei.class);

    public static void main(String[] args) {
        try (EntityManagerFactory fabrique =
                     Persistence.createEntityManagerFactory("collaborateurs-pu");
             Scanner scanner = new Scanner(System.in)) {
            CollaborateurService service = new CollaborateurService(fabrique);
            service.initialiserDonneesDemoSiVide();
            int choix = -1;

            while (choix != 0) {
                afficherMenu();

                if (scanner.hasNextInt()) {
                    choix = scanner.nextInt();
                    scanner.nextLine();

                    switch (choix) {
                        case 0 -> System.out.println("À bientôt !");

                        case 1 -> afficherListe(service.tous());

                        case 2 -> {
                            System.out.print("Fragment de nom : ");
                            afficherListe(service.nomContenant(scanner.nextLine()));
                        }

                        case 3 -> {
                            System.out.print("Salaire minimum (€) : ");
                            if (scanner.hasNextDouble()) {
                                double seuil = scanner.nextDouble();
                                scanner.nextLine();
                                if (Double.isFinite(seuil) && seuil >= 0) {
                                    afficherListe(service.salaireSuperieurA(seuil));
                                } else {
                                    System.out.println("Entrée non valide.");
                                }
                            } else {
                                System.out.println("Saisie invalide.");
                                scanner.nextLine();
                            }
                        }

                        case 4 -> afficherListe(service.programmeurs());

                        case 5 -> afficherListe(service.triesParNom());

                        case 6 -> afficherListe(service.triesParSalaire());

                        case 7 -> afficherListe(service.triesParNomPuisPrenom());

                        case 8 -> {
                            System.out.print("Identifiant du collaborateur : ");
                            String id = scanner.nextLine();
                            Collaborateur trouve = service.rechercherParIdentifiant(id);
                            if (trouve != null) {
                                System.out.println();
                                trouve.afficherFiche();
                            } else {
                                System.out.println("Collaborateur introuvable : " + id);
                            }
                        }

                        case 9 -> augmenterSalaire(scanner, service);

                        case 10 -> ajouterCollaborateur(scanner, service);

                        default -> System.out.println("Option invalide. Choisissez entre 0 et 10.");
                    }
                } else {
                    System.out.println("Saisie invalide. Veuillez entrer un nombre.");
                    scanner.nextLine();
                }
            }
        } catch (PersistenceException e) {
            System.err.println("Connexion à la base impossible. Vérifiez que MariaDB est démarrée "
                    + "et que la base efrei_tp4 est accessible.");
            logger.error("Erreur lors de l'accès à la base de données", e);
        } catch (IllegalArgumentException e) {
            System.err.println("Donnée invalide : " + e.getMessage());
            logger.error("Donnée invalide lors du démarrage ou de la création d'un collaborateur", e);
        }
    }

    // --- Méthodes statiques utilitaires ---

    private static void afficherMenu() {
        System.out.println();
        System.out.println("=== Annuaire des collaborateurs (" + "EFREI" + ") ===");
        System.out.println("1. Afficher tous les collaborateurs");
        System.out.println("2. Rechercher par nom");
        System.out.println("3. Filtrer par salaire minimum");
        System.out.println("4. Afficher les programmeurs");
        System.out.println("5. Trier par nom");
        System.out.println("6. Trier par salaire");
        System.out.println("7. Trier par nom puis prénom");
        System.out.println("8. Afficher la fiche d'un collaborateur");
        System.out.println("9. Augmenter le salaire d'un collaborateur");
        System.out.println("10. Ajouter un collaborateur");
        System.out.println("0. Quitter");
        System.out.print("Votre choix : ");
    }

    private static void augmenterSalaire(Scanner scanner, CollaborateurService service) {
        System.out.print("Identifiant du collaborateur : ");
        String id = scanner.nextLine();
        Collaborateur avant = service.rechercherParIdentifiant(id);
        if (avant == null) {
            System.out.println("Collaborateur introuvable : " + id);
            return;
        }

        System.out.print("Pourcentage d'augmentation : ");
        double pourcentage;
        try {
            pourcentage = Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Saisie invalide.");
            return;
        }

        Collaborateur apres = service.augmenterSalaire(id, pourcentage);
        if (apres == null) {
            System.out.println("Collaborateur introuvable : " + id);
        } else {
            System.out.printf("Salaire : %.2f € -> %.2f €%n",
                    avant.getSalaire(), apres.getSalaire());
        }
    }

    private static void ajouterCollaborateur(Scanner scanner, CollaborateurService service) {
        System.out.print("Type (1 = programmeur, 2 = testeur) : ");
        String type = scanner.nextLine();
        if (!type.equals("1") && !type.equals("2")) {
            System.out.println("Type invalide.");
            return;
        }

        System.out.print("Identifiant : ");
        String identifiant = scanner.nextLine();
        System.out.print("Prénom : ");
        String prenom = scanner.nextLine();
        System.out.print("Nom : ");
        String nom = scanner.nextLine();
        System.out.print("Salaire (€) : ");
        double salaire;
        try {
            salaire = Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Saisie invalide : le salaire doit être un nombre.");
            return;
        }
        if (!Double.isFinite(salaire) || salaire < 0) {
            System.out.println("Saisie invalide : le salaire doit être positif ou nul.");
            return;
        }

        System.out.print("Rue : ");
        String rue = scanner.nextLine();
        System.out.print("Code postal : ");
        String codePostal = scanner.nextLine();
        System.out.print("Ville : ");
        String ville = scanner.nextLine();
        System.out.print("Pays : ");
        String pays = scanner.nextLine();
        Adresse adresse = new Adresse(rue, codePostal, ville, pays);

        Collaborateur collaborateur;
        if (type.equals("1")) {
            System.out.print("Langage préféré : ");
            String langage = scanner.nextLine();
            collaborateur = new Programmeur(identifiant, prenom, nom, salaire, adresse, langage);
        } else {
            collaborateur = new Testeur(identifiant, prenom, nom, salaire, adresse);
        }

        try {
            service.ajouter(collaborateur);
            System.out.println("Collaborateur ajouté.");
        } catch (CollaborateurDejaExistantException e) {
            System.out.println("Impossible d'ajouter le collaborateur : l'identifiant "
                    + e.identifiant() + " est déjà utilisé.");
        }
    }

    /**
     * Affiche une liste de collaborateurs.
     * Utilise List<? extends Collaborateur> pour accepter aussi bien
     * List<Collaborateur> que List<Programmeur> (covariance via wildcard borné).
     */
    private static void afficherListe(List<? extends Collaborateur> liste) {
        if (liste.isEmpty()) {
            System.out.println("Aucun collaborateur trouvé.");
        } else {
            System.out.println(liste.size() + " collaborateur(s) :");
            for (Collaborateur c : liste) {
                System.out.println("  " + c);
            }
        }
    }
}
