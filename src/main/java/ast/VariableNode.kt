package ast

import lexer.Token

class VariableNode(val variable: Token) : ExpressionNode()
