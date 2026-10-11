public class CollaborateurDejaExistantException extends RuntimeException {
    private final String identifiant;

    public CollaborateurDejaExistantException(String identifiant) {
        super("Collaborateur existant : " + identifiant);
        this.identifiant = identifiant;
    }

    public String identifiant() {
        return identifiant;
    }
}
