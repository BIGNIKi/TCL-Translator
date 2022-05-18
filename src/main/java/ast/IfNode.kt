package ast

class IfNode : ExpressionNode() {
    private val branches: MutableList<IfBranch> = mutableListOf()

    fun addBranch(branch: IfBranch) {
        branches.add(branch)
    }

    override fun toString(): String {
        return "IfNode(branches=$branches)"
    }
}

data class IfBranch(
    val condition: ExpressionNode?,
    val body: ExpressionNode
)
