package ast

class QuotationNodes : ExpressionNode() {
    private val nodes: MutableList<ExpressionNode> = mutableListOf()

    fun addNode(node: ExpressionNode) {
        nodes.add(node)
    }

    override fun toString(): String {
        return "QuotationNodes\n" +
                "nodes: $nodes"
    }


}