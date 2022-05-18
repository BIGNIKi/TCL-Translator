package ast

class StringNode(var string: String = ""): ExpressionNode() {

    fun join(str: String) {
        string += str
    }

    override fun toString(): String {
        return "StringNode(string='$string')"
    }
}
