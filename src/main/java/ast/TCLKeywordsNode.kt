package ast

import lexer.Token

class TCLKeywordsNode(private val keyword: Token): ExpressionNode() {
    override fun toString(): String {
        return "TCLKeywordsNode(keyword=$keyword)"
    }
}
