import lexer.Lexer
import parser.Parser

fun main() {

    setupLexer()
    // debugRegex()
}

fun setupLexer() {
    val code = "set a {[set b \"Some string\"]};"

    val lexer = Lexer(code)
    val tokenList = lexer.lexAnalysis()
    //println(tokenList)

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