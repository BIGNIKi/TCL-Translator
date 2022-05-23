package ast

import lexer.Token

class BinOperationNode(
    val operator: Token,
    val whomAssign: VariableNode,
    val whatAssign: ExpressionNode
) : ExpressionNode() {
    override fun toString(): String {
        return "BinOperationNode(operator=$operator, whomAssign=$whomAssign, whatAssign=$whatAssign)"
    }
}
