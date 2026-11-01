package fr.efrei.collaborateurs.ui;

import fr.efrei.collaborateurs.exception.CollaborateurDejaExistantException;
import fr.efrei.collaborateurs.exception.CollaborateurIntrouvableException;
import fr.efrei.collaborateurs.exception.TicketIntrouvableException;
import fr.efrei.collaborateurs.modele.*;
import fr.efrei.collaborateurs.service.CollaborateurService;
import fr.efrei.collaborateurs.service.TicketService;
import jakarta.persistence.PersistenceException;

import java.util.List;
import java.util.Scanner;

public class ConsoleApp {

    private final CollaborateurService collaborateurService;
    private final TicketService ticketService;
    private final Scanner scanner = new Scanner(System.in);

    public ConsoleApp(CollaborateurService collaborateurService, TicketService ticketService) {
        this.collaborateurService = collaborateurService;
        this.ticketService = ticketService;
    }

    public void lancer() {
        boolean continuer = true;
        while (continuer) {
            afficherMenu();
            String choix = scanner.nextLine().trim();
            try {
                switch (choix) {
                    case "1" -> ajouterProgrammeur();
                    case "2" -> ajouterTesteur();
                    case "3" -> rechercherParNom();
                    case "4" -> augmenter();
                    case "5" -> listerParLangage();
                    case "10" -> creerTicket();
                    case "11" -> consulterTickets();
                    case "12" -> assignerTicket();
                    case "13" -> demarrerTicket();
                    case "14" -> soumettreTicket();
                    case "15" -> validerTicket();
                    case "16" -> renvoyerTicket();
                    case "17" -> ticketsCollaborateur();
                    case "18" -> modifierTicket();
                    case "19" -> supprimerTicket();
                    case "20" -> ticketsARepartir();
                    case "0" -> continuer = false;
                    default -> System.out.println("Choix inconnu.");
                }
            } catch (CollaborateurDejaExistantException e) {
                System.out.println("Cet identifiant est déjà pris (" + e.identifiant() + ").");
            } catch (CollaborateurIntrouvableException e) {
                System.out.println("Collaborateur introuvable : " + e.identifiant());
            } catch (TicketIntrouvableException e) {
                System.out.println("Ticket introuvable : #" + e.id());
            } catch (IllegalStateException e) {
                System.out.println("Opération refusée : " + e.getMessage());
            } catch (SaisieInvalideException | IllegalArgumentException e) {
                System.out.println("Saisie refusée : " + e.getMessage());
            } catch (PersistenceException e) {
                System.out.println("Problème technique, l'opération n'a pas été enregistrée.");
            }
        }
        System.out.println("Au revoir !");
    }

    private void afficherMenu() {
        System.out.println();
        System.out.println("=== EFREIForge ===");
        System.out.println("--- Collaborateurs ---");
        System.out.println(" 1. Ajouter un programmeur");
        System.out.println(" 2. Ajouter un testeur");
        System.out.println(" 3. Rechercher par nom");
        System.out.println(" 4. Augmenter un salaire");
        System.out.println(" 5. Programmeurs par langage");
        System.out.println("--- Tickets ---");
        System.out.println("10. Créer un ticket");
        System.out.println("11. Consulter les tickets");
        System.out.println("12. Assigner un ticket");
        System.out.println("13. Démarrer un ticket");
        System.out.println("14. Soumettre à validation");
        System.out.println("15. Valider un ticket");
        System.out.println("16. Renvoyer un ticket en cours");
        System.out.println("17. Tickets d'un collaborateur");
        System.out.println("18. Modifier un ticket");
        System.out.println("19. Supprimer un ticket");
        System.out.println("20. Tickets à répartir");
        System.out.println(" 0. Quitter");
        System.out.print("Votre choix : ");
    }

    private void ajouterProgrammeur() {
        String id = lireTexte("Identifiant");
        String prenom = lireTexte("Prénom");
        String nom = lireTexte("Nom");
        double salaire = lireDecimal("Salaire");
        String langage = lireTexte("Langage préféré");
        collaborateurService.ajouter(new Programmeur(id, prenom, nom, salaire, lireAdresse(), langage));
        System.out.println("Programmeur enregistré.");
    }

    private void ajouterTesteur() {
        String id = lireTexte("Identifiant");
        String prenom = lireTexte("Prénom");
        String nom = lireTexte("Nom");
        double salaire = lireDecimal("Salaire");
        collaborateurService.ajouter(new Testeur(id, prenom, nom, salaire, lireAdresse()));
        System.out.println("Testeur enregistré.");
    }

    private void rechercherParNom() {
        List<Collaborateur> resultats = collaborateurService.rechercherParNom(lireTexte("Fragment du nom"));
        if (resultats.isEmpty()) {
            System.out.println("Aucun résultat.");
        } else {
            resultats.forEach(System.out::println);
        }
    }

    private void augmenter() {
        String id = lireTexte("Identifiant");
        double pct = lireDecimal("Pourcentage d'augmentation");
        double nouveau = collaborateurService.augmenterSalaire(id, pct);
        System.out.printf("Nouveau salaire : %.2f EUR%n", nouveau);
    }

    private void listerParLangage() {
        List<Programmeur> resultats = collaborateurService.programmeursParLangage(lireTexte("Langage"));
        if (resultats.isEmpty()) {
            System.out.println("Aucun résultat.");
        } else {
            resultats.forEach(System.out::println);
        }
    }

