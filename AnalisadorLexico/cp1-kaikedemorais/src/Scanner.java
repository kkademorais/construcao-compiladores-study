import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Scanner {

    private static final int TAMANHO_MAXIMO_IDENTIFICADOR = 20;

    private static final Set<String> PALAVRAS_RESERVADAS = Set.of(
            "int", "double", "bool", "char", "string", "void",
            "if", "else", "while",
            "function", "return");

    private static final String OPERADORES_SIMPLES = "=<>+-*/%";
    private static final Set<String> OPERADORES_DUPLOS =
            Set.of("==", "!=", "<=", ">=", "&&", "||", "**");
    private static final String INICIOS_DE_OPERADOR = "=<>+-*/%!&|";

    private final String fonte;
    private int posicao = 0;
    private int linha = 1;
    private int coluna = 1;
    private final List<LexicalError> erros = new ArrayList<>();

    public Scanner(String fonte) {
        this.fonte = fonte;
    }


    public boolean hasNext() {
        return posicao < fonte.length();
    }

    private char peek() {
        return peek(0);
    }

    private char peek(int deslocamento) {
        int i = posicao + deslocamento;
        if (i < fonte.length()) {
            return fonte.charAt(i);
        }
        return '\0';
    }

    private char advance() {
        char c = fonte.charAt(posicao++);
        if (c == '\n') {
            linha++;
            coluna = 1;
        } else {
            coluna++;
        }
        return c;
    }

    private void registrarErro(String mensagem, int linhaErro, int colunaErro) {
        erros.add(new LexicalError(mensagem, linhaErro, colunaErro));
    }

    public List<LexicalError> getErros() {
        return erros;
    }


    private static boolean ehLetra(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    private static boolean ehDigito(char c) {
        return c >= '0' && c <= '9';
    }


    public List<Token> tokenizar() {
        List<Token> tokens = new ArrayList<>();
        Token token;
        do {
            token = nextToken();
            tokens.add(token);
        } while (token.getTipo() != TokenType.EOF);
        return tokens;
    }


    public Token nextToken() {
        while (true) {
            ignorarEspacosEComentarios();
            if (!hasNext()) {
                return new Token(TokenType.EOF, "", linha, coluna);
            }

            int linhaInicio = linha;
            int colunaInicio = coluna;
            char c = peek();
            Token token;

            if (ehLetra(c)) {
                token = lerIdentificadorOuReservada(linhaInicio, colunaInicio);
            } else if (ehDigito(c)) {
                token = lerLiteralNumerico(linhaInicio, colunaInicio);
            } else if (c == '"') {
                token = lerString(linhaInicio, colunaInicio);
            } else if (INICIOS_DE_OPERADOR.indexOf(c) >= 0) {
                token = lerOperador(linhaInicio, colunaInicio);
            } else {
                advance(); // recuperação: descarta o caractere inválido e segue
                registrarErro("caractere inválido '" + c + "'", linhaInicio, colunaInicio);
                token = null;
            }

            if (token != null) {
                return token;
            }
        }
    }


    private void ignorarEspacosEComentarios() {
        while (hasNext()) {
            char c = peek();
            if (c == ' ' || c == '\t' || c == '\r' || c == '\n') {
                advance();
            } else if (c == '/' && peek(1) == '/') {
                ignorarComentarioDeLinha();
            } else if (c == '/' && peek(1) == '*') {
                ignorarComentarioDeBloco();
            } else {
                return;
            }
        }
    }

    private void ignorarComentarioDeLinha() {
        while (hasNext() && peek() != '\n') {
            advance();
        }
    }


    private void ignorarComentarioDeBloco() {
        int linhaInicio = linha;
        int colunaInicio = coluna;
        advance(); // '/'
        advance(); // '*'
        int profundidade = 1;

        while (profundidade > 0) {
            if (!hasNext()) {
                registrarErro("comentário de bloco não fechado até o fim do arquivo",
                        linhaInicio, colunaInicio);
                return;
            }
            if (peek() == '/' && peek(1) == '*') {
                advance();
                advance();
                profundidade++;
            } else if (peek() == '*' && peek(1) == '/') {
                advance();
                advance();
                profundidade--;
            } else {
                advance();
            }
        }
    }

    // IDENTIFICADOR

    private enum EstadoIdentificador { S0, S1, FIM }

    private Token lerIdentificadorOuReservada(int linhaInicio, int colunaInicio) {
        StringBuilder lexema = new StringBuilder();
        EstadoIdentificador estado = EstadoIdentificador.S0;

        while (estado != EstadoIdentificador.FIM) {
            switch (estado) {
                case S0: // início: exige uma letra
                    lexema.append(advance());
                    estado = EstadoIdentificador.S1;
                    break;
                case S1: // aceitação: continua enquanto houver letra ou dígito
                    if (ehLetra(peek()) || ehDigito(peek())) {
                        lexema.append(advance());
                    } else {
                        estado = EstadoIdentificador.FIM;
                    }
                    break;
                default:
                    break;
            }
        }

        String texto = lexema.toString();
        if (PALAVRAS_RESERVADAS.contains(texto)) {
            return new Token(TokenType.PALAVRA_RESERVADA, texto, linhaInicio, colunaInicio);
        }
        if (texto.length() > TAMANHO_MAXIMO_IDENTIFICADOR) {
            registrarErro("identificador '" + texto + "' excede o tamanho máximo de "
                    + TAMANHO_MAXIMO_IDENTIFICADOR + " caracteres", linhaInicio, colunaInicio);
            return null;
        }
        return new Token(TokenType.IDENTIFICADOR, texto, linhaInicio, colunaInicio);
    }

    // STRING

    private enum EstadoString { S0, S1, S2, ERRO }

    private Token lerString(int linhaInicio, int colunaInicio) {
        StringBuilder lexema = new StringBuilder();
        EstadoString estado = EstadoString.S0;

        while (estado != EstadoString.S2 && estado != EstadoString.ERRO) {
            switch (estado) {
                case S0: // aspas de abertura
                    lexema.append(advance());
                    estado = EstadoString.S1;
                    break;
                case S1: // conteúdo
                    if (!hasNext()) {
                        estado = EstadoString.ERRO;
                    } else if (peek() == '"') {
                        lexema.append(advance());
                        estado = EstadoString.S2;
                    } else {
                        lexema.append(advance());
                    }
                    break;
                default:
                    break;
            }
        }

        if (estado == EstadoString.ERRO) {
            registrarErro("string não fechada até o fim do arquivo", linhaInicio, colunaInicio);
            return null;
        }
        return new Token(TokenType.STRING, lexema.toString(), linhaInicio, colunaInicio);
    }

    // OPERADOR

    private enum EstadoOperador { S0, S1, S2 }

    private Token lerOperador(int linhaInicio, int colunaInicio) {
        StringBuilder lexema = new StringBuilder();
        EstadoOperador estado = EstadoOperador.S0;

        while (true) {
            switch (estado) {
                case S0:
                    lexema.append(advance());
                    estado = EstadoOperador.S1;
                    break;
                case S1: {
                    String candidato = lexema.toString() + peek();
                    if (hasNext() && OPERADORES_DUPLOS.contains(candidato)) {
                        lexema.append(advance());
                        estado = EstadoOperador.S2;
                    } else if (OPERADORES_SIMPLES.indexOf(lexema.charAt(0)) >= 0) {
                        return new Token(TokenType.OPERADOR, lexema.toString(), linhaInicio, colunaInicio);
                    } else {
                        registrarErro("operador incompleto '" + lexema + "'", linhaInicio, colunaInicio);
                        return null;
                    }
                    break;
                }
                case S2:
                    return new Token(TokenType.OPERADOR, lexema.toString(), linhaInicio, colunaInicio);
            }
        }
    }

    // LITERAL NUMÉRICO

    private enum EstadoNumero { S0, S1, S2, S3, FIM, ERRO }

    private Token lerLiteralNumerico(int linhaInicio, int colunaInicio) {
        StringBuilder lexema = new StringBuilder();
        EstadoNumero estado = EstadoNumero.S0;

        while (estado != EstadoNumero.FIM && estado != EstadoNumero.ERRO) {
            switch (estado) {
                case S0: // primeiro dígito
                    lexema.append(advance());
                    estado = EstadoNumero.S1;
                    break;
                case S1: // parte inteira
                    if (ehDigito(peek())) {
                        lexema.append(advance());
                    } else if (peek() == '.') {
                        lexema.append(advance());
                        estado = EstadoNumero.S2;
                    } else {
                        estado = EstadoNumero.FIM;
                    }
                    break;
                case S2: // ponto visto: exige ao menos um dígito
                    if (ehDigito(peek())) {
                        lexema.append(advance());
                        estado = EstadoNumero.S3;
                    } else {
                        estado = EstadoNumero.ERRO;
                    }
                    break;
                case S3: // parte decimal
                    if (ehDigito(peek())) {
                        lexema.append(advance());
                    } else {
                        estado = EstadoNumero.FIM;
                    }
                    break;
                default:
                    break;
            }
        }

        if (estado == EstadoNumero.ERRO) {
            registrarErro("literal numérico malformado '" + lexema
                    + "': esperado dígito após o ponto", linhaInicio, colunaInicio);
            return null;
        }
        return new Token(TokenType.LITERAL_NUMERICO, lexema.toString(), linhaInicio, colunaInicio);
    }
}
