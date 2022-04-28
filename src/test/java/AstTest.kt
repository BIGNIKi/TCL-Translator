import lexer.Lexer
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import parser.Parser

class AstTest {

    @Test
    internal fun `set + grouping with braces test 1`() {
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
    internal fun `set + grouping with braces test 2`() {
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
    internal fun `set + grouping with braces test 3`() {
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
    internal fun `set + grouping with braces test 4`() {
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
    internal fun `set + grouping with braces test 5`() {
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

    @Test
    internal fun `set + grouping with braces test 6`() {
        val code = "set a \"Some string\";"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode: \n" +
                "[BinOperationNode:\n" +
                "operator: Token(type=SET, text='set', pos=0)\n" +
                "whomAssign: VariableNode: Token(type=VARIABLE, text='a', pos=4)\n" +
                "whatAssign: QuotationNodes\n" +
                "nodes: [StringNode: Some string]\n" +
                "]"

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `set + grouping with braces test 7`() {
        val code = "set a \"Some string \$a\";"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode: \n" +
                "[BinOperationNode:\n" +
                "operator: Token(type=SET, text='set', pos=0)\n" +
                "whomAssign: VariableNode: Token(type=VARIABLE, text='a', pos=4)\n" +
                "whatAssign: QuotationNodes\n" +
                "nodes: [StringNode: Some string , VariableNode: Token(type=LINK_VARIABLE, text='\$a', pos=19)]\n" +
                "]"

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `set + grouping with braces test 8`() {
        val code = "set a \"Some string " + "\u005C" + "\$a\";"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode: \n" +
                "[BinOperationNode:\n" +
                "operator: Token(type=SET, text='set', pos=0)\n" +
                "whomAssign: VariableNode: Token(type=VARIABLE, text='a', pos=4)\n" +
                "whatAssign: QuotationNodes\n" +
                "nodes: [StringNode: Some string \$a]\n" +
                "]"

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `puts + grouping with braces test 9`() {
        val code = "puts \"Hello world\";"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode: \n" +
                "[UnarOperationNode\n" +
                "operator: Token(type=PUTS, text='puts', pos=0)\n" +
                "operand: QuotationNodes\n" +
                "nodes: [StringNode: Hello world]]"

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `puts + grouping with braces test 10`() {
        val code = "puts {Hello world};"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode: \n" +
                "[UnarOperationNode\n" +
                "operator: Token(type=PUTS, text='puts', pos=0)\n" +
                "operand: CurlyBracesNodes:\n" +
                "nodes: [StringNode: Hello world]]"

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `puts + grouping with braces test 11`() {
        val code = "puts \"Hello world\"; puts \"Hello world\";"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode: \n" +
                "[UnarOperationNode\n" +
                "operator: Token(type=PUTS, text='puts', pos=0)\n" +
                "operand: QuotationNodes\n" +
                "nodes: [StringNode: Hello world], UnarOperationNode\n" +
                "operator: Token(type=PUTS, text='puts', pos=20)\n" +
                "operand: QuotationNodes\n" +
                "nodes: [StringNode: Hello world]]"

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `puts + grouping with braces test 12`() {
        val code = "puts \"\$a \$b\";"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode: \n" +
                "[UnarOperationNode\n" +
                "operator: Token(type=PUTS, text='puts', pos=0)\n" +
                "operand: QuotationNodes\n" +
                "nodes: [VariableNode: Token(type=LINK_VARIABLE, text='\$a', pos=6), StringNode:  , VariableNode: Token(type=LINK_VARIABLE, text='\$b', pos=9)]]"

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `puts + grouping with braces test 13`() {
        val code = "puts \"\$a " + "\u005C" + "\$b\";"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode: \n" +
                "[UnarOperationNode\n" +
                "operator: Token(type=PUTS, text='puts', pos=0)\n" +
                "operand: QuotationNodes\n" +
                "nodes: [VariableNode: Token(type=LINK_VARIABLE, text='\$a', pos=6), StringNode:  \$b]]"

        Assertions.assertEquals(expected, actual)
    }
}