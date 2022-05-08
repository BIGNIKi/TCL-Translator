package ast

class IfNode : ExpressionNode() {
    private val branches: MutableList<IfBranch> = mutableListOf()
    override fun toString(): String {
        return "IfNode(branches=$branches)"
    }

    fun addBranch(branch: IfBranch) {
        branches.add(branch)
    }
}

data class IfBranch(
    val condition: ExpressionNode?,
    val body: ExpressionNode
)
