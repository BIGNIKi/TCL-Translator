package ast

import lexer.Token

class MathFunctionNode(
    val mathFun: Token,
    val arguments: List<ExpressionNode>
) : ExpressionNode() {
    override fun toString(): String {
        return "MathFunctionNode(mathFun=$mathFun, arguments=$arguments)"
    }
}
