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
        var quoteCounter = 0

        val newTokenList: MutableList<Token> = mutableListOf()
        val bracesSequence: MutableList<TokenType> = mutableListOf()

        tokenList.forEach { token ->
            when (token.type) {
                TokenType.QUOT -> {
                    quoteCounter++
                    if (quoteCounter % 2 != 0) {
                        bracesSequence.add(TokenType.QUOT)
                    } else {
                        bracesSequence.removeLast()
                    }
                }
                TokenType.LCUR -> { bracesSequence.add(TokenType.LCUR) }
                TokenType.RCUR -> { bracesSequence.removeLast() }
                TokenType.LSQU -> { bracesSequence.add(TokenType.LSQU) }
                TokenType.RSQU -> { bracesSequence.removeLast() }
                else -> {}
            }

            // Убираем space везде, если мы не внутри braces
            if (token.type == TokenType.SPACE && bracesSequence.isEmpty()) {
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
            val resultMatch: MatchResult? = tokenType.regex?.find(code.substring(pos))
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