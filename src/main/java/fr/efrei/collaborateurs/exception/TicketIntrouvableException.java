package fr.efrei.collaborateurs.exception;

public class TicketIntrouvableException extends RuntimeException {

    private final Long id;

    public TicketIntrouvableException(Long id) {
        super("Aucun ticket avec l'id " + id);
        this.id = id;
    }

    public Long id() { return id; }
}

