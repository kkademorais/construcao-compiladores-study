import java.util.ArrayList;
import java.util.List;


public class ScannerTest {

    private static int aprovados = 0;
    private static int falhas = 0;

    public static void main(String[] args) {
        // Uma categoria de cada
        testeIdentificador();
        testeIdentificadorCaseSensitive();
        testePalavrasReservadas();
        testeString();
        testeOperadoresSimplesEDuplos();
        testeMaximalMunch();
        testeLiteralNumerico();

        // Comentários e espaços
        testeComentarios();
        testeComentarioDeBlocoAninhado();

        // Erros léxicos
        testeErroStringNaoFechadaEmEOF();
        testeStringComQuebraDeLinhaNoConteudo();
        testeErroPontuacaoForaDaEspecificacao();
        testeErroCaractereForaDoAlfabeto();
        testeErroComentarioDeBlocoNaoFechado();
        testeErroIdentificadorMuitoLongo();
        testeErroLiteralNumericoMalformado();
        testeErroOperadorIncompleto();

        // Trecho realista
        testeTrechoRealista();

        System.out.println();
        System.out.println("Resultado: " + aprovados + " aprovados, " + falhas + " falhas");
        if (falhas > 0) {
            System.exit(1);
        }
    }


    private static void testeIdentificador() {
        Scanner s = new Scanner("total x1 contaItens");
        verificarTokens("identificadores", s.tokenizar(),
                "IDENTIFICADOR(total)", "IDENTIFICADOR(x1)", "IDENTIFICADOR(contaItens)", "EOF()");
        verificarErros("identificadores sem erro", s);
    }

    private static void testeIdentificadorCaseSensitive() {
        Scanner s = new Scanner("total Total If WHILE");
        verificarTokens("case-sensitive: maiúsculas não são reservadas", s.tokenizar(),
                "IDENTIFICADOR(total)", "IDENTIFICADOR(Total)", "IDENTIFICADOR(If)",
                "IDENTIFICADOR(WHILE)", "EOF()");
    }

    private static void testePalavrasReservadas() {
        Scanner s = new Scanner("int double bool char string void if else while function return");
        verificarTokens("palavras reservadas", s.tokenizar(),
                "PALAVRA_RESERVADA(int)", "PALAVRA_RESERVADA(double)", "PALAVRA_RESERVADA(bool)",
                "PALAVRA_RESERVADA(char)", "PALAVRA_RESERVADA(string)", "PALAVRA_RESERVADA(void)",
                "PALAVRA_RESERVADA(if)", "PALAVRA_RESERVADA(else)", "PALAVRA_RESERVADA(while)",
                "PALAVRA_RESERVADA(function)", "PALAVRA_RESERVADA(return)", "EOF()");

        // maximal munch: while1 é um identificador, não "while" seguido de "1"
        Scanner s2 = new Scanner("while1 intx");
        verificarTokens("reservada seguida de letra/dígito vira identificador", s2.tokenizar(),
                "IDENTIFICADOR(while1)", "IDENTIFICADOR(intx)", "EOF()");
    }

    private static void testeString() {
        Scanner s = new Scanner("\"ok\" \"linha 1\" \"\"");
        verificarTokens("strings", s.tokenizar(),
                "STRING(\"ok\")", "STRING(\"linha 1\")", "STRING(\"\")", "EOF()");
        verificarErros("strings sem erro", s);
    }

    private static void testeOperadoresSimplesEDuplos() {
        Scanner s = new Scanner("= < > + - * / % == != <= >= && || **");
        verificarTokens("operadores", s.tokenizar(),
                "OPERADOR(=)", "OPERADOR(<)", "OPERADOR(>)", "OPERADOR(+)", "OPERADOR(-)",
                "OPERADOR(*)", "OPERADOR(/)", "OPERADOR(%)",
                "OPERADOR(==)", "OPERADOR(!=)", "OPERADOR(<=)", "OPERADOR(>=)",
                "OPERADOR(&&)", "OPERADOR(||)", "OPERADOR(**)", "EOF()");
        verificarErros("operadores sem erro", s);
    }

    private static void testeMaximalMunch() {
        Scanner s = new Scanner("a===b <=> x***y");
        verificarTokens("maximal munch em operadores", s.tokenizar(),
                "IDENTIFICADOR(a)", "OPERADOR(==)", "OPERADOR(=)", "IDENTIFICADOR(b)",
                "OPERADOR(<=)", "OPERADOR(>)", "IDENTIFICADOR(x)", "OPERADOR(**)",
                "OPERADOR(*)", "IDENTIFICADOR(y)", "EOF()");
    }

