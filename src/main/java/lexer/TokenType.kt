package lexer

enum class TokenType(val label: String, val regex: Regex) {
    FLOAT("FLOAT", Regex("^([0-9]*[.,][0-9]*)")),
    NUMBER("NUMBER", Regex("^[0-9]*")),

    // STRING("STRING", Regex("^[ \\w]*")),
    // STRING("STRING", Regex("^[\"][\\w ]*[\"]")),

    COMMENT("COMMENT", Regex("^#[ \\w]*")),

    SYMBOL("SYMBOL", Regex("^[.,_:#]*")),

    SEMICOLON("SEMICOLON", Regex("^[;\n]")),
    SPACE("SPACE", Regex("^[ \t\r]")),

    OPERATION("OPERATION", Regex("^[-+~!*/%&^|<>=]")),
    // PLUS("PLUS", Regex("^+")),
    // MINUS("MINUS", Regex("^-")),

    LPAR("LPAR", Regex("^\\(")),
    RPAR("RPAR", Regex("^\\)")),
    QUOT("QUOT", Regex("^\"")),
    LCUR("LCUR", Regex("^\\{")),
    RCUR("RCUR", Regex("^}")),
    LSQU("LSQU", Regex("^\\[")),
    RSQU("RSQU", Regex("^]")),

    SET("SET", Regex("^set")),
    PUTS("PUTS", Regex("^puts")),

    EXPR("EXPR", Regex("^expr")),
    SQRT("SQRT", Regex("^sqrt")),

    VARIABLE("VARIABLE", Regex("^[\\w]*")),
    LINK_VARIABLE("LINK_VARIABLE", Regex("^(\\$\\w)*")),
}