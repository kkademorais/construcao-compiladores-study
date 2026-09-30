# Especificação léxica da linguagem
Trabalho 1 de Construção de Compiladores
Kaike de Morais Carvalho - 12421BCC051
GitHub do repositório: https://github.com/kkademorais/construcao-compiladores-study

---

## Categorias de tokens 

### Identificador
- Notação: [Aa-Zz]([Aa-Zz]|[0-9])*
- Case-sensitive
- "_" não permitido -> trabalhar com camelCase
- Tamanho máximo = 20

### Palavra Reservada
- Notação: [a-z]([a-z])* + tabela de busca
- Tipos básicos: int, double, bool, char, string, void
- Estruturas de controle: seleção (if/else) e repetição (while)
- Procedimentos e funções: declaração, parâmetros, chamada e retorno (com e sem valor)

### String
- Notação: "([Aa-Zz] != ")*"
- Retornar erro de sintaxe em caso de string não fechada

### Operador
- Notação: 1 ou 2 caracteres 
- 1 caractere: =, <, >, +, -, *, /, %
- 2 caracteres: ==, !=, <=, >=, &&, ||, **

### Literal numérico
- Notação: [0-9]+("."[0-9]+)?
- Notação científica ou hexadecimal não são permitidos

---

### 1. Qual é o alfabeto de entrada (que caracteres são válidos em algum ponto de algum token)?
Todos os caracteres de A a Z são aceitos, assim como números.
Números também são aceitos. 
Pontuação também é aceita ("." e "," entre strings, ":", ";")


### 2. A linguagem é case-sensitive? Palavras reservadas e identificadores seguem a mesma regra de maiúsculas/minúsculas?
Sim, a linguagem é case-sensitive (total e Total são variáveis distintas). 
As palavras reservadas seguem uma regra distinta da seguida pelos identificadores. Os identificadores podem iniciar com letra maiúscula, enquanto as palavras reservadas devem ser todas minúsculas.


### 3. Como são delimitados espaços em branco e comentários (linha e/ou bloco)? Comentários de bloco podem ser aninhados?
Espaços em branco no código não fazem diferença, assim como os comentários. Comentários de bloco devem ser necessariamente aninhados.


### 4. Para operadores com forma composta (ex.: = vs. ==), qual é a regra de desambiguação — "maximal munch": o scanner sempre tenta consumir o prefixo válido mais longo antes de decidir o token.
Sim, o scanner consome os tokens possíveis e logo então decide à respeito do token.


### 5. Quais são exatamente as palavras reservadas da linguagem (lista fechada, não apenas exemplos)?

- Tipos básicos: int, double, bool, char, string, void
- Estruturas de controle: if, else, while
- Procedimentos e funções: function, return

---
