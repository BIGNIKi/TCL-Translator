package ast

import lexer.Token

class SwitchNode(private val string: Token, private val cases: List<SwitchCase>) : ExpressionNode() {
    override fun toString(): String {
        return "SwitchNode: string: $string, cases: $cases"
    }
}

data class SwitchCase(
    val value: Token,
    val body: ExpressionNode
)
