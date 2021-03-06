package ast

class SquareBracesNodes : ExpressionNode() {
    val nodes: MutableList<ExpressionNode> = mutableListOf()

    fun addNode(node: ExpressionNode) {
        nodes.add(node)
    }

    override fun toString(): String {
        return "SquareBracesNodes(nodes=$nodes)"
    }
}
