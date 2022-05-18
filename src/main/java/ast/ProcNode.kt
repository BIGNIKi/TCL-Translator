package ast

class ProcNode(
    private val functionName: StringNode,
    private val args: List<VariableNode>,
    private val body: ExpressionNode
): ExpressionNode() {
    override fun toString(): String {
        return "ProcNode(functionName=$functionName, args=$args, body=$body)"
    }
}
