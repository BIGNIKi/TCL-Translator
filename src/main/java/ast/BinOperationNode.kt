package ast

import lexer.Token

class BinOperationNode(
    private val operator: Token,
    private val whomAssign: VariableNode,
    private val whatAssign: ExpressionNode
) : ExpressionNode() {
    override fun toString(): String {
        return "BinOperationNode(operator=$operator, whomAssign=$whomAssign, whatAssign=$whatAssign)"
    }
}
