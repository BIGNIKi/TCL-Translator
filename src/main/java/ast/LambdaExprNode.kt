package ast

class LambdaExprNode(
    private val args: List<VariableNode>,
    private val body: ExpressionNode,
    private val exprAsString: CurlyBracesNodes
): ExpressionNode() {
    override fun toString(): String {
        return "LambdaExprNode(args=$args, body=$body, exprAsString=$exprAsString)"
    }
}