    private static void testeLiteralNumerico() {
        Scanner s = new Scanner("10 3.14 0 007 0.5");
        verificarTokens("literais numéricos", s.tokenizar(),
                "LITERAL_NUMERICO(10)", "LITERAL_NUMERICO(3.14)", "LITERAL_NUMERICO(0)",
                "LITERAL_NUMERICO(007)", "LITERAL_NUMERICO(0.5)", "EOF()");
        verificarErros("literais sem erro", s);
    }

    // Comentários

    private static void testeComentarios() {
        Scanner s = new Scanner("a // resto ignorado 1 + 2\nb /* bloco */ c");
        verificarTokens("comentários de linha e de bloco", s.tokenizar(),
                "IDENTIFICADOR(a)", "IDENTIFICADOR(b)", "IDENTIFICADOR(c)", "EOF()");
        verificarErros("comentários sem erro", s);
    }

    private static void testeComentarioDeBlocoAninhado() {
        Scanner s = new Scanner("a /* fora /* dentro */ ainda comentário */ b");
        verificarTokens("comentário de bloco aninhado", s.tokenizar(),
                "IDENTIFICADOR(a)", "IDENTIFICADOR(b)", "EOF()");
        verificarErros("aninhado sem erro", s);
    }

    // Erros léxicos: posição correta e recuperação (segue tokenizando)

    private static void testeErroStringNaoFechadaEmEOF() {
        Scanner s = new Scanner("x = \"abc");
        verificarTokens("string não fechada em EOF: tokens anteriores preservados", s.tokenizar(),
                "IDENTIFICADOR(x)", "OPERADOR(=)", "EOF()");
        verificarErros("string não fechada em EOF", s, "1:5 string não fechada até o fim do arquivo");
    }

    private static void testeStringComQuebraDeLinhaNoConteudo() {
        Scanner s = new Scanner("x = \"abc\ndef\" y");
        verificarTokens("string aceita quebra de linha (AFD so erra em EOF)", s.tokenizar(),
                "IDENTIFICADOR(x)", "OPERADOR(=)", "STRING(\"abc\ndef\")", "IDENTIFICADOR(y)", "EOF()");
        verificarErros("string com quebra de linha", s);
    }

    private static void testeErroPontuacaoForaDaEspecificacao() {
        Scanner s = new Scanner("a ( b ) ; c");
        verificarTokens("pontuacao sem categoria na especificacao e erro", s.tokenizar(),
                "IDENTIFICADOR(a)", "IDENTIFICADOR(b)", "IDENTIFICADOR(c)", "EOF()");
        verificarErros("pontuacao", s,
                "1:3 caractere inválido '('",
                "1:7 caractere inválido ')'",
                "1:9 caractere inválido ';'");
    }

    private static void testeErroCaractereForaDoAlfabeto() {
        Scanner s = new Scanner("a @ b\n  ç c");
        verificarTokens("caractere inválido: scanner continua", s.tokenizar(),
                "IDENTIFICADOR(a)", "IDENTIFICADOR(b)", "IDENTIFICADOR(c)", "EOF()");
        verificarErros("caracteres inválidos", s,
                "1:3 caractere inválido '@'",
                "2:3 caractere inválido 'ç'");
    }

    private static void testeErroComentarioDeBlocoNaoFechado() {
        Scanner s = new Scanner("a\n/* nunca fecha /* nem aqui */ b");
        verificarTokens("comentário de bloco não fechado", s.tokenizar(),
                "IDENTIFICADOR(a)", "EOF()");
        verificarErros("comentário de bloco não fechado", s,
                "2:1 comentário de bloco não fechado até o fim do arquivo");
    }

    private static void testeErroIdentificadorMuitoLongo() {
        String vinte = "abcdefghijklmnopqrst";           // 20: válido
        String vinteEUm = vinte + "u";                    // 21: inválido
        Scanner s = new Scanner(vinte + " " + vinteEUm + " ok");
        verificarTokens("identificador com 21 caracteres é rejeitado", s.tokenizar(),
                "IDENTIFICADOR(" + vinte + ")", "IDENTIFICADOR(ok)", "EOF()");
        verificarErros("identificador longo", s,
                "1:22 identificador '" + vinteEUm + "' excede o tamanho máximo de 20 caracteres");
    }

