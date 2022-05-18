package ast

import lexer.Token

class UnarOperationNode(
    private val operator: Token,
    private val operand: ExpressionNode
) : ExpressionNode() {
    override fun toString(): String {
        return "UnarOperationNode\n" +
                "operator: $operator\n" +
                "operand: $operand"
    }
}
