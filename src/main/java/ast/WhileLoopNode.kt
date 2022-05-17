package ast

class WhileLoopNode(
    private val condition: ExpressionNode,
    private val body: ExpressionNode
): ExpressionNode() {
    override fun toString(): String {
        return "WhileLoopNode(condition=$condition, body=$body)"
    }
}
