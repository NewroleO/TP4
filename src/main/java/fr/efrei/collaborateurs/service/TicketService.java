package fr.efrei.collaborateurs.service;

import fr.efrei.collaborateurs.exception.CollaborateurIntrouvableException;
import fr.efrei.collaborateurs.exception.TicketIntrouvableException;
import fr.efrei.collaborateurs.modele.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

public class TicketService {

    private final EntityManagerFactory emFactory;

    public TicketService(EntityManagerFactory emf) {
        this.emFactory = emf;
    }

    public Ticket creer(String titre, Priorite priorite) {
        Ticket ticket = new Ticket(titre, priorite);
        return dansUneTransaction(em -> {
            em.persist(ticket);
            return ticket;
        });
    }

    public List<Ticket> listerTous() {
        try (EntityManager em = emFactory.createEntityManager()) {
            return em.createQuery("SELECT t FROM Ticket t ORDER BY t.id", Ticket.class)
                    .getResultList();
        }
    }

    public List<Ticket> listerParStatut(Statut statut) {
        try (EntityManager em = emFactory.createEntityManager()) {
            return em.createQuery(
                            "SELECT t FROM Ticket t WHERE t.statut = :statut ORDER BY t.id", Ticket.class)
                    .setParameter("statut", statut)
                    .getResultList();
        }
    }

    public void assigner(Long ticketId, String collaborateurId) {
        dansUneTransaction(em -> {
            Ticket ticket = em.find(Ticket.class, ticketId);
            if (ticket == null) throw new TicketIntrouvableException(ticketId);

            Collaborateur collab = em.find(Collaborateur.class, collaborateurId);
            if (collab == null) throw new CollaborateurIntrouvableException(collaborateurId);

            if (!(collab instanceof Programmeur)) {
                throw new IllegalStateException("Seul un programmeur peut être assigné à un ticket");
            }

            if (ticket.getStatut() != Statut.A_FAIRE) {
                throw new IllegalStateException("Un ticket ne peut être assigné que s'il est à faire");
            }

            ticket.setAssigne(collab);
            return null;
        });
    }

    public void demarrer(Long ticketId) {
        dansUneTransaction(em -> {
            Ticket ticket = em.find(Ticket.class, ticketId);
            if (ticket == null) throw new TicketIntrouvableException(ticketId);

            if (ticket.getStatut() != Statut.A_FAIRE) {
                throw new IllegalStateException("Seul un ticket à faire peut être démarré");
            }

            if (ticket.getAssigne() == null) {
                throw new IllegalStateException("Le ticket doit être assigné avant d'être démarré");
            }

            long enCours = em.createQuery(
                            "SELECT COUNT(t) FROM Ticket t WHERE t.assigne = :prog AND t.statut = :statut",
                            Long.class)
                    .setParameter("prog", ticket.getAssigne())
                    .setParameter("statut", Statut.EN_COURS)
                    .getSingleResult();

            if (enCours >= 3) {
                throw new IllegalStateException("Ce programmeur a déjà 3 tickets en cours");
            }

            ticket.setStatut(Statut.EN_COURS);
            return null;
        });
    }

    public void soumettre(Long ticketId) {
        dansUneTransaction(em -> {
            Ticket ticket = em.find(Ticket.class, ticketId);
            if (ticket == null) throw new TicketIntrouvableException(ticketId);

            if (ticket.getStatut() != Statut.EN_COURS) {
                throw new IllegalStateException("Seul un ticket en cours peut être soumis à validation");
            }

            ticket.setStatut(Statut.A_VALIDER);
            return null;
        });
    }

    public void valider(Long ticketId, String testeurId) {
        dansUneTransaction(em -> {
            Ticket ticket = em.find(Ticket.class, ticketId);
            if (ticket == null) throw new TicketIntrouvableException(ticketId);

            Collaborateur collab = em.find(Collaborateur.class, testeurId);
            if (collab == null) throw new CollaborateurIntrouvableException(testeurId);

            if (!(collab instanceof Testeur)) {
                throw new IllegalStateException("Seul un testeur peut valider un ticket");
            }

            if (ticket.getStatut() != Statut.A_VALIDER) {
                throw new IllegalStateException("Seul un ticket à valider peut être validé");
            }

            ticket.setStatut(Statut.TERMINE);
            return null;
        });
    }

