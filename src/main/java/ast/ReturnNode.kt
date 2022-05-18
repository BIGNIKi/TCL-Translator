package ast

class ReturnNode(
    private val returnValue: ExpressionNode?
) : ExpressionNode() {
    override fun toString(): String {
        return "ReturnNode(returnValue=$returnValue)"
    }
}
