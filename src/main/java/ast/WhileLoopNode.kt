package ast

class WhileLoopNode(
    val condition: ExpressionNode,
    val body: ExpressionNode
): ExpressionNode() {
    override fun toString(): String {
        return "WhileLoopNode(condition=$condition, body=$body)"
    }
}
