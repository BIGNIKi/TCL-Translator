package ast

class SquareBracesNodes : ExpressionNode() {
    private val nodes: MutableList<ExpressionNode> = mutableListOf()

    fun addNode(node: ExpressionNode) {
        nodes.add(node)
    }

    override fun toString(): String {
        return "SquareBracesNodes:\n" +
                "nodes: $nodes"
    }

}