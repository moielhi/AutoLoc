package tn.esprit.autoloc.io;

public final class RapportFactory {

    private RapportFactory() {
        throw new IllegalStateException("Factory class");
    }

    public static IRapport createRapport(String type) {
        if ("CONSOLE".equalsIgnoreCase(type)) {
            return new RapportConsole();
        }
        return new RapportComplet();
    }
}
