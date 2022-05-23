package ast

import lexer.Token

class UnarOperationNode(
    val operator: Token,
    val operand: ExpressionNode
) : ExpressionNode() {
    override fun toString(): String {
        return "UnarOperationNode(operator=$operator, operand=$operand)"
    }
}
