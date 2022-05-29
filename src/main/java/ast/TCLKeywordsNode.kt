package ast

import lexer.Token

class TCLKeywordsNode(val keyword: Token): ExpressionNode() {
    override fun toString(): String {
        return "TCLKeywordsNode(keyword=$keyword)"
    }
}
