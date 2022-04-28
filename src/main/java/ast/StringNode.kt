package ast

class StringNode: ExpressionNode() {
    var string: String = ""

    fun join(str: String) {
        string += str
    }

    override fun toString(): String {
        return "StringNode: $string"
    }


}
