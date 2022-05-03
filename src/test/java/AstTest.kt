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
    internal fun `puts + grouping with braces test 1`() {
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
    internal fun `puts + grouping with braces test 2`() {
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
    internal fun `puts + grouping with braces test 3`() {
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
    internal fun `puts + grouping with braces test 4`() {
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
    internal fun `puts + grouping with braces test 5`() {
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

    @Test
    internal fun `expr test 1`() {
        val code = "puts [expr 2 + (3 * (4 - 2)) + 1];"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode: \n" +
                "[UnarOperationNode\n" +
                "operator: Token(type=PUTS, text='puts', pos=0)\n" +
                "operand: SquareBracesNodes:\n" +
                "nodes: [UnarOperationNode\n" +
                "operator: Token(type=EXPR, text='expr', pos=6)\n" +
                "operand: MathExpNodes:\n" +
                "nodes: [NumberNode: Token(type=INTEGER, text='2', pos=11)), OperationNode: Token(type=OPERATION, text='+', pos=13)), BracesNodes\n" +
                "nodes: [NumberNode: Token(type=INTEGER, text='3', pos=16)), OperationNode: Token(type=OPERATION, text='*', pos=18)), BracesNodes\n" +
                "nodes: [NumberNode: Token(type=INTEGER, text='4', pos=21)), OperationNode: Token(type=OPERATION, text='-', pos=23)), NumberNode: Token(type=INTEGER, text='2', pos=25))]], OperationNode: Token(type=OPERATION, text='+', pos=29)), NumberNode: Token(type=INTEGER, text='1', pos=31))]]]"

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `expr test 2`() {
        val code = "puts [expr 2 * 4 + 1];"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode: \n" +
                "[UnarOperationNode\n" +
                "operator: Token(type=PUTS, text='puts', pos=0)\n" +
                "operand: SquareBracesNodes:\n" +
                "nodes: [UnarOperationNode\n" +
                "operator: Token(type=EXPR, text='expr', pos=6)\n" +
                "operand: MathExpNodes:\n" +
                "nodes: [NumberNode: Token(type=INTEGER, text='2', pos=11)), OperationNode: Token(type=OPERATION, text='*', pos=13)), NumberNode: Token(type=INTEGER, text='4', pos=15)), OperationNode: Token(type=OPERATION, text='+', pos=17)), NumberNode: Token(type=INTEGER, text='1', pos=19))]]]"

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `expr test 3`() {
        val code = "puts [expr sqrt(9) + 1];"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode: \n" +
                "[UnarOperationNode\n" +
                "operator: Token(type=PUTS, text='puts', pos=0)\n" +
                "operand: SquareBracesNodes:\n" +
                "nodes: [UnarOperationNode\n" +
                "operator: Token(type=EXPR, text='expr', pos=6)\n" +
                "operand: MathExpNodes:\n" +
                "nodes: [MathFunctionNode: funNode: Token(type=SQRT, text='sqrt', pos=11), argument: NumberNode: Token(type=INTEGER, text='9', pos=16)), OperationNode: Token(type=OPERATION, text='+', pos=19)), NumberNode: Token(type=INTEGER, text='1', pos=21))]]]"

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `expr test 4`() {
        val code = "puts [expr \$a + \$b + 2];"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode: \n" +
                "[UnarOperationNode\n" +
                "operator: Token(type=PUTS, text='puts', pos=0)\n" +
                "operand: SquareBracesNodes:\n" +
                "nodes: [UnarOperationNode\n" +
                "operator: Token(type=EXPR, text='expr', pos=6)\n" +
                "operand: MathExpNodes:\n" +
                "nodes: [VariableNode: Token(type=LINK_VARIABLE, text='\$a', pos=11), OperationNode: Token(type=OPERATION, text='+', pos=14)), VariableNode: Token(type=LINK_VARIABLE, text='\$b', pos=16), OperationNode: Token(type=OPERATION, text='+', pos=19)), NumberNode: Token(type=INTEGER, text='2', pos=21))]]]"

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `expr test 5`() {
        val code = "puts [expr \"\$a + \$b + 2\"];"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode: \n" +
                "[UnarOperationNode\n" +
                "operator: Token(type=PUTS, text='puts', pos=0)\n" +
                "operand: SquareBracesNodes:\n" +
                "nodes: [UnarOperationNode\n" +
                "operator: Token(type=EXPR, text='expr', pos=6)\n" +
                "operand: MathExpNodes:\n" +
                "nodes: [VariableNode: Token(type=LINK_VARIABLE, text='\$a', pos=12), OperationNode: Token(type=OPERATION, text='+', pos=15)), VariableNode: Token(type=LINK_VARIABLE, text='\$b', pos=17), OperationNode: Token(type=OPERATION, text='+', pos=20)), NumberNode: Token(type=INTEGER, text='2', pos=22))]]]"

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `expr test 6`() {
        val code = "puts [expr {\$a + \$b + 2}];"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode: \n" +
                "[UnarOperationNode\n" +
                "operator: Token(type=PUTS, text='puts', pos=0)\n" +
                "operand: SquareBracesNodes:\n" +
                "nodes: [UnarOperationNode\n" +
                "operator: Token(type=EXPR, text='expr', pos=6)\n" +
                "operand: MathExpNodes:\n" +
                "nodes: [VariableNode: Token(type=LINK_VARIABLE, text='\$a', pos=12), OperationNode: Token(type=OPERATION, text='+', pos=15)), VariableNode: Token(type=LINK_VARIABLE, text='\$b', pos=17), OperationNode: Token(type=OPERATION, text='+', pos=20)), NumberNode: Token(type=INTEGER, text='2', pos=22))]]]"

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `comment test 1`() {
        val code = "\"# Comment also can be parsed;\""
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode: \n" +
                "[CommentNode: comment: 'Token(type=COMMENT, text='# Comment also can be parsed', pos=0)']"
        Assertions.assertEquals(expected, actual)
    }
}