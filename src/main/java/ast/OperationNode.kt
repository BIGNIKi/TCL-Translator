package ast

import lexer.Token

class OperationNode(private val operation: Token) : ExpressionNode() {
    override fun toString(): String {
        return "OperationNode(operation=$operation)"
    }
}
