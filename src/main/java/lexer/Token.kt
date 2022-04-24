package lexer

data class Token(
    val type: TokenType,
    val text: String,
    val pos: Int
) {
    override fun toString(): String {
        return "Token(type=$type, text='$text', pos=$pos)\n"
    }
}