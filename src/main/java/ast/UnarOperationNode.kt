package ast

import lexer.Token

class UnarOperationNode(
    val operator: Token,
    val operand: ExpressionNode
) : ExpressionNode()
