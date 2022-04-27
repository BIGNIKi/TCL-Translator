package parser

import ast.BinOperationNode
import ast.ExpressionNode
import ast.StatementsNode
import ast.VariableNode
import lexer.Token
import lexer.TokenType

class Parser(private val tokens: List<Token>)  {
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
            match(TokenType.SET) != null -> { parseSetExpr() }
            else -> { throw Exception("Unknown TokenType") }
        }

    }

    private fun parseFormula(): ExpressionNode {
        return ExpressionNode()
    }

    private fun parseSetExpr(): ExpressionNode {
        pos -= 1
        val assignOperator = match(TokenType.SET)!!

        val variableNode = parseVariable()
        val rightFormulaNode = parseFormula()

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