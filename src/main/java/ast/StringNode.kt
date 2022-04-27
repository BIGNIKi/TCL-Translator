package ast

class StringNode: ExpressionNode() {
    var string: String = ""

    fun join(str: String) {
        string += str
    }
}
