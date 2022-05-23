package ast

import lexer.Token

class ValueNode (val value: Token) : ExpressionNode() {
    override fun toString(): String {
        return "ValueNode(value=$value)"
    }
}
