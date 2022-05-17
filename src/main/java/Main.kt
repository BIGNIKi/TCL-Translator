import lexer.Lexer
import parser.Parser

fun main() {

    setupLexer()
    // debugRegex()
}

fun setupLexer() {
    // todo add parsing of negative numbers
    val code = "while {\$x >= 1 && \$x <= 0} {\n" +
            "set x 4\n" +
            "puts \"x is \$x\"\n" +
            "if {\$x > 4} {break}" +
            "} \n"

    val lexer = Lexer(code)
    val tokenList = lexer.lexAnalysis()
    println(tokenList)

    val parser = Parser(tokenList)
    println(parser.parseCode())
}

fun debugRegex() {
    val inputString = "\u005C"
    val regex = "^\\u005C".toRegex()

    val match = regex.find(inputString)!!
    println(match.value)
    println(match.range)
}
