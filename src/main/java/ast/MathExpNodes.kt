package ast

class MathExpNodes : ExpressionNode() {
    val nodes: MutableList<ExpressionNode> = mutableListOf()

    fun addNode(node: ExpressionNode) {
        nodes.add(node)
    }

    override fun toString(): String {
        return "MathExpNodes:\n" +
                "nodes: $nodes"
    }
}
