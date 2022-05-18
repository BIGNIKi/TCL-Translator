package ast

import lexer.Token

class MathFunctionNode(val mathFun: Token, val argument: ExpressionNode) : ExpressionNode() {
    override fun toString(): String {
        return "MathFunctionNode(mathFun=$mathFun, argument=$argument)"
    }
}
