package ast

class CurlyBracketsNodes : ExpressionNode() {
    private val nodes: MutableList<ExpressionNode> = mutableListOf()

    fun addNode(node: ExpressionNode) {
        nodes.add(node)
    }
}
