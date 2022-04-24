package lexer

class Lexer(private val code: String) {
    private var pos: Int = 0
    private val tokenList: MutableList<Token> = mutableListOf()

    fun lexAnalysis(): List<Token> {
        while (nextToken()) { }

        return tokenList.filter { token ->
            token.type.label != TokenType.SPACE.name
        }
    }

    private fun nextToken(): Boolean {
        if (pos >= code.length) {
            return false
        }

        val tokenTypesValues = TokenType.values()
        tokenTypesValues.forEach { tokenType ->
            val resultMatch: MatchResult? = tokenType.regex.find(code.substring(pos))
            if (resultMatch != null && resultMatch.value.isNotEmpty()) {
                val token = Token(tokenType, resultMatch.value, pos)
                pos += token.text.length
                tokenList.add(token)
                return true
            }
        }
        throw Exception("On position $pos detected error")
    }
}