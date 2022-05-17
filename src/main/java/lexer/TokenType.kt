package lexer

enum class TokenType(val label: String, val regex: Regex?) {
    FLOAT("FLOAT", Regex("^([0-9]*[.,][0-9]*)")),
    INTEGER("INTEGER", Regex("^[0-9]*")),
    STRING("STRING", null),

    COMMENT("COMMENT", Regex("^#[ \\w]*")),

    SYMBOL("SYMBOL", Regex("^[.,_:#]*")),
    CANCEL_SYMBOL("CANCEL_SYMBOL", Regex("^\\u005C")),

    SEMICOLON("SEMICOLON", Regex("^[;\n]")),
    SPACE("SPACE", Regex("^[ \t\r]")),

    OPERATION("OPERATION", Regex("^[-+~*/%]")),
    IS_EQUAL("IS_EQUAL", Regex("^==")),
    IS_NOT_EQUAL("IS_NOT_EQUAL", Regex("^!=")),
    AND("AND", Regex("^&&")),
    OR("OR", Regex("^\\|\\|")),
    GREATER_OR_EQUAL("GREATER_OR_EQUAL", Regex("^>=")),
    LESS_OR_EQUAL("LESS_OR_EQUAL", Regex("^<=")),
    GREATER("GREATER", Regex("^>")),
    LESS("LESS", Regex("^<")),
    TRUE("true", Regex("^true")),
    FALSE("false", Regex("^false")),

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

    IF("IF", Regex("^if")),
    ELSEIF("ELSEIF", Regex("^elseif")),
    ELSE("ELSE", Regex("^else")),

    VARIABLE("VARIABLE", Regex("^[\\w]*")),
    LINK_VARIABLE("LINK_VARIABLE", Regex("^[\$][\\w]*")),
}