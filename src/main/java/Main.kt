import lexer.Lexer
import parser.Parser

fun main() {

    setupLexer()
    // debugRegex()
}

fun setupLexer() {
    val code = "set milk 123;"

    val lexer = Lexer(code)
    val tokenList = lexer.lexAnalysis()
    println(tokenList)

    val parser = Parser(tokenList)
    parser.parseCode()
}

fun debugRegex() {
    val inputString = "asd ? das : 1321"
    val regex = "^([\\w ]*[?][\\w ]*[:][\\w ])*".toRegex()

    val match = regex.find(inputString)!!
    println(match.value)
    println(match.range)
}