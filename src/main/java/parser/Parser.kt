package parser

import lexer.Token
import lexer.TokenType

class Parser(private val tokens: List<Token>)  {
    var pos: Int = 0
    val scope: HashMap<String, Any> = hashMapOf()


    /**
     * Используется если мы в конце ожидаем; или если у нас есть '(' значит мы ожидаем ')'
     */
    private fun require(tokenType: TokenType): Token {
        val token = match(tokenType)
        token?.let {
            return it
        } ?: throw Exception("on position $pos expected ${tokenType.label}")
    }

    /**
     * По текущей позиции возвращает токен из списка
     */
    private fun match(tokenType: TokenType): Token? {
        if (pos < tokens.size) {
            val currentToken = tokens[pos]
            if (tokenType == currentToken.type) {
                pos += 1
                return currentToken
            }
        }
        return null
    }
}