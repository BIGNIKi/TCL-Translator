package ast

import lexer.Token

class ValueNode (private val value: Token) : ExpressionNode() {
    override fun toString(): String {
        return "ValueNode(value=$value)"
    }
}
