package ast

import lexer.Token

class CommentNode(private val comment: Token) : ExpressionNode() {
    override fun toString(): String {
        return "CommentNode(comment=$comment)"
    }
}
