package ast

class QuotationNodes : ExpressionNode() {
    val nodes: MutableList<ExpressionNode> = mutableListOf()

    fun addNode(node: ExpressionNode) {
        nodes.add(node)
    }

    override fun toString(): String {
        return "QuotationNodes(nodes=$nodes)"
    }
}
