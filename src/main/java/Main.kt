import lexer.Lexer

fun main() {

    setupLexer()
    // debugRegex()
}

fun setupLexer() {
    val code = "puts \"Hello baby 4\" \n"

    val lexer = Lexer(code)
    val tokenList = lexer.lexAnalysis()
    println(tokenList)
}

fun debugRegex() {
    val inputString = "\"fdsf1 2sAzdadf\""
    val regex = "^[\"][\\w ]*[\"]$".toRegex()

    val match = regex.find(inputString)!!
    println(match.value)
    println(match.range)
}