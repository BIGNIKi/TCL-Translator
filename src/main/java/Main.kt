import lexer.Lexer
import parser.Parser

fun main() {

    setupLexer()
    // debugRegex()
}

fun setupLexer() {
    val code = "if {\$y == true || (\$x == 2 && \$b != 3)} {set x 2} elseif {\$bool == true} {set x 3} else {set x 5}\n"

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
