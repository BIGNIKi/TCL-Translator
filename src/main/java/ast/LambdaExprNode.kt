package ast

class LambdaExprNode(
    val args: List<VariableNode>,
    val body: ExpressionNode
): ExpressionNode() {
    private var exprAsString: StringNode? = null

    fun addStringRepresentation(strNode: StringNode) {
        exprAsString = strNode
    }

    override fun toString(): String {
        return "LambdaExprNode(args=$args, body=$body, exprAsString=$exprAsString)"
    }
}
