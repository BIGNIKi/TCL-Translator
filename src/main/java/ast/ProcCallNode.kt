package ast

class ProcCallNode(
    val functionName: StringNode,
    val args: List<ExpressionNode>,
) : ExpressionNode() {
    override fun toString(): String {
        return "ProcCallNode(functionName=$functionName, args=$args)"
    }
}
