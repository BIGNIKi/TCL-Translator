package ast

/**
 * Самый корневой узел дерева, хранит в себе строки кода
 */
class StatementsNode : ExpressionNode() {
    private val codeStrings: MutableList<ExpressionNode> = mutableListOf()

    fun addNode(node: ExpressionNode) {
        codeStrings.add(node)
    }

    override fun toString(): String {
        return "StatementsNode(codeStrings=$codeStrings)"
    }
}
