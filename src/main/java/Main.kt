import lexer.Lexer
import parser.Parser
import translator.TestFile
import translator.Translator

fun main() {

    setupLexer()
    // debugRegex()
}

fun setupLexer() {
    val code = "puts \$a"

    val lexer = Lexer(code)
    val tokenList = lexer.lexAnalysis()
    println(tokenList)

    val parser = Parser(tokenList) // вернули всю запарсенную штуку

    println(parser.parseCode())
    //val transl = Translator()
    //transl.generateClass(parser.parseCode());

    TestFile.Go();
}

fun debugRegex() {
    val inputString = "\u005C"
    val regex = "^\\u005C".toRegex()

    val match = regex.find(inputString)!!
    println(match.value)
    println(match.range)
}
