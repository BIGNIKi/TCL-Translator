package lexer

class Lexer(private val code: String) {
    private var pos: Int = 0
    private val tokenList: MutableList<Token> = mutableListOf()

    fun lexAnalysis(): List<Token> {
        while (nextToken()) { }
        return removeUnnecessaryTokensFromList()
    }

    /**
     * Удаляет лишние токена Space, кроме случаев, когда мы находимся внутри "...", [...], {...}
     */
    private fun removeUnnecessaryTokensFromList(): List<Token> {
        var isInsideQuote = false
        var quoteCounter = 0
        var isInsideCurlyBraces = false
        var isInsideSquareBraces = false

        val newTokenList: MutableList<Token> = mutableListOf()

        tokenList.forEach { token ->
            when (token.type) {
                TokenType.QUOT -> {
                    quoteCounter++
                    isInsideQuote = quoteCounter % 2 != 0
                }
                TokenType.LCUR -> { isInsideCurlyBraces = true }
                TokenType.RCUR -> { isInsideCurlyBraces = false }
                TokenType.LSQU -> { isInsideSquareBraces = true }
                TokenType.RSQU -> { isInsideSquareBraces = false }
                else -> {}
            }

            // если токен это space и он находится не внутри "...", [...], {...}
            if (token.type == TokenType.SPACE && !isInsideQuote && !isInsideCurlyBraces && !isInsideSquareBraces) {
                return@forEach
            }

            newTokenList.add(token)
        }

        return newTokenList
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