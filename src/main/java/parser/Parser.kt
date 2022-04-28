package parser

import ast.*
import lexer.Token
import lexer.TokenType

class Parser(private val tokens: List<Token>) {
    var pos: Int = 0
    val scope: HashMap<String, Any> = hashMapOf()

    fun parseCode(): ExpressionNode {
        val root = StatementsNode()
        while (pos < tokens.size) {
            val codeString = parseExpression() // отдельно взятая строка кода
            require(listOf(TokenType.SEMICOLON))
            root.addNode(codeString)
        }
        return root
    }

    /**
     * Метод парсит отдельно взятую строчку
     */
    private fun parseExpression(): ExpressionNode {
        return when {
            isCurrentTokenTypeEqualTo(TokenType.SET) -> {
                parseSetExpr()
            }
            else -> {
                throw Exception("Unknown TokenType")
            }
        }

    }

    private fun parseSetOrPutsFormula(): ExpressionNode {
        return when {
            isCurrentTokenTypeEqualTo(TokenType.VARIABLE) -> {
                val variable = match(TokenType.VARIABLE)!!
                VariableNode(variable)
            }
            isCurrentTokenTypeEqualTo(TokenType.QUOT) -> {
                incCurrentPos()
                parseQuotExpression()
            }
            isCurrentTokenTypeEqualTo(TokenType.LSQU) -> {
                incCurrentPos()
                parseSquareBracketsExpression()
            }
            isCurrentTokenTypeEqualTo(TokenType.LCUR) -> {
                incCurrentPos()
                parseCurlyBracesExpression()
            }
            else -> throw Exception("Unknown TokenType")
        }
    }

    /**
     * Grammar
     * Case 1. No replacement is made inside the curly brackets
     */
    private fun parseCurlyBracesExpression(): ExpressionNode {
        val curlyBracesNode = CurlyBracesNodes()
        val stringNode = StringNode()

        while (pos < tokens.size) {
            if (isCurrentTokenTypeEqualTo(TokenType.RCUR)) {
                incCurrentPos()
                if (stringNode.string.isNotEmpty()) {
                    // finish forming string Node
                    curlyBracesNode.addNode(stringNode)
                }
                return curlyBracesNode
            }

            val token = getCurrentToken()
            stringNode.join(token.text)
        }

        throw Exception("Missing closing }")
    }

    private fun parseSquareBracketsExpression(): ExpressionNode {
        return SquareBracketsNodes()
    }

