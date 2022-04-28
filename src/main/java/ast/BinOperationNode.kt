package ast

import lexer.Token

class BinOperationNode(
    val operator: Token,
    val whomAssign: ExpressionNode,
    val whatAssign: ExpressionNode
) : ExpressionNode() {
    override fun toString(): String {
        return "BinOperationNode:\n" +
                "operator: $operator\n" +
                "whomAssign: $whomAssign\n" +
                "whatAssign: $whatAssign\n"
    }
}
