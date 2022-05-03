package ast

class BracesNodes : ExpressionNode() {
    val nodes: MutableList<ExpressionNode> = mutableListOf()

    fun addNode(node: ExpressionNode) {
        nodes.add(node)
    }

    override fun toString(): String {
        return "BracesNodes\n" +
                "nodes: $nodes"
    }
}
