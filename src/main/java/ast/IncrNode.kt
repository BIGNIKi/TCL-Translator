package ast

class IncrNode(
    val variable: VariableNode,
    val value: Int
) : ExpressionNode() {
    override fun toString(): String {
        return "IncrNode(variable=$variable, value=$value)"
    }
}
