package lexer

enum class TokenType(val label: String, val regex: Regex?) {
    FLOAT("FLOAT", Regex("^([0-9]*[.,][0-9]*)")),
    INTEGER("INTEGER", Regex("^[0-9]*")),
    STRING("STRING", null),
    // STRING("STRING", Regex("^[ \\w]*")),
    // STRING("STRING", Regex("^[\"][\\w ]*[\"]")),

    COMMENT("COMMENT", Regex("^#[ \\w]*")),

    SYMBOL("SYMBOL", Regex("^[.,_:#]*")),
    CANCEL_SYMBOL("CANCEL_SYMBOL", Regex("^\\u005C")),

    SEMICOLON("SEMICOLON", Regex("^[;\n]")),
    SPACE("SPACE", Regex("^[ \t\r]")),

    OPERATION("OPERATION", Regex("^[-+~!*/%&^|<>=]")),

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
    LOG("LOG", Regex("^log")),

    SWITCH("SWITCH", Regex("^switch")),
    DEFAULT("DEFAULT", Regex("^default")),

    VARIABLE("VARIABLE", Regex("^[\\w]*")),
    LINK_VARIABLE("LINK_VARIABLE", Regex("^(\\$\\w)*")),
}