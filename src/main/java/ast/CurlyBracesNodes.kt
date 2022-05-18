package ast

class CurlyBracesNodes : ExpressionNode() {
    private val nodes: MutableList<ExpressionNode> = mutableListOf()

    fun addNode(node: ExpressionNode) {
        nodes.add(node)
    }

    override fun toString(): String {
        return "CurlyBracesNodes(nodes=$nodes)"
    }

}
