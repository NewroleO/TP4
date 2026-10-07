package fr.efrei.java;

public class CollaborateurDejaExistantException extends RuntimeException {
    private final String id;

    public CollaborateurDejaExistantException( String id) {
        super("Collaborateur existant : " + id);
        this.id = id;
    }
    public String id() {

        return id;

    }

}
