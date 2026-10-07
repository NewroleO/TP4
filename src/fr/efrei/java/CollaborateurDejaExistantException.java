package fr.efrei.java;

public class CollaborateurDejaExistantException extends RuntimeException {
    private final String id;

    public CollaborateurDejaExistantException(String id, String id1) {
        super("Collaborateur existant : " + id);
        this.id = id1;
    }
    public String id() {

        return "";
    }

}
