import lexer.Lexer
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import parser.Parser
import translator.Translator
import java.io.File
import java.io.PrintStream
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths

class TranslatorTest {

    @Test
    internal fun `test 1`() {
        val code = Files.readString(Paths.get(TEST_FILE) , StandardCharsets.US_ASCII)
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()

        File(ACTUAL_FILE).delete()
        System.setOut(PrintStream(File(ACTUAL_FILE)));

        val transl = Translator()
        transl.generateClass(asl)
        transl.BuildAndRun()

        val outputActual = Files.readString(Paths.get(ACTUAL_FILE) , StandardCharsets.US_ASCII)
        val outputExpected = Files.readString(Paths.get(EXPECTED_FILE) , StandardCharsets.US_ASCII)

        Assertions.assertEquals(outputExpected, outputActual)
    }

    companion object {
        const val TEST_FILE = "src/main/resources/BigTest.tcl"
        const val ACTUAL_FILE = "src/main/resources/output_file_actual.txt"
        const val EXPECTED_FILE = "src/main/resources/output_file_expected.txt"
    }
}