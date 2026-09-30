# Log de uso de IA

## Etapa: Checkpoint 1 (especificação léxica e scanner)

**Ferramenta:** Claude Code (Claude Sonnet 5.5).

**Declaração:** a implementação do scanner em Java (`src/`) e da suíte de testes (`test/`) foi gerada pela IA a partir da especificação léxica (`especificacao-lexica.md`), do enunciado do checkpoint, da Aula 2 e do desenho dos AFDs (`automatos/`). O enunciado classifica esse uso como "vedado sem declarar"; por isso ele está declarado aqui.

**O que a IA fez:**
- Criou `Token`, `TokenType`, `LexicalError`, `Scanner` e `Main`, com um método por AFD e um `case` por estado (S0, S1...), seguindo o desenho.
- Criou `ScannerTest` (33 verificações) e a evidência de execução em `test/evidencia-execucao.txt`.