    public void renvoyer(Long ticketId, String testeurId) {
        dansUneTransaction(em -> {
            Ticket ticket = em.find(Ticket.class, ticketId);
            if (ticket == null) throw new TicketIntrouvableException(ticketId);

            Collaborateur collab = em.find(Collaborateur.class, testeurId);
            if (collab == null) throw new CollaborateurIntrouvableException(testeurId);

            if (!(collab instanceof Testeur)) {
                throw new IllegalStateException("Seul un testeur peut renvoyer un ticket");
            }

            if (ticket.getStatut() != Statut.A_VALIDER) {
                throw new IllegalStateException("Seul un ticket à valider peut être renvoyé en cours");
            }

            long enCours = em.createQuery(
                            "SELECT COUNT(t) FROM Ticket t WHERE t.assigne = :prog AND t.statut = :statut",
                            Long.class)
                    .setParameter("prog", ticket.getAssigne())
                    .setParameter("statut", Statut.EN_COURS)
                    .getSingleResult();

            if (enCours >= 3) {
                throw new IllegalStateException("Le programmeur assigné a déjà 3 tickets en cours");
            }

            ticket.setStatut(Statut.EN_COURS);
            return null;
        });
    }

    public List<Ticket> listerParCollaborateur(String collaborateurId) {
        try (EntityManager em = emFactory.createEntityManager()) {
            Collaborateur collab = em.find(Collaborateur.class, collaborateurId);
            if (collab == null) throw new CollaborateurIntrouvableException(collaborateurId);

            return em.createQuery(
                            "SELECT t FROM Ticket t WHERE t.assigne.identifiant = :id ORDER BY t.id",
                            Ticket.class)
                    .setParameter("id", collaborateurId)
                    .getResultList();
        }
    }

    public void modifierTitre(Long ticketId, String nouveauTitre) {
        dansUneTransaction(em -> {
            Ticket ticket = em.find(Ticket.class, ticketId);
            if (ticket == null) throw new TicketIntrouvableException(ticketId);

            if (ticket.getStatut() == Statut.TERMINE) {
                throw new IllegalStateException("Un ticket terminé ne peut plus être modifié");
            }

            ticket.setTitre(nouveauTitre);
            return null;
        });
    }

    public void modifierPriorite(Long ticketId, Priorite nouvellePriorite) {
        dansUneTransaction(em -> {
            Ticket ticket = em.find(Ticket.class, ticketId);
            if (ticket == null) throw new TicketIntrouvableException(ticketId);

            if (ticket.getStatut() == Statut.TERMINE) {
                throw new IllegalStateException("Un ticket terminé ne peut plus être modifié");
            }

            ticket.setPriorite(nouvellePriorite);
            return null;
        });
    }

    public void supprimer(Long ticketId) {
        dansUneTransaction(em -> {
            Ticket ticket = em.find(Ticket.class, ticketId);
            if (ticket == null) throw new TicketIntrouvableException(ticketId);

            if (ticket.getStatut() != Statut.A_FAIRE) {
                throw new IllegalStateException("Seul un ticket à faire peut être supprimé");
            }

            em.remove(ticket);
            return null;
        });
    }

    public List<Ticket> listerNonAssignes() {
        try (EntityManager em = emFactory.createEntityManager()) {
            List<Ticket> tickets = new ArrayList<>(em.createQuery(
                            "SELECT t FROM Ticket t WHERE t.assigne IS NULL ORDER BY t.id", Ticket.class)
                    .getResultList());
            tickets.sort(Comparator.comparing(Ticket::getPriorite).reversed()
                    .thenComparing(Ticket::getId));
            return tickets;
        }
    }

    private <T> T dansUneTransaction(Function<EntityManager, T> travail) {
        try (EntityManager em = emFactory.createEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            tx.begin();
            try {
                T resultat = travail.apply(em);
                tx.commit();
                return resultat;
            } catch (RuntimeException e) {
                if (tx.isActive()) tx.rollback();
                throw e;
            }
        }
    }
}

