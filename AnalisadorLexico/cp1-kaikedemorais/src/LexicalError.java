
public class LexicalError {
    private final String mensagem;
    private final int linha;
    private final int coluna;

    public LexicalError(String mensagem, int linha, int coluna) {
        this.mensagem = mensagem;
        this.linha = linha;
        this.coluna = coluna;
    }

    public String getMensagem() { return mensagem; }
    public int getLinha() { return linha; }
    public int getColuna() { return coluna; }

    @Override
    public String toString() {
        return "Erro léxico em " + linha + ":" + coluna + " - " + mensagem;
    }
}
