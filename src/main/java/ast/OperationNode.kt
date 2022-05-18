package ast

import lexer.Token

class OperationNode(val operation: Token) : ExpressionNode() {
    override fun toString(): String {
        return "OperationNode(operation=$operation)"
    }
}
