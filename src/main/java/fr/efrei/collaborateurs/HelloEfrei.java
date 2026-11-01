package fr.efrei.collaborateurs;

import fr.efrei.collaborateurs.service.CollaborateurService;
import fr.efrei.collaborateurs.service.TicketService;
import fr.efrei.collaborateurs.ui.ConsoleApp;
import jakarta.persistence.EntityManagerFactory;

public class HelloEfrei {

    public static void main(String[] args) {
        EntityManagerFactory emf = JpaUtil.initEMFactory();
        try {
            CollaborateurService collabService = new CollaborateurService(emf);
            TicketService ticketService = new TicketService(emf);
            new ConsoleApp(collabService, ticketService).lancer();
        } finally {
            JpaUtil.fermerEMFactory();
        }
    }
}
