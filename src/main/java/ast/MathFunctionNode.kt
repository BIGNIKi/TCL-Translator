package ast

import lexer.Token

class MathFunctionNode(
    private val mathFun: Token,
    private val arguments: List<ExpressionNode>
) : ExpressionNode() {
    override fun toString(): String {
        return "MathFunctionNode(mathFun=$mathFun, arguments=$arguments)"
    }
}
