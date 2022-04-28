import lexer.Lexer
import parser.Parser

fun main() {

    setupLexer()
    // debugRegex()
}

fun setupLexer() {
    // val code = "set text \"No washi on " + '\u005C' + "\$a\";"
    // val code = "set text \"No washi on {\$a}\";"
    val code = "set ab {abab \$a};"

    val lexer = Lexer(code)
    val tokenList = lexer.lexAnalysis()
    println(tokenList)

    val parser = Parser(tokenList)
    parser.parseCode()
}

fun debugRegex() {
    val inputString = "\u005C"
    val regex = "^\\u005C".toRegex()

    val match = regex.find(inputString)!!
    println(match.value)
    println(match.range)
}