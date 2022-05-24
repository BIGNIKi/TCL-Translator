package ast

class MathExpNodes(val nodes: MutableList<ExpressionNode>) : ExpressionNode() {
    override fun toString(): String {
        return "MathExpNodes(nodes=$nodes)"
    }
}
