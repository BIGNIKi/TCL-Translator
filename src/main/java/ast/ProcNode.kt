package ast

class ProcNode(
    val functionName: StringNode,
    val args: List<VariableNode>,
    val body: ExpressionNode
): ExpressionNode() {
    override fun toString(): String {
        return "ProcNode(functionName=$functionName, args=$args, body=$body)"
    }
}
