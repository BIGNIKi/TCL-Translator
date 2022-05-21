package ast

class ApplyNode(): ExpressionNode() {
    var lambdaExpr: ExpressionNode? = null
    val args: MutableList<ExpressionNode> = mutableListOf()

    override fun toString(): String {
        return "ApplyNode(lambdaExpr=$lambdaExpr, args=$args)"
    }
}
