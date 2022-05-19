package ast

import lexer.Token

class MathFunctionNode(
    private val mathFun: Token,
    private val argument: ExpressionNode
) : ExpressionNode() {
    override fun toString(): String {
        return "MathFunctionNode(mathFun=$mathFun, argument=$argument)"
    }
}
