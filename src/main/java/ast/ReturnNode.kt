package ast

class ReturnNode(
    val returnValue: ExpressionNode?
) : ExpressionNode() {
    override fun toString(): String {
        return "ReturnNode(returnValue=$returnValue)"
    }
}
