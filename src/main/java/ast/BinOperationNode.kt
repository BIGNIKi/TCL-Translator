package ast

import lexer.Token

class BinOperationNode(
    val operator: Token,
    val whomAssign: ExpressionNode,
    val whatAssign: ExpressionNode
) : ExpressionNode()
