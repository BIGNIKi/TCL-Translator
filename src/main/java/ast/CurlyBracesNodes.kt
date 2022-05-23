package ast

class CurlyBracesNodes : ExpressionNode() {
    val nodes: MutableList<ExpressionNode> = mutableListOf()

    fun addNode(node: ExpressionNode) {
        nodes.add(node)
    }

    fun getFirstNode(): ExpressionNode {
        return nodes.first()
    }

    override fun toString(): String {
        return "CurlyBracesNodes(nodes=$nodes)"
    }

}
