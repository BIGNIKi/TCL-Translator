package ast

class ForLoopNode: ExpressionNode() {
    val initBlock: MutableList<ExpressionNode> = mutableListOf()
    val conditionsBlock: MutableList<ExpressionNode> = mutableListOf()
    val counterBlock: MutableList<ExpressionNode> = mutableListOf()
    val commandBlock: MutableList<ExpressionNode> = mutableListOf()

    fun addExpressionToInitBlock(node: ExpressionNode) {
        initBlock.add(node)
    }

    fun addExpressionToConditionBlock(node: ExpressionNode) {
        conditionsBlock.add(node)
    }

    fun addExpressionToCounterBlock(node: ExpressionNode) {
        counterBlock.add(node)
    }

    fun addExpressionToCommandBlock(node: ExpressionNode) {
        commandBlock.add(node)
    }

    override fun toString(): String {
        return "ForLoopNode(initBlock=$initBlock, conditionsBlock=$conditionsBlock, counterBlock=$counterBlock, commandBlock=$commandBlock)"
    }
}
