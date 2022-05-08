import lexer.Lexer
import parser.Parser

fun main() {

    setupLexer()
    // debugRegex()
}

fun setupLexer() {
    val code = "switch \$x {\n" +
            "  \"\$z\"\t\t{set y1 [expr \$y+1]; puts \"match \$z. \$y + \$z is \$y1\" } \n" +
            "  \"one\"\t{set y1 [expr \$y+1]; puts \"match one \$y plus one is \$y1\"} \n" +
            "  \"default\"\t{puts \"\$x none\"}\n" +
            "}"

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