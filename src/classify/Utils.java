package classify;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import static java.lang.System.out;

public class Utils {

    public static final String EMAIL_REGEX =
            "^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)" +
            "*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$"
    ;

    public static void validateEmail(String email) {
        if (!email.matches(Utils.EMAIL_REGEX)) {
            throw new IllegalArgumentException("Invalid email " + email);
        }
    }

    public static Optional<Integer> acceptInt(String msg) {
        return acceptInt(new Scanner(System.in), msg);
    }

    public static Optional<Integer> acceptInt(Scanner in, String msg) {
        return acceptInt(in, msg, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    public static Optional<Integer> acceptInt(String msg, int min, int max) {
        return acceptInt(new Scanner(System.in), msg, min, max);
    }

    public static Optional<Integer> acceptInt(Scanner in, String msg, int min, int max) {
        return acceptNumber(in, msg, min, max, true).map(Double::intValue);
    }

    public static Optional<Double> acceptDouble(String msg) {
        return acceptDouble(new Scanner(System.in), msg);
    }

    public static Optional<Double> acceptDouble(Scanner in, String msg) {
        return acceptDouble(in, msg, Double.MIN_VALUE, Double.MAX_VALUE);
    }

    public static Optional<Double> acceptDouble(String msg, double min, double max) {
        return acceptDouble(new Scanner(System.in), msg, min, max);
    }

    public static Optional<Double> acceptDouble(Scanner in, String msg, double min, double max) {
        return acceptNumber(in, msg, min, max, false);
    }

    private static Optional<Double> acceptNumber(
            Scanner in,
            String msg,
            double min,
            double max,
            boolean isInt
    ) {
        while (true) {
            try {
                out.print(msg);
                Optional<Double> value = readNumber(in, isInt);
                if (value.isPresent()
                    && !(value.get() >= min && value.get() <= max)) {
                    out.printf("Número deve estar entre %s e %s\n", min, max);
                    continue;
                }
                return value;
            }
            catch (NumberFormatException ex) {
                out.println("Número inválido.");
            }
            out.println();
        }
    }

    private static Optional<Double> readNumber(Scanner in, boolean isInt) {
        var input = in.nextLine();
        if (input.trim().isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(isInt ? Integer.parseInt(input) : Double.parseDouble(input));
    }

    public static String center(String str, int len) {
        if (str.isEmpty()) {
            throw new IllegalArgumentException("Can't center the empty string.");
        }
        if (len <= str.length()) {
            throw new IllegalArgumentException(String.format(
                    "Length (%d) should be > than string length (%d).",
                    len, str.length()
            ));
        }
        String init = " ".repeat(len);
        StringBuilder sb = new StringBuilder(init);
        int start = (len - str.length())/2;
        int end = start + str.length();
        for (int i = start, j = 0; i < end; i += 1, j += 1) {
            sb.setCharAt(i, str.charAt(j));
        }
        return sb.toString();
    }

    public static void clearScreen() {
        // https://stackoverflow.com/a/33379766
        // na mesma thread: https://stackoverflow.com/a/38365871
        // https://www.mkyong.com/java/how-to-detect-os-in-java-systemgetpropertyosname/
        if (!inUnix() && !inWindows()) {
            return;
        }
        List<String> cmd = inUnix() ? List.of("clear") : List.of("cmd", "/c", "cls");
        try {
            new ProcessBuilder(cmd).inheritIO().start().waitFor();
        }
        catch (IOException | InterruptedException ignored) {}
    }

    public static void pause(String msg) {
        if (!inUnix() && !inWindows()) {
            return;
        }
        List<String> cmd = inUnix() ?
            List.of("sh", "-c", String.format("read -s -n 1 -p '%s'", msg)) :
            List.of("cmd", "/c", String.format("pause>nul|set/p='%s'", msg))
            ;
        try {
            new ProcessBuilder(cmd).inheritIO().start().waitFor();
        }
        catch (IOException | InterruptedException ignored) {}
    }

    public static void pause() {
        pause("Pressione qualquer tecla para continuar...");
    }

    public static void pauseMute() {
        pause("");
    }

    public static boolean inWindows() {
        return System.getProperty("os.name").toLowerCase().contains("Windows");
    }

    public static boolean inUnix() {
        return inLinux() || inMacOS() || inBSD();
    }

    public static boolean inLinux() {
        return System.getProperty("os.name").toLowerCase().contains("linux");
    }

    public static boolean inMacOS() {
        return System.getProperty("os.name").toLowerCase().contains("mac");
    }

    public static boolean inBSD() {
        return System.getProperty("os.name").toLowerCase().contains("bsd");
    }

    // Class just a namespace, not for instantiating...
    private Utils() {}
}
