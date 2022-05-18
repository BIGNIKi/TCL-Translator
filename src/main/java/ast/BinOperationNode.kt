package ast

import lexer.Token

class BinOperationNode(
    private val operator: Token,
    private val whomAssign: VariableNode,
    private val whatAssign: ExpressionNode
) : ExpressionNode() {
    override fun toString(): String {
        return "BinOperationNode:\n" +
                "operator: $operator\n" +
                "whomAssign: $whomAssign\n" +
                "whatAssign: $whatAssign\n"
    }
}
