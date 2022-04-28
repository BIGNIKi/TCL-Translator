package ast

import lexer.Token

class NumberNode (val number: Token) : ExpressionNode() {
    override fun toString(): String {
        return "NumberNode: $number)"
    }
}
