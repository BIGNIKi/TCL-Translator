package ast

import lexer.Token

class VariableNode(val variable: Token) : ExpressionNode() {
    override fun toString(): String {
        return "VariableNode(variable=$variable)"
    }
}
