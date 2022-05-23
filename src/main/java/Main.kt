import lexer.Lexer
import parser.Parser
import translator.Translator
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths


fun main() {

    setupLexer()
    // debugRegex()
}

fun setupLexer() {
    val code = Files.readString(Paths.get("src/main/resources/codeExample.tcl") , StandardCharsets.US_ASCII)

    //val code = "puts \$a"

    val lexer = Lexer(code)
    val tokenList = lexer.lexAnalysis()
    println(tokenList)

    val parser = Parser(tokenList) // вернули всю запарсенную штуку

    //println(parser.parseCode())
    val transl = Translator()
    transl.generateClass(parser.parseCode())
    transl.BuildAndRun()
}

fun debugRegex() {
    val inputString = "\u005C"
    val regex = "^\\u005C".toRegex()

    val match = regex.find(inputString)!!
    println(match.value)
    println(match.range)
}
