import lexer.Lexer
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import parser.Parser

class AstTest {

    @Test
    internal fun `Square braces test 1`() {
        val code = "set a {[set b \"Some string\"]};"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode: \n" +
                "[BinOperationNode:\n" +
                "operator: Token(type=SET, text='set', pos=0)\n" +
                "whomAssign: VariableNode: Token(type=VARIABLE, text='a', pos=4)\n" +
                "whatAssign: CurlyBracesNodes:\n" +
                "nodes: [StringNode: [set b \"Some string\"]]\n" +
                "]"

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `Square braces test 2`() {
        val code = "set a \"[set b {Some string}]\";"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode: \n" +
                "[BinOperationNode:\n" +
                "operator: Token(type=SET, text='set', pos=0)\n" +
                "whomAssign: VariableNode: Token(type=VARIABLE, text='a', pos=4)\n" +
                "whatAssign: QuotationNodes\n" +
                "nodes: [SquareBracesNodes:\n" +
                "nodes: [BinOperationNode:\n" +
                "operator: Token(type=SET, text='set', pos=8)\n" +
                "whomAssign: VariableNode: Token(type=VARIABLE, text='b', pos=12)\n" +
                "whatAssign: CurlyBracesNodes:\n" +
                "nodes: [StringNode: Some string]\n" +
                "]]\n" +
                "]"

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `Square braces test 3`() {
        val code = "set a \"\\[set b {Some string}]\";"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode: \n" +
                "[BinOperationNode:\n" +
                "operator: Token(type=SET, text='set', pos=0)\n" +
                "whomAssign: VariableNode: Token(type=VARIABLE, text='a', pos=4)\n" +
                "whatAssign: QuotationNodes\n" +
                "nodes: [StringNode: [set b {Some string}]]\n" +
                "]"

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `Square braces test 4`() {
        val code = "set a [set b \" 123 \"];"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode: \n" +
                "[BinOperationNode:\n" +
                "operator: Token(type=SET, text='set', pos=0)\n" +
                "whomAssign: VariableNode: Token(type=VARIABLE, text='a', pos=4)\n" +
                "whatAssign: SquareBracesNodes:\n" +
                "nodes: [BinOperationNode:\n" +
                "operator: Token(type=SET, text='set', pos=7)\n" +
                "whomAssign: VariableNode: Token(type=VARIABLE, text='b', pos=11)\n" +
                "whatAssign: QuotationNodes\n" +
                "nodes: [StringNode:  123 ]\n" +
                "]\n" +
                "]"

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `Square braces test 5`() {
        val code = "set a [set b { 123 }];"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode: \n" +
                "[BinOperationNode:\n" +
                "operator: Token(type=SET, text='set', pos=0)\n" +
                "whomAssign: VariableNode: Token(type=VARIABLE, text='a', pos=4)\n" +
                "whatAssign: SquareBracesNodes:\n" +
                "nodes: [BinOperationNode:\n" +
                "operator: Token(type=SET, text='set', pos=7)\n" +
                "whomAssign: VariableNode: Token(type=VARIABLE, text='b', pos=11)\n" +
                "whatAssign: CurlyBracesNodes:\n" +
                "nodes: [StringNode:  123 ]\n" +
                "]\n" +
                "]"

        Assertions.assertEquals(expected, actual)
    }
}