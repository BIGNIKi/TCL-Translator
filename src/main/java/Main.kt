import lexer.Lexer

fun main() {

    setupLexer()
    // debugRegex()
}

fun setupLexer() {
    val code = "# assign variable Y\nset Y 1.24\n\n;# output of X and Y\nputs \$X"

    val lexer = Lexer(code)
    val tokenList = lexer.lexAnalysis()
    println(tokenList)
}

fun debugRegex() {
    val inputString = "asd ? das : 1321"
    val regex = "^([\\w ]*[?][\\w ]*[:][\\w ])*".toRegex()

    val match = regex.find(inputString)!!
    println(match.value)
    println(match.range)
}