    private void creerTicket() {
        String titre = lireTexte("Titre");
        Priorite priorite = lirePriorite();
        Ticket ticket = ticketService.creer(titre, priorite);
        System.out.println("Ticket créé : " + ticket);
    }

    private void consulterTickets() {
        System.out.println("Filtrer par statut ? (1=À faire, 2=En cours, 3=À valider, 4=Terminé, Entrée=tous)");
        System.out.print("Choix : ");
        String choix = scanner.nextLine().trim();

        List<Ticket> tickets;
        if (choix.isEmpty()) {
            tickets = ticketService.listerTous();
        } else {
            Statut statut = switch (choix) {
                case "1" -> Statut.A_FAIRE;
                case "2" -> Statut.EN_COURS;
                case "3" -> Statut.A_VALIDER;
                case "4" -> Statut.TERMINE;
                default -> throw new SaisieInvalideException("Choix de statut invalide");
            };
            tickets = ticketService.listerParStatut(statut);
        }

        if (tickets.isEmpty()) {
            System.out.println("Aucun ticket.");
        } else {
            tickets.forEach(System.out::println);
        }
    }

    private void assignerTicket() {
        long ticketId = lireLong("Numéro du ticket");
        String collabId = lireTexte("Identifiant du programmeur");
        ticketService.assigner(ticketId, collabId);
        System.out.println("Ticket #" + ticketId + " assigné à " + collabId + ".");
    }

    private void demarrerTicket() {
        long ticketId = lireLong("Numéro du ticket");
        ticketService.demarrer(ticketId);
        System.out.println("Ticket #" + ticketId + " démarré.");
    }

    private void soumettreTicket() {
        long ticketId = lireLong("Numéro du ticket");
        ticketService.soumettre(ticketId);
        System.out.println("Ticket #" + ticketId + " soumis à validation.");
    }

    private void validerTicket() {
        long ticketId = lireLong("Numéro du ticket");
        String testeurId = lireTexte("Identifiant du testeur");
        ticketService.valider(ticketId, testeurId);
        System.out.println("Ticket #" + ticketId + " validé.");
    }

    private void renvoyerTicket() {
        long ticketId = lireLong("Numéro du ticket");
        String testeurId = lireTexte("Identifiant du testeur");
        ticketService.renvoyer(ticketId, testeurId);
        System.out.println("Ticket #" + ticketId + " renvoyé en cours.");
    }

    private void ticketsCollaborateur() {
        String collabId = lireTexte("Identifiant du collaborateur");
        List<Ticket> tickets = ticketService.listerParCollaborateur(collabId);
        if (tickets.isEmpty()) {
            System.out.println("Aucun ticket pour ce collaborateur.");
        } else {
            tickets.forEach(System.out::println);
        }
    }

    private void modifierTicket() {
        long ticketId = lireLong("Numéro du ticket");
        System.out.println("Modifier : 1=Titre, 2=Priorité");
        System.out.print("Choix : ");
        String choix = scanner.nextLine().trim();
        switch (choix) {
            case "1" -> {
                ticketService.modifierTitre(ticketId, lireTexte("Nouveau titre"));
                System.out.println("Titre modifié.");
            }
            case "2" -> {
                ticketService.modifierPriorite(ticketId, lirePriorite());
                System.out.println("Priorité modifiée.");
            }
            default -> System.out.println("Choix invalide.");
        }
    }

    private void supprimerTicket() {
        long ticketId = lireLong("Numéro du ticket");
        ticketService.supprimer(ticketId);
        System.out.println("Ticket #" + ticketId + " supprimé.");
    }

    private void ticketsARepartir() {
        List<Ticket> tickets = ticketService.listerNonAssignes();
        if (tickets.isEmpty()) {
            System.out.println("Tous les tickets sont assignés.");
        } else {
            System.out.println("Tickets à répartir (" + tickets.size() + ")");
            tickets.forEach(System.out::println);
        }
    }

    private Adresse lireAdresse() {
        return new Adresse(lireTexte("Rue"), lireTexte("Code postal"), lireTexte("Ville"), lireTexte("Pays"));
    }

    private Priorite lirePriorite() {
        System.out.print("Priorité (1=Basse, 2=Normale, 3=Haute) : ");
        String choix = scanner.nextLine().trim();
        return switch (choix) {
            case "1" -> Priorite.BASSE;
            case "2" -> Priorite.NORMALE;
            case "3" -> Priorite.HAUTE;
            default -> throw new SaisieInvalideException("Priorité invalide");
        };
    }

    private String lireTexte(String libelle) {
        System.out.print(libelle + " : ");
        String valeur = scanner.nextLine().trim();
        if (valeur.isEmpty()) {
            throw new SaisieInvalideException(libelle + " ne peut pas être vide");
        }
        return valeur;
    }

    private double lireDecimal(String libelle) {
        String texte = lireTexte(libelle);
        try {
            return Double.parseDouble(texte.replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new SaisieInvalideException("\"" + texte + "\" n'est pas un nombre");
        }
    }

    private long lireLong(String libelle) {
        String texte = lireTexte(libelle);
        try {
            return Long.parseLong(texte);
        } catch (NumberFormatException e) {
            throw new SaisieInvalideException("\"" + texte + "\" n'est pas un nombre valide");
        }
    }

    private static class SaisieInvalideException extends RuntimeException {
        SaisieInvalideException(String message) {
            super(message);
        }
    }
}
