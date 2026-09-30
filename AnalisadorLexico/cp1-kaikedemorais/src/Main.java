import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;


public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Uso: java Main <arquivo-fonte>");
            System.exit(2);
        }

        String fonte = Files.readString(Path.of(args[0]), StandardCharsets.UTF_8);
        Scanner scanner = new Scanner(fonte);

        for (Token token : scanner.tokenizar()) {
            System.out.println(token);
        }

        if (!scanner.getErros().isEmpty()) {
            System.out.println();
            for (LexicalError erro : scanner.getErros()) {
                System.out.println(erro);
            }
        }
    }
}
