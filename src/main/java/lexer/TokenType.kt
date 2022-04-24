package lexer

enum class TokenType(val label: String, val regex: Regex) {
    NUMBER("NUMBER", Regex("^[0-9]*")),
    STRING("STRING", Regex("^[\"][\\w ]*[\"]")),

    VARIABLE("VARIABLE", Regex("^[a-zA-Z]*")),

    SEMICOLON("SEMICOLON", Regex("^[;\n]")),
    SPACE("SPACE", Regex("^[ \t\r]")),

    PLUS("PLUS", Regex("^+")),
    MINUS("MINUS", Regex("^-")),

    LPAR("LPAR", Regex("^\\(")),
    RPAR("RPAR", Regex("^\\)")),

    SET("SET", Regex("^set")),
    PUTS("PUTS", Regex("^puts")),
}