    private static void testeErroLiteralNumericoMalformado() {
        Scanner s = new Scanner("x = 3. + 1");
        verificarTokens("literal '3.' malformado", s.tokenizar(),
                "IDENTIFICADOR(x)", "OPERADOR(=)", "OPERADOR(+)", "LITERAL_NUMERICO(1)", "EOF()");
        verificarErros("literal malformado", s,
                "1:5 literal numérico malformado '3.': esperado dígito após o ponto");
    }

    private static void testeErroOperadorIncompleto() {
        Scanner s = new Scanner("a ! b & c | d");
        verificarTokens("! & | sozinhos não são operadores", s.tokenizar(),
                "IDENTIFICADOR(a)", "IDENTIFICADOR(b)", "IDENTIFICADOR(c)", "IDENTIFICADOR(d)", "EOF()");
        verificarErros("operadores incompletos", s,
                "1:3 operador incompleto '!'",
                "1:7 operador incompleto '&'",
                "1:11 operador incompleto '|'");
    }

    // Trecho realista: várias construções, espaços e comentários misturados

    private static void testeTrechoRealista() {
        String codigo =
                "function int soma a b // soma dois inteiros\n"
              + "    /* resultado /* parcial */ */ return a+b\n"
              + "double taxa = 2.5 ** 2\n"
              + "while i <= 10 && ok != 0 string msg = \"fim\"";

        Scanner s = new Scanner(codigo);
        List<Token> tokens = s.tokenizar();
        verificarTokens("trecho realista", tokens,
                "PALAVRA_RESERVADA(function)", "PALAVRA_RESERVADA(int)", "IDENTIFICADOR(soma)",
                "IDENTIFICADOR(a)", "IDENTIFICADOR(b)",
                "PALAVRA_RESERVADA(return)", "IDENTIFICADOR(a)", "OPERADOR(+)", "IDENTIFICADOR(b)",
                "PALAVRA_RESERVADA(double)", "IDENTIFICADOR(taxa)", "OPERADOR(=)",
                "LITERAL_NUMERICO(2.5)", "OPERADOR(**)", "LITERAL_NUMERICO(2)",
                "PALAVRA_RESERVADA(while)", "IDENTIFICADOR(i)", "OPERADOR(<=)",
                "LITERAL_NUMERICO(10)", "OPERADOR(&&)", "IDENTIFICADOR(ok)", "OPERADOR(!=)",
                "LITERAL_NUMERICO(0)",
                "PALAVRA_RESERVADA(string)", "IDENTIFICADOR(msg)", "OPERADOR(=)", "STRING(\"fim\")",
                "EOF()");
        verificarErros("trecho realista sem erro", s);

        // posição: 'return' está na linha 2, coluna 35
        Token retorno = tokens.get(5);
        verificar("posição de 'return' = 2:35",
                retorno.getLinha() == 2 && retorno.getColuna() == 35,
                "obtido " + retorno.getLinha() + ":" + retorno.getColuna());
    }

    // Utilitários de verificação

    private static void verificarTokens(String nome, List<Token> obtidos, String... esperados) {
        List<String> descricao = new ArrayList<>();
        for (Token t : obtidos) {
            descricao.add(t.getTipo() + "(" + t.getLexema() + ")");
        }
        verificar(nome, descricao.equals(List.of(esperados)),
                "esperado " + List.of(esperados) + " mas obtido " + descricao);
    }

    // Cada erro esperado tem o formato "linha:coluna mensagem". 
    private static void verificarErros(String nome, Scanner s, String... esperados) {
        List<String> obtidos = new ArrayList<>();
        for (LexicalError e : s.getErros()) {
            obtidos.add(e.getLinha() + ":" + e.getColuna() + " " + e.getMensagem());
        }
        verificar("erros - " + nome, obtidos.equals(List.of(esperados)),
                "esperado " + List.of(esperados) + " mas obtido " + obtidos);
    }

    private static void verificar(String nome, boolean condicao, String detalhe) {
        if (condicao) {
            aprovados++;
            System.out.println("[OK]    " + nome);
        } else {
            falhas++;
            System.out.println("[FALHA] " + nome + "\n        " + detalhe);
        }
    }
}