    /**
     * Grammar
     * Case 1: Variable (which corresponds to string variable)
     * Case 2: Space character (we must consider all space related characters while concatenating string)
     * Case 3: Semicolon character (we must consider \n character while concatenating string)
     * Case 4: Cancel symbol
     * Case 5: Link variable
     * Case 6: Internal curly braces expression
     * Case 7: Internal square braces expression
     * Case 8: Right curly brace (in case that we used cancel symbol on left curly brace)
     * Case 9: Right square brace (in case that we used cancel symbol on left square brace)
     */
    private fun parseQuotExpression(): ExpressionNode {
        val quotationsNode = QuotationNodes()
        var stringNode = StringNode()

        while (pos < tokens.size) {
            when {
                isCurrentTokenTypeEqualTo(TokenType.VARIABLE) -> {
                    val string = match(TokenType.VARIABLE)!!
                    stringNode.join(string.text)
                }
                isCurrentTokenTypeEqualTo(TokenType.SPACE) -> {
                    val space = match(TokenType.SPACE)!!
                    stringNode.join(space.text)
                }
                isCurrentTokenTypeEqualTo(TokenType.SEMICOLON) -> {
                    val semicolon = match(TokenType.SEMICOLON)!!
                    stringNode.join(semicolon.text)
                }
                isCurrentTokenTypeEqualTo(TokenType.CANCEL_SYMBOL) -> {
                    incCurrentPos()
                }
                // If /$a then substitution is canceled otherwise we return variable node
                isCurrentTokenTypeEqualTo(TokenType.LINK_VARIABLE) -> {
                    val linkVariable = match(TokenType.LINK_VARIABLE)!!
                    val isCancelSymbolSet = checkIfCancelSymbolSet()
                    if (isCancelSymbolSet) {
                        stringNode.join(linkVariable.text)
                    } else {
                        // finish forming string Node
                        quotationsNode.addNode(stringNode)
                        stringNode = StringNode()

                        quotationsNode.addNode(VariableNode(linkVariable))
                    }
                }
                isCurrentTokenTypeEqualTo(TokenType.LCUR) -> {
                    val leftCurlyBrace = match(TokenType.LCUR)!!
                    stringNode.join(leftCurlyBrace.text)
                }
                isCurrentTokenTypeEqualTo(TokenType.LSQU) -> {
                    incCurrentPos()
                    // finish forming string Node
                    quotationsNode.addNode(stringNode)
                    stringNode = StringNode()

                    val squareBracketsExpression = parseSquareBracketsExpression()
                    quotationsNode.addNode(squareBracketsExpression)
                }
                isCurrentTokenTypeEqualTo(TokenType.RCUR) -> {
                    val rightCurlyBrace = match(TokenType.RCUR)!!
                    stringNode.join(rightCurlyBrace.text)
                }
                isCurrentTokenTypeEqualTo(TokenType.RSQU) -> {
                    val rightSquareBrace = match(TokenType.RSQU)!!
                    stringNode.join(rightSquareBrace.text)
                }
                // if we reach the end of equation, we simply return quotationsNode
                isCurrentTokenTypeEqualTo(TokenType.QUOT) ->  {
                    incCurrentPos()
                    if (stringNode.string.isNotEmpty()) {
                        // finish forming string Node
                        quotationsNode.addNode(stringNode)
                    }
                    return quotationsNode
                }
            }
        }

        throw Exception("Missing closing \"")
    }

    private fun checkIfCancelSymbolSet(): Boolean {
        pos -= 2
        if (pos < 0) return false
        val cancelSymbol = isCurrentTokenTypeEqualTo(TokenType.CANCEL_SYMBOL)
        pos += 2
        return cancelSymbol
    }

    private fun parseSetExpr(): ExpressionNode {
        val assignOperator = match(TokenType.SET)!!

        val variableNode = parseVariable()
        val rightFormulaNode = parseSetOrPutsFormula()

        return BinOperationNode(assignOperator, variableNode, rightFormulaNode)
    }

    private fun parseVariable(): ExpressionNode {
        val variable = match(TokenType.VARIABLE)
        variable?.let {
            return VariableNode(it)
        } ?: throw Exception("The variable was expected at $pos position")
    }

    /**
     * Используется если мы в конце ожидаем; или если у нас есть '(' значит мы ожидаем ')'
     */
    private fun require(tokenTypes: List<TokenType>): Token {
        tokenTypes.forEach { tokenType ->
            val token = match(tokenType)
            token?.let { return it }
        }

        val expectedTokens = tokenTypes.joinToString("or ") { it.label }
        throw Exception("on position $pos expected $expectedTokens")
    }

    /**
     * По текущей позиции возвращает токен из списка и сдвигает на один текущую позицию
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

    /**
     * Сравнивает текущий тип токена и tokenType
     */
    private fun isCurrentTokenTypeEqualTo(tokenType: TokenType): Boolean {
        if (pos < tokens.size) {
            val currentToken = tokens[pos]
            if (tokenType == currentToken.type) {
                return true
            }
        }
        return false
    }

    /**
     * Получить текущий токен по текущей позиции и увеличить позицию на 1
     */
    private fun getCurrentToken(): Token {
        return tokens[pos++]
    }

    /**
     * Сдвигает текущую позицию в списке токенов на один
     */
    private fun incCurrentPos() {
        pos++
    }
}