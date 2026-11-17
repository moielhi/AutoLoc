package tn.esprit.autoloc;

public final class Utils {

    private Utils() {
        throw new IllegalStateException("Utility class");
    }

    public static boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }
}
