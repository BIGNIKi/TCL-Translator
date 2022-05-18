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
    internal fun `set test 9`() {
        val code = "set x 3\n"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode: \n" +
                "[BinOperationNode:\n" +
                "operator: Token(type=SET, text='set', pos=0)\n" +
                "whomAssign: VariableNode: Token(type=VARIABLE, text='x', pos=4)\n" +
                "whatAssign: ValueNode: Token(type=INTEGER, text='3', pos=6))\n" +
                "]"

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `set test 10`() {
        val code = "set x 3.333\n"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode: \n" +
                "[BinOperationNode:\n" +
                "operator: Token(type=SET, text='set', pos=0)\n" +
                "whomAssign: VariableNode: Token(type=VARIABLE, text='x', pos=4)\n" +
                "whatAssign: ValueNode: Token(type=FLOAT, text='3.333', pos=6))\n" +
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
                "nodes: [ValueNode: Token(type=INTEGER, text='2', pos=11)), OperationNode: Token(type=OPERATION, text='+', pos=13)), BracesNodes\n" +
                "nodes: [ValueNode: Token(type=INTEGER, text='3', pos=16)), OperationNode: Token(type=OPERATION, text='*', pos=18)), BracesNodes\n" +
                "nodes: [ValueNode: Token(type=INTEGER, text='4', pos=21)), OperationNode: Token(type=OPERATION, text='-', pos=23)), ValueNode: Token(type=INTEGER, text='2', pos=25))]], OperationNode: Token(type=OPERATION, text='+', pos=29)), ValueNode: Token(type=INTEGER, text='1', pos=31))]]]"

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
                "nodes: [ValueNode: Token(type=INTEGER, text='2', pos=11)), OperationNode: Token(type=OPERATION, text='*', pos=13)), ValueNode: Token(type=INTEGER, text='4', pos=15)), OperationNode: Token(type=OPERATION, text='+', pos=17)), ValueNode: Token(type=INTEGER, text='1', pos=19))]]]"

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
                "nodes: [MathFunctionNode: funNode: Token(type=SQRT, text='sqrt', pos=11), argument: ValueNode: Token(type=INTEGER, text='9', pos=16)), OperationNode: Token(type=OPERATION, text='+', pos=19)), ValueNode: Token(type=INTEGER, text='1', pos=21))]]]"

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
                "nodes: [VariableNode: Token(type=LINK_VARIABLE, text='\$a', pos=11), OperationNode: Token(type=OPERATION, text='+', pos=14)), VariableNode: Token(type=LINK_VARIABLE, text='\$b', pos=16), OperationNode: Token(type=OPERATION, text='+', pos=19)), ValueNode: Token(type=INTEGER, text='2', pos=21))]]]"

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
                "nodes: [VariableNode: Token(type=LINK_VARIABLE, text='\$a', pos=12), OperationNode: Token(type=OPERATION, text='+', pos=15)), VariableNode: Token(type=LINK_VARIABLE, text='\$b', pos=17), OperationNode: Token(type=OPERATION, text='+', pos=20)), ValueNode: Token(type=INTEGER, text='2', pos=22))]]]"

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
                "nodes: [VariableNode: Token(type=LINK_VARIABLE, text='\$a', pos=12), OperationNode: Token(type=OPERATION, text='+', pos=15)), VariableNode: Token(type=LINK_VARIABLE, text='\$b', pos=17), OperationNode: Token(type=OPERATION, text='+', pos=20)), ValueNode: Token(type=INTEGER, text='2', pos=22))]]]"

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `comment test 1`() {
        val code = "# Comment also can be parsed;"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode: \n" +
                "[CommentNode: comment: 'Token(type=COMMENT, text='# Comment also can be parsed', pos=0)']"

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `switch case test 1`() {
        val code = "switch \$x \"one\" \"puts one\" \"two\" \"puts two\" \"default\" \"puts none\";"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode: \n" +
                "[SwitchNode: string: Token(type=LINK_VARIABLE, text='\$x', pos=7), cases: [SwitchCase(value=Token(type=STRING, text='one', pos=11), body=CurlyBracesNodes:\n" +
                "nodes: [UnarOperationNode\n" +
                "operator: Token(type=PUTS, text='puts', pos=17)\n" +
                "operand: VariableNode: Token(type=VARIABLE, text='one', pos=22)]), SwitchCase(value=Token(type=STRING, text='two', pos=28), body=CurlyBracesNodes:\n" +
                "nodes: [UnarOperationNode\n" +
                "operator: Token(type=PUTS, text='puts', pos=34)\n" +
                "operand: VariableNode: Token(type=VARIABLE, text='two', pos=39)]), SwitchCase(value=Token(type=DEFAULT, text='default', pos=45), body=CurlyBracesNodes:\n" +
                "nodes: [UnarOperationNode\n" +
                "operator: Token(type=PUTS, text='puts', pos=55)\n" +
                "operand: VariableNode: Token(type=VARIABLE, text='none', pos=60)])], isSubstitutionsAllowed: false]"

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `switch case test 2`() {
        val code = "switch \$x \n" +
                "  \"one\" \t\"puts one\"  \n" +
                "  \"two\" \t\"puts two\" \n" +
                "  \"default\" \t\"puts none\";\n"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode: \n" +
                "[SwitchNode: string: Token(type=LINK_VARIABLE, text='\$x', pos=7), cases: [SwitchCase(value=Token(type=STRING, text='one', pos=14), body=CurlyBracesNodes:\n" +
                "nodes: [UnarOperationNode\n" +
                "operator: Token(type=PUTS, text='puts', pos=21)\n" +
                "operand: VariableNode: Token(type=VARIABLE, text='one', pos=26)]), SwitchCase(value=Token(type=STRING, text='two', pos=36), body=CurlyBracesNodes:\n" +
                "nodes: [UnarOperationNode\n" +
                "operator: Token(type=PUTS, text='puts', pos=43)\n" +
                "operand: VariableNode: Token(type=VARIABLE, text='two', pos=48)]), SwitchCase(value=Token(type=DEFAULT, text='default', pos=57), body=CurlyBracesNodes:\n" +
                "nodes: [UnarOperationNode\n" +
                "operator: Token(type=PUTS, text='puts', pos=68)\n" +
                "operand: VariableNode: Token(type=VARIABLE, text='none', pos=73)])], isSubstitutionsAllowed: false]"

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `switch case test 3`() {
        val code = "switch \$x" + "\u005C" + "\n" +
                "  \"\$z\"\t\t{set y1 [expr \$y+1]; puts \"match \$z. \$y + \$z is \$y1\" } \n" +
                "  \"one\"\t{set y1 [expr \$y+1]; puts \"match one \$y plus one is \$y1\"} \n" +
                "  \"default\"\t{puts \"\$x none\"}\n"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode: \n" +
                "[SwitchNode: string: Token(type=LINK_VARIABLE, text='\$x', pos=7), cases: [SwitchCase(value=Token(type=LINK_VARIABLE, text='\$z', pos=14), body=CurlyBracesNodes:\n" +
                "nodes: [BinOperationNode:\n" +
                "operator: Token(type=SET, text='set', pos=20)\n" +
                "whomAssign: VariableNode: Token(type=VARIABLE, text='y1', pos=24)\n" +
                "whatAssign: SquareBracesNodes:\n" +
                "nodes: [UnarOperationNode\n" +
                "operator: Token(type=EXPR, text='expr', pos=28)\n" +
                "operand: MathExpNodes:\n" +
                "nodes: [VariableNode: Token(type=LINK_VARIABLE, text='\$y', pos=33), OperationNode: Token(type=OPERATION, text='+', pos=35)), ValueNode: Token(type=INTEGER, text='1', pos=36))]]\n" +
                ", UnarOperationNode\n" +
                "operator: Token(type=PUTS, text='puts', pos=40)\n" +
                "operand: QuotationNodes\n" +
                "nodes: [StringNode: match , VariableNode: Token(type=LINK_VARIABLE, text='\$z', pos=52), StringNode: . , VariableNode: Token(type=LINK_VARIABLE, text='\$y', pos=56), StringNode:  + , VariableNode: Token(type=LINK_VARIABLE, text='\$z', pos=61), StringNode:  is , VariableNode: Token(type=LINK_VARIABLE, text='\$y1', pos=67)]]), SwitchCase(value=Token(type=STRING, text='one', pos=78), body=CurlyBracesNodes:\n" +
                "nodes: [BinOperationNode:\n" +
                "operator: Token(type=SET, text='set', pos=84)\n" +
                "whomAssign: VariableNode: Token(type=VARIABLE, text='y1', pos=88)\n" +
                "whatAssign: SquareBracesNodes:\n" +
                "nodes: [UnarOperationNode\n" +
                "operator: Token(type=EXPR, text='expr', pos=92)\n" +
                "operand: MathExpNodes:\n" +
                "nodes: [VariableNode: Token(type=LINK_VARIABLE, text='\$y', pos=97), OperationNode: Token(type=OPERATION, text='+', pos=99)), ValueNode: Token(type=INTEGER, text='1', pos=100))]]\n" +
                ", UnarOperationNode\n" +
                "operator: Token(type=PUTS, text='puts', pos=104)\n" +
                "operand: QuotationNodes\n" +
                "nodes: [StringNode: match one , VariableNode: Token(type=LINK_VARIABLE, text='\$y', pos=120), StringNode:  plus one is , VariableNode: Token(type=LINK_VARIABLE, text='\$y1', pos=135)]]), SwitchCase(value=Token(type=DEFAULT, text='default', pos=145), body=CurlyBracesNodes:\n" +
                "nodes: [UnarOperationNode\n" +
                "operator: Token(type=PUTS, text='puts', pos=155)\n" +
                "operand: QuotationNodes\n" +
                "nodes: [VariableNode: Token(type=LINK_VARIABLE, text='\$x', pos=161), StringNode:  none]])], isSubstitutionsAllowed: true]"

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `switch case test 4`() {
        val code = "switch \$x {\n" +
                "  \"\$z\"\t\t{set y1 [expr \$y+1]; puts \"match \$z. \$y + \$z is \$y1\" } \n" +
                "  \"one\"\t{set y1 [expr \$y+1]; puts \"match one \$y plus one is \$y1\"} \n" +
                "  \"default\"\t{puts \"\$x none\"}\n" +
                "}"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode: \n" +
                "[SwitchNode: string: Token(type=LINK_VARIABLE, text='\$x', pos=7), cases: [SwitchCase(value=Token(type=LINK_VARIABLE, text='\$z', pos=15), body=CurlyBracesNodes:\n" +
                "nodes: [BinOperationNode:\n" +
                "operator: Token(type=SET, text='set', pos=21)\n" +
                "whomAssign: VariableNode: Token(type=VARIABLE, text='y1', pos=25)\n" +
                "whatAssign: SquareBracesNodes:\n" +
                "nodes: [UnarOperationNode\n" +
                "operator: Token(type=EXPR, text='expr', pos=29)\n" +
                "operand: MathExpNodes:\n" +
                "nodes: [VariableNode: Token(type=LINK_VARIABLE, text='\$y', pos=34), OperationNode: Token(type=OPERATION, text='+', pos=36)), ValueNode: Token(type=INTEGER, text='1', pos=37))]]\n" +
                ", UnarOperationNode\n" +
                "operator: Token(type=PUTS, text='puts', pos=41)\n" +
                "operand: QuotationNodes\n" +
                "nodes: [StringNode: match , VariableNode: Token(type=LINK_VARIABLE, text='\$z', pos=53), StringNode: . , VariableNode: Token(type=LINK_VARIABLE, text='\$y', pos=57), StringNode:  + , VariableNode: Token(type=LINK_VARIABLE, text='\$z', pos=62), StringNode:  is , VariableNode: Token(type=LINK_VARIABLE, text='\$y1', pos=68)]]), SwitchCase(value=Token(type=STRING, text='one', pos=79), body=CurlyBracesNodes:\n" +
                "nodes: [BinOperationNode:\n" +
                "operator: Token(type=SET, text='set', pos=85)\n" +
                "whomAssign: VariableNode: Token(type=VARIABLE, text='y1', pos=89)\n" +
                "whatAssign: SquareBracesNodes:\n" +
                "nodes: [UnarOperationNode\n" +
                "operator: Token(type=EXPR, text='expr', pos=93)\n" +
                "operand: MathExpNodes:\n" +
                "nodes: [VariableNode: Token(type=LINK_VARIABLE, text='\$y', pos=98), OperationNode: Token(type=OPERATION, text='+', pos=100)), ValueNode: Token(type=INTEGER, text='1', pos=101))]]\n" +
                ", UnarOperationNode\n" +
                "operator: Token(type=PUTS, text='puts', pos=105)\n" +
                "operand: QuotationNodes\n" +
                "nodes: [StringNode: match one , VariableNode: Token(type=LINK_VARIABLE, text='\$y', pos=121), StringNode:  plus one is , VariableNode: Token(type=LINK_VARIABLE, text='\$y1', pos=136)]]), SwitchCase(value=Token(type=DEFAULT, text='default', pos=146), body=CurlyBracesNodes:\n" +
                "nodes: [UnarOperationNode\n" +
                "operator: Token(type=PUTS, text='puts', pos=156)\n" +
                "operand: QuotationNodes\n" +
                "nodes: [VariableNode: Token(type=LINK_VARIABLE, text='\$x', pos=162), StringNode:  none]])], isSubstitutionsAllowed: false]"

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `if test 1`() {
        val code = "if {\$x == 2} {puts \"\$x is 2\"} elseif {\$x == 3} {puts \"\$x is 3\"} else {puts \"\$x is none\"}\n"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode: \n" +
                "[IfNode(branches=[IfBranch(condition=BracesNodes\n" +
                "nodes: [VariableNode: Token(type=LINK_VARIABLE, text='\$x', pos=4), OperationNode: Token(type=IS_EQUAL, text='==', pos=7)), ValueNode: Token(type=INTEGER, text='2', pos=10))], body=CurlyBracesNodes:\n" +
                "nodes: [UnarOperationNode\n" +
                "operator: Token(type=PUTS, text='puts', pos=14)\n" +
                "operand: QuotationNodes\n" +
                "nodes: [VariableNode: Token(type=LINK_VARIABLE, text='\$x', pos=20), StringNode:  is 2]]), IfBranch(condition=BracesNodes\n" +
                "nodes: [VariableNode: Token(type=LINK_VARIABLE, text='\$x', pos=38), OperationNode: Token(type=IS_EQUAL, text='==', pos=41)), ValueNode: Token(type=INTEGER, text='3', pos=44))], body=CurlyBracesNodes:\n" +
                "nodes: [UnarOperationNode\n" +
                "operator: Token(type=PUTS, text='puts', pos=48)\n" +
                "operand: QuotationNodes\n" +
                "nodes: [VariableNode: Token(type=LINK_VARIABLE, text='\$x', pos=54), StringNode:  is 3]]), IfBranch(condition=null, body=CurlyBracesNodes:\n" +
                "nodes: [UnarOperationNode\n" +
                "operator: Token(type=PUTS, text='puts', pos=70)\n" +
                "operand: QuotationNodes\n" +
                "nodes: [VariableNode: Token(type=LINK_VARIABLE, text='\$x', pos=76), StringNode:  is none]])])]"

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `if test 2`() {
        val code = "if {\$x == 2} {puts \"\$x is 2\"} else {puts \"\$x is none\"}\n"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode: \n" +
                "[IfNode(branches=[IfBranch(condition=BracesNodes\n" +
                "nodes: [VariableNode: Token(type=LINK_VARIABLE, text='\$x', pos=4), OperationNode: Token(type=IS_EQUAL, text='==', pos=7)), ValueNode: Token(type=INTEGER, text='2', pos=10))], body=CurlyBracesNodes:\n" +
                "nodes: [UnarOperationNode\n" +
                "operator: Token(type=PUTS, text='puts', pos=14)\n" +
                "operand: QuotationNodes\n" +
                "nodes: [VariableNode: Token(type=LINK_VARIABLE, text='\$x', pos=20), StringNode:  is 2]]), IfBranch(condition=null, body=CurlyBracesNodes:\n" +
                "nodes: [UnarOperationNode\n" +
                "operator: Token(type=PUTS, text='puts', pos=36)\n" +
                "operand: QuotationNodes\n" +
                "nodes: [VariableNode: Token(type=LINK_VARIABLE, text='\$x', pos=42), StringNode:  is none]])])]"

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `if test 3`() {
        val code = "if {\$x == 2} {puts \"\$x is 2\"} \n"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode: \n" +
                "[IfNode(branches=[IfBranch(condition=BracesNodes\n" +
                "nodes: [VariableNode: Token(type=LINK_VARIABLE, text='\$x', pos=4), OperationNode: Token(type=IS_EQUAL, text='==', pos=7)), ValueNode: Token(type=INTEGER, text='2', pos=10))], body=CurlyBracesNodes:\n" +
                "nodes: [UnarOperationNode\n" +
                "operator: Token(type=PUTS, text='puts', pos=14)\n" +
                "operand: QuotationNodes\n" +
                "nodes: [VariableNode: Token(type=LINK_VARIABLE, text='\$x', pos=20), StringNode:  is 2]])])]"

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `if test 4`() {
        val code = "if { \$a == 10 } {\n" +
                "   # if condition is true then print the following \n" +
                "   puts \"Value of a is 10\"\n" +
                "} elseif { \$a == 20 } {\n" +
                "   # if else if condition is true \n" +
                "   puts \"Value of a is 20\"\n" +
                "} elseif { \$a == 30 } {\n" +
                "   # if else if condition is true \n" +
                "   puts \"Value of a is 30\"\n" +
                "} else {\n" +
                "   # if none of the conditions is true \n" +
                "   puts \"None of the values is matching\"\n" +
                "}\n"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode: \n" +
                "[IfNode(branches=[IfBranch(condition=BracesNodes\n" +
                "nodes: [VariableNode: Token(type=LINK_VARIABLE, text='\$a', pos=5), OperationNode: Token(type=IS_EQUAL, text='==', pos=8)), ValueNode: Token(type=INTEGER, text='10', pos=11))], body=CurlyBracesNodes:\n" +
                "nodes: [CommentNode: comment: 'Token(type=COMMENT, text='# if condition is true then print the following ', pos=21)', UnarOperationNode\n" +
                "operator: Token(type=PUTS, text='puts', pos=73)\n" +
                "operand: QuotationNodes\n" +
                "nodes: [StringNode: Value of a is 10]]), IfBranch(condition=BracesNodes\n" +
                "nodes: [VariableNode: Token(type=LINK_VARIABLE, text='\$a', pos=108), OperationNode: Token(type=IS_EQUAL, text='==', pos=111)), ValueNode: Token(type=INTEGER, text='20', pos=114))], body=CurlyBracesNodes:\n" +
                "nodes: [CommentNode: comment: 'Token(type=COMMENT, text='# if else if condition is true ', pos=124)', UnarOperationNode\n" +
                "operator: Token(type=PUTS, text='puts', pos=159)\n" +
                "operand: QuotationNodes\n" +
                "nodes: [StringNode: Value of a is 20]]), IfBranch(condition=BracesNodes\n" +
                "nodes: [VariableNode: Token(type=LINK_VARIABLE, text='\$a', pos=194), OperationNode: Token(type=IS_EQUAL, text='==', pos=197)), ValueNode: Token(type=INTEGER, text='30', pos=200))], body=CurlyBracesNodes:\n" +
                "nodes: [CommentNode: comment: 'Token(type=COMMENT, text='# if else if condition is true ', pos=210)', UnarOperationNode\n" +
                "operator: Token(type=PUTS, text='puts', pos=245)\n" +
                "operand: QuotationNodes\n" +
                "nodes: [StringNode: Value of a is 30]]), IfBranch(condition=null, body=CurlyBracesNodes:\n" +
                "nodes: [CommentNode: comment: 'Token(type=COMMENT, text='# if none of the conditions is true ', pos=281)', UnarOperationNode\n" +
                "operator: Token(type=PUTS, text='puts', pos=321)\n" +
                "operand: QuotationNodes\n" +
                "nodes: [StringNode: None of the values is matching]])])]"

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `if test 5`() {
        val code = "if {\$x == 2 || \$x == 3} {puts \"\$x is 2\"} else {puts \"\$x is none\"}\n"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode: \n" +
                "[IfNode(branches=[IfBranch(condition=BracesNodes\n" +
                "nodes: [VariableNode: Token(type=LINK_VARIABLE, text='\$x', pos=4), OperationNode: Token(type=IS_EQUAL, text='==', pos=7)), ValueNode: Token(type=INTEGER, text='2', pos=10)), OperationNode: Token(type=OR, text='||', pos=12)), VariableNode: Token(type=LINK_VARIABLE, text='\$x', pos=15), OperationNode: Token(type=IS_EQUAL, text='==', pos=18)), ValueNode: Token(type=INTEGER, text='3', pos=21))], body=CurlyBracesNodes:\n" +
                "nodes: [UnarOperationNode\n" +
                "operator: Token(type=PUTS, text='puts', pos=25)\n" +
                "operand: QuotationNodes\n" +
                "nodes: [VariableNode: Token(type=LINK_VARIABLE, text='\$x', pos=31), StringNode:  is 2]]), IfBranch(condition=null, body=CurlyBracesNodes:\n" +
                "nodes: [UnarOperationNode\n" +
                "operator: Token(type=PUTS, text='puts', pos=47)\n" +
                "operand: QuotationNodes\n" +
                "nodes: [VariableNode: Token(type=LINK_VARIABLE, text='\$x', pos=53), StringNode:  is none]])])]"

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `if test 6`() {
        val code = "if {\$y == true || (\$x == 2 && \$b != 3)} {set x 2} elseif {\$bool == true} {set x 3} else {set x 5}\n"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode: \n" +
                "[IfNode(branches=[IfBranch(condition=BracesNodes\n" +
                "nodes: [VariableNode: Token(type=LINK_VARIABLE, text='\$y', pos=4), OperationNode: Token(type=IS_EQUAL, text='==', pos=7)), ValueNode: Token(type=TRUE, text='true', pos=10)), OperationNode: Token(type=OR, text='||', pos=15)), BracesNodes\n" +
                "nodes: [VariableNode: Token(type=LINK_VARIABLE, text='\$x', pos=19), OperationNode: Token(type=IS_EQUAL, text='==', pos=22)), ValueNode: Token(type=INTEGER, text='2', pos=25)), OperationNode: Token(type=AND, text='&&', pos=27)), VariableNode: Token(type=LINK_VARIABLE, text='\$b', pos=30), OperationNode: Token(type=IS_NOT_EQUAL, text='!=', pos=33)), ValueNode: Token(type=INTEGER, text='3', pos=36))]], body=CurlyBracesNodes:\n" +
                "nodes: [BinOperationNode:\n" +
                "operator: Token(type=SET, text='set', pos=41)\n" +
                "whomAssign: VariableNode: Token(type=VARIABLE, text='x', pos=45)\n" +
                "whatAssign: ValueNode: Token(type=INTEGER, text='2', pos=47))\n" +
                "]), IfBranch(condition=BracesNodes\n" +
                "nodes: [VariableNode: Token(type=LINK_VARIABLE, text='\$bool', pos=58), OperationNode: Token(type=IS_EQUAL, text='==', pos=64)), ValueNode: Token(type=TRUE, text='true', pos=67))], body=CurlyBracesNodes:\n" +
                "nodes: [BinOperationNode:\n" +
                "operator: Token(type=SET, text='set', pos=74)\n" +
                "whomAssign: VariableNode: Token(type=VARIABLE, text='x', pos=78)\n" +
                "whatAssign: ValueNode: Token(type=INTEGER, text='3', pos=80))\n" +
                "]), IfBranch(condition=null, body=CurlyBracesNodes:\n" +
                "nodes: [BinOperationNode:\n" +
                "operator: Token(type=SET, text='set', pos=89)\n" +
                "whomAssign: VariableNode: Token(type=VARIABLE, text='x', pos=93)\n" +
                "whatAssign: ValueNode: Token(type=INTEGER, text='5', pos=95))\n" +
                "])])]"

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `while test 1`() {
        val code = "while \"\$x >= 1 && \$x <= 0\" {\n" +
                "set x 4\n" +
                "puts \"x is \$x\"\n" +
                "if {\$x > 4} {break}" +
                "} \n"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode: \n" +
                "[WhileLoopNode(condition=BracesNodes\n" +
                "nodes: [VariableNode: Token(type=LINK_VARIABLE, text='\$x', pos=7), OperationNode: Token(type=GREATER_OR_EQUAL, text='>=', pos=10)), ValueNode: Token(type=INTEGER, text='1', pos=13)), OperationNode: Token(type=AND, text='&&', pos=15)), VariableNode: Token(type=LINK_VARIABLE, text='\$x', pos=18), OperationNode: Token(type=LESS_OR_EQUAL, text='<=', pos=21)), ValueNode: Token(type=INTEGER, text='0', pos=24))], body=CurlyBracesNodes:\n" +
                "nodes: [BinOperationNode:\n" +
                "operator: Token(type=SET, text='set', pos=29)\n" +
                "whomAssign: VariableNode: Token(type=VARIABLE, text='x', pos=33)\n" +
                "whatAssign: ValueNode: Token(type=INTEGER, text='4', pos=35))\n" +
                ", UnarOperationNode\n" +
                "operator: Token(type=PUTS, text='puts', pos=37)\n" +
                "operand: QuotationNodes\n" +
                "nodes: [StringNode: x is , VariableNode: Token(type=LINK_VARIABLE, text='\$x', pos=48)], IfNode(branches=[IfBranch(condition=BracesNodes\n" +
                "nodes: [VariableNode: Token(type=LINK_VARIABLE, text='\$x', pos=56), OperationNode: Token(type=GREATER, text='>', pos=59)), ValueNode: Token(type=INTEGER, text='4', pos=61))], body=CurlyBracesNodes:\n" +
                "nodes: [TCLKeywordsNode(keyword=Token(type=BREAK, text='break', pos=65))])])])]"

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `while test 2`() {
        val code = "while {\$x >= 1 && \$x <= 0} {\n" +
                "set x 4\n" +
                "puts \"x is \$x\"\n" +
                "if {\$x > 4} {break}" +
                "} \n"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode: \n" +
                "[WhileLoopNode(condition=BracesNodes\n" +
                "nodes: [VariableNode: Token(type=LINK_VARIABLE, text='\$x', pos=7), OperationNode: Token(type=GREATER_OR_EQUAL, text='>=', pos=10)), ValueNode: Token(type=INTEGER, text='1', pos=13)), OperationNode: Token(type=AND, text='&&', pos=15)), VariableNode: Token(type=LINK_VARIABLE, text='\$x', pos=18), OperationNode: Token(type=LESS_OR_EQUAL, text='<=', pos=21)), ValueNode: Token(type=INTEGER, text='0', pos=24))], body=CurlyBracesNodes:\n" +
                "nodes: [BinOperationNode:\n" +
                "operator: Token(type=SET, text='set', pos=29)\n" +
                "whomAssign: VariableNode: Token(type=VARIABLE, text='x', pos=33)\n" +
                "whatAssign: ValueNode: Token(type=INTEGER, text='4', pos=35))\n" +
                ", UnarOperationNode\n" +
                "operator: Token(type=PUTS, text='puts', pos=37)\n" +
                "operand: QuotationNodes\n" +
                "nodes: [StringNode: x is , VariableNode: Token(type=LINK_VARIABLE, text='\$x', pos=48)], IfNode(branches=[IfBranch(condition=BracesNodes\n" +
                "nodes: [VariableNode: Token(type=LINK_VARIABLE, text='\$x', pos=56), OperationNode: Token(type=GREATER, text='>', pos=59)), ValueNode: Token(type=INTEGER, text='4', pos=61))], body=CurlyBracesNodes:\n" +
                "nodes: [TCLKeywordsNode(keyword=Token(type=BREAK, text='break', pos=65))])])])]"

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `for test 1`() {
        val code = "for {puts \"start\"; set i 0} {\$i > 0} {incr i; puts \"incremented\"} {puts \"command\"}"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode: \n" +
                "[ForLoopNode(initBlock=[UnarOperationNode\n" +
                "operator: Token(type=PUTS, text='puts', pos=5)\n" +
                "operand: QuotationNodes\n" +
                "nodes: [StringNode: start], BinOperationNode:\n" +
                "operator: Token(type=SET, text='set', pos=19)\n" +
                "whomAssign: VariableNode: Token(type=VARIABLE, text='i', pos=23)\n" +
                "whatAssign: ValueNode: Token(type=INTEGER, text='0', pos=25))\n" +
                "], conditionsBlock=[BracesNodes\n" +
                "nodes: [VariableNode: Token(type=LINK_VARIABLE, text='\$i', pos=29), OperationNode: Token(type=GREATER, text='>', pos=32)), ValueNode: Token(type=INTEGER, text='0', pos=34))]], counterBlock=[IncrNode(variable=VariableNode: Token(type=VARIABLE, text='i', pos=43), value=1), UnarOperationNode\n" +
                "operator: Token(type=PUTS, text='puts', pos=46)\n" +
                "operand: QuotationNodes\n" +
                "nodes: [StringNode: incremented]], commandBlock=[UnarOperationNode\n" +
                "operator: Token(type=PUTS, text='puts', pos=67)\n" +
                "operand: QuotationNodes\n" +
                "nodes: [StringNode: command]])]"

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `for test 2`() {
        val code = "for {set i 5} {\$i > 0 && \$i <= 100} {incr i -1;} {}"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode: \n" +
                "[ForLoopNode(initBlock=[BinOperationNode:\n" +
                "operator: Token(type=SET, text='set', pos=5)\n" +
                "whomAssign: VariableNode: Token(type=VARIABLE, text='i', pos=9)\n" +
                "whatAssign: ValueNode: Token(type=INTEGER, text='5', pos=11))\n" +
                "], conditionsBlock=[BracesNodes\n" +
                "nodes: [VariableNode: Token(type=LINK_VARIABLE, text='\$i', pos=15), OperationNode: Token(type=GREATER, text='>', pos=18)), ValueNode: Token(type=INTEGER, text='0', pos=20)), OperationNode: Token(type=AND, text='&&', pos=22)), VariableNode: Token(type=LINK_VARIABLE, text='\$i', pos=25), OperationNode: Token(type=LESS_OR_EQUAL, text='<=', pos=28)), ValueNode: Token(type=INTEGER, text='100', pos=31))]], counterBlock=[IncrNode(variable=VariableNode: Token(type=VARIABLE, text='i', pos=42), value=-1)], commandBlock=[])]"

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `return test 1`() {
        val code = "proc foo {} {\n" +
                "return " +
                "}\n"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode: \n" +
                "[ProcNode(functionName=StringNode: foo, args=[], body=CurlyBracesNodes:\n" +
                "nodes: [ReturnNode(returnValue=null)])]"

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `return test 2`() {
        val code = "proc foo {} {\n" +
                "return 0" +
                "}\n"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode: \n" +
                "[ProcNode(functionName=StringNode: foo, args=[], body=CurlyBracesNodes:\n" +
                "nodes: [ReturnNode(returnValue=ValueNode: Token(type=INTEGER, text='0', pos=21)))])]"

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `return test 3`() {
        val code = "proc foo {} {\n" +
                "return 2.54" +
                "}\n"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode: \n" +
                "[ProcNode(functionName=StringNode: foo, args=[], body=CurlyBracesNodes:\n" +
                "nodes: [ReturnNode(returnValue=ValueNode: Token(type=FLOAT, text='2.54', pos=21)))])]"

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `return test 4`() {
        val code = "proc foo {} {\n" +
                "return \"hello world\"" +
                "}\n"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode: \n" +
                "[ProcNode(functionName=StringNode: foo, args=[], body=CurlyBracesNodes:\n" +
                "nodes: [ReturnNode(returnValue=QuotationNodes\n" +
                "nodes: [StringNode: hello world])])]"

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `return test 5`() {
        val code = "proc foo {} {\n" +
                "return hello" +
                "}\n"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode: \n" +
                "[ProcNode(functionName=StringNode: foo, args=[], body=CurlyBracesNodes:\n" +
                "nodes: [ReturnNode(returnValue=StringNode: hello)])]"

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `return test 6`() {
        val code = "proc foo {} {\n" +
                "return [expr 2 + 3]" +
                "}\n"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode: \n" +
                "[ProcNode(functionName=StringNode: foo, args=[], body=CurlyBracesNodes:\n" +
                "nodes: [ReturnNode(returnValue=SquareBracesNodes:\n" +
                "nodes: [UnarOperationNode\n" +
                "operator: Token(type=EXPR, text='expr', pos=22)\n" +
                "operand: MathExpNodes:\n" +
                "nodes: [ValueNode: Token(type=INTEGER, text='2', pos=27)), OperationNode: Token(type=OPERATION, text='+', pos=29)), ValueNode: Token(type=INTEGER, text='3', pos=31))]])])]"

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `proc test 1`() {
        val code = "proc sum {arg1 arg2} {\n" +
                "\tset x [expr \$arg1+\$arg2];\n" +
                "\treturn \$x\n" +
                "}\n"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode: \n" +
                "[ProcNode(functionName=StringNode: sum, args=[VariableNode: Token(type=VARIABLE, text='arg1', pos=10), VariableNode: Token(type=VARIABLE, text='arg2', pos=15)], body=CurlyBracesNodes:\n" +
                "nodes: [BinOperationNode:\n" +
                "operator: Token(type=SET, text='set', pos=24)\n" +
                "whomAssign: VariableNode: Token(type=VARIABLE, text='x', pos=28)\n" +
                "whatAssign: SquareBracesNodes:\n" +
                "nodes: [UnarOperationNode\n" +
                "operator: Token(type=EXPR, text='expr', pos=31)\n" +
                "operand: MathExpNodes:\n" +
                "nodes: [VariableNode: Token(type=LINK_VARIABLE, text='\$arg1', pos=36), OperationNode: Token(type=OPERATION, text='+', pos=41)), VariableNode: Token(type=LINK_VARIABLE, text='\$arg2', pos=42)]]\n" +
                ", ReturnNode(returnValue=VariableNode: Token(type=LINK_VARIABLE, text='\$x', pos=58))])]"

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `proc test 2`() {
        val code = "proc sum {} {\n" +
                "\treturn 0" +
                "}\n"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode: \n" +
                "[ProcNode(functionName=StringNode: sum, args=[], body=CurlyBracesNodes:\n" +
                "nodes: [ReturnNode(returnValue=ValueNode: Token(type=INTEGER, text='0', pos=22)))])]"

        Assertions.assertEquals(expected, actual)
    }
}