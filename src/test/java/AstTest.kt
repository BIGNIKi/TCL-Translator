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

        val expected = "StatementsNode(codeStrings=[BinOperationNode(operator=Token(type=SET, text='set', pos=0), whomAssign=VariableNode(variable=Token(type=VARIABLE, text='a', pos=4)), whatAssign=CurlyBracesNodes(nodes=[StringNode(string='[set b \"Some string\"]')]))])"

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `set + grouping with braces test 2`() {
        val code = "set a \"[set b {Some string}]\";"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode(codeStrings=[BinOperationNode(operator=Token(type=SET, text='set', pos=0), whomAssign=VariableNode(variable=Token(type=VARIABLE, text='a', pos=4)), whatAssign=QuotationNodes(nodes=[SquareBracesNodes(nodes=[BinOperationNode(operator=Token(type=SET, text='set', pos=8), whomAssign=VariableNode(variable=Token(type=VARIABLE, text='b', pos=12)), whatAssign=CurlyBracesNodes(nodes=[StringNode(string='Some string')]))])]))])"

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `set + grouping with braces test 3`() {
        val code = "set a \"\\[set b {Some string}]\";"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode(codeStrings=[BinOperationNode(operator=Token(type=SET, text='set', pos=0), whomAssign=VariableNode(variable=Token(type=VARIABLE, text='a', pos=4)), whatAssign=QuotationNodes(nodes=[StringNode(string='[set b {Some string}]')]))])"

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `set + grouping with braces test 4`() {
        val code = "set a [set b \" 123 \"];"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode(codeStrings=[BinOperationNode(operator=Token(type=SET, text='set', pos=0), whomAssign=VariableNode(variable=Token(type=VARIABLE, text='a', pos=4)), whatAssign=SquareBracesNodes(nodes=[BinOperationNode(operator=Token(type=SET, text='set', pos=7), whomAssign=VariableNode(variable=Token(type=VARIABLE, text='b', pos=11)), whatAssign=QuotationNodes(nodes=[StringNode(string=' 123 ')]))]))])"

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `set + grouping with braces test 5`() {
        val code = "set a [set b { 123 }];"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode(codeStrings=[BinOperationNode(operator=Token(type=SET, text='set', pos=0), whomAssign=VariableNode(variable=Token(type=VARIABLE, text='a', pos=4)), whatAssign=SquareBracesNodes(nodes=[BinOperationNode(operator=Token(type=SET, text='set', pos=7), whomAssign=VariableNode(variable=Token(type=VARIABLE, text='b', pos=11)), whatAssign=CurlyBracesNodes(nodes=[StringNode(string=' 123 ')]))]))])"

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `set + grouping with braces test 6`() {
        val code = "set a \"Some string\";"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode(codeStrings=[BinOperationNode(operator=Token(type=SET, text='set', pos=0), whomAssign=VariableNode(variable=Token(type=VARIABLE, text='a', pos=4)), whatAssign=QuotationNodes(nodes=[StringNode(string='Some string')]))])"

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `set + grouping with braces test 7`() {
        val code = "set a \"Some string \$a\";"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode(codeStrings=[BinOperationNode(operator=Token(type=SET, text='set', pos=0), whomAssign=VariableNode(variable=Token(type=VARIABLE, text='a', pos=4)), whatAssign=QuotationNodes(nodes=[StringNode(string='Some string '), VariableNode(variable=Token(type=LINK_VARIABLE, text='\$a', pos=19))]))])"

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `set + grouping with braces test 8`() {
        val code = "set a \"Some string " + "\u005C" + "\$a\";"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode(codeStrings=[BinOperationNode(operator=Token(type=SET, text='set', pos=0), whomAssign=VariableNode(variable=Token(type=VARIABLE, text='a', pos=4)), whatAssign=QuotationNodes(nodes=[StringNode(string='Some string \$a')]))])"

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `set test 9`() {
        val code = "set x 3\n"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode(codeStrings=[BinOperationNode(operator=Token(type=SET, text='set', pos=0), whomAssign=VariableNode(variable=Token(type=VARIABLE, text='x', pos=4)), whatAssign=ValueNode(value=Token(type=INTEGER, text='3', pos=6)))])"

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `set test 10`() {
        val code = "set x 3.333\n"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode(codeStrings=[BinOperationNode(operator=Token(type=SET, text='set', pos=0), whomAssign=VariableNode(variable=Token(type=VARIABLE, text='x', pos=4)), whatAssign=ValueNode(value=Token(type=FLOAT, text='3.333', pos=6)))])"

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `set test 11`() {
        val code = "set x hello;"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode(codeStrings=[BinOperationNode(operator=Token(type=SET, text='set', pos=0), whomAssign=VariableNode(variable=Token(type=VARIABLE, text='x', pos=4)), whatAssign=ValueNode(value=Token(type=STRING, text='hello', pos=6)))])"

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `puts + grouping with braces test 1`() {
        val code = "puts \"Hello world\";"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode(codeStrings=[UnarOperationNode(operator=Token(type=PUTS, text='puts', pos=0), operand=QuotationNodes(nodes=[StringNode(string='Hello world')]))])"

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `puts + grouping with braces test 2`() {
        val code = "puts {Hello world};"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode(codeStrings=[UnarOperationNode(operator=Token(type=PUTS, text='puts', pos=0), operand=CurlyBracesNodes(nodes=[StringNode(string='Hello world')]))])"

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `puts + grouping with braces test 3`() {
        val code = "puts \"Hello world\"; puts \"Hello world\";"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode(codeStrings=[UnarOperationNode(operator=Token(type=PUTS, text='puts', pos=0), operand=QuotationNodes(nodes=[StringNode(string='Hello world')])), UnarOperationNode(operator=Token(type=PUTS, text='puts', pos=20), operand=QuotationNodes(nodes=[StringNode(string='Hello world')]))])"

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `puts + grouping with braces test 4`() {
        val code = "puts \"\$a \$b\";"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode(codeStrings=[UnarOperationNode(operator=Token(type=PUTS, text='puts', pos=0), operand=QuotationNodes(nodes=[VariableNode(variable=Token(type=LINK_VARIABLE, text='\$a', pos=6)), StringNode(string=' '), VariableNode(variable=Token(type=LINK_VARIABLE, text='\$b', pos=9))]))])"

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `puts + grouping with braces test 5`() {
        val code = "puts \"\$a " + "\u005C" + "\$b\";"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode(codeStrings=[UnarOperationNode(operator=Token(type=PUTS, text='puts', pos=0), operand=QuotationNodes(nodes=[VariableNode(variable=Token(type=LINK_VARIABLE, text='\$a', pos=6)), StringNode(string=' \$b')]))])"

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `puts test 6`() {
        val code = "puts hello;"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode(codeStrings=[UnarOperationNode(operator=Token(type=PUTS, text='puts', pos=0), operand=ValueNode(value=Token(type=STRING, text='hello', pos=5)))])"

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `puts test 7`() {
        val code = "puts 3;"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode(codeStrings=[UnarOperationNode(operator=Token(type=PUTS, text='puts', pos=0), operand=ValueNode(value=Token(type=INTEGER, text='3', pos=5)))])"

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `puts test 8`() {
        val code = "puts 3.33;"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode(codeStrings=[UnarOperationNode(operator=Token(type=PUTS, text='puts', pos=0), operand=ValueNode(value=Token(type=FLOAT, text='3.33', pos=5)))])"

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `expr test 1`() {
        val code = "puts [expr 2 + (3 * (4 - 2)) + 1];"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode(codeStrings=[UnarOperationNode(operator=Token(type=PUTS, text='puts', pos=0), operand=SquareBracesNodes(nodes=[UnarOperationNode(operator=Token(type=EXPR, text='expr', pos=6), operand=MathExpNodes(nodes=[ValueNode(value=Token(type=INTEGER, text='2', pos=11)), OperationNode(operation=Token(type=PLUS, text='+', pos=13)), BracesNodes(nodes=[ValueNode(value=Token(type=INTEGER, text='3', pos=16)), OperationNode(operation=Token(type=MULTIPLICATION, text='*', pos=18)), BracesNodes(nodes=[ValueNode(value=Token(type=INTEGER, text='4', pos=21)), OperationNode(operation=Token(type=MINUS, text='-', pos=23)), ValueNode(value=Token(type=INTEGER, text='2', pos=25))])]), OperationNode(operation=Token(type=PLUS, text='+', pos=29)), ValueNode(value=Token(type=INTEGER, text='1', pos=31))]))]))])"

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `expr test 2`() {
        val code = "puts [expr 2 * 4 + 1];"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode(codeStrings=[UnarOperationNode(operator=Token(type=PUTS, text='puts', pos=0), operand=SquareBracesNodes(nodes=[UnarOperationNode(operator=Token(type=EXPR, text='expr', pos=6), operand=MathExpNodes(nodes=[ValueNode(value=Token(type=INTEGER, text='2', pos=11)), OperationNode(operation=Token(type=MULTIPLICATION, text='*', pos=13)), ValueNode(value=Token(type=INTEGER, text='4', pos=15)), OperationNode(operation=Token(type=PLUS, text='+', pos=17)), ValueNode(value=Token(type=INTEGER, text='1', pos=19))]))]))])"
        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `expr test 3`() {
        val code = "puts [expr sqrt(9) + 1];"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode(codeStrings=[UnarOperationNode(operator=Token(type=PUTS, text='puts', pos=0), operand=SquareBracesNodes(nodes=[UnarOperationNode(operator=Token(type=EXPR, text='expr', pos=6), operand=MathExpNodes(nodes=[MathFunctionNode(mathFun=Token(type=SQRT, text='sqrt', pos=11), arguments=[ValueNode(value=Token(type=INTEGER, text='9', pos=16))]), OperationNode(operation=Token(type=PLUS, text='+', pos=19)), ValueNode(value=Token(type=INTEGER, text='1', pos=21))]))]))])"
        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `expr test 4`() {
        val code = "puts [expr \$a + \$b + 2];"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode(codeStrings=[UnarOperationNode(operator=Token(type=PUTS, text='puts', pos=0), operand=SquareBracesNodes(nodes=[UnarOperationNode(operator=Token(type=EXPR, text='expr', pos=6), operand=MathExpNodes(nodes=[VariableNode(variable=Token(type=LINK_VARIABLE, text='\$a', pos=11)), OperationNode(operation=Token(type=PLUS, text='+', pos=14)), VariableNode(variable=Token(type=LINK_VARIABLE, text='\$b', pos=16)), OperationNode(operation=Token(type=PLUS, text='+', pos=19)), ValueNode(value=Token(type=INTEGER, text='2', pos=21))]))]))])"
        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `expr test 5`() {
        val code = "puts [expr \"\$a + \$b + 2\"];"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode(codeStrings=[UnarOperationNode(operator=Token(type=PUTS, text='puts', pos=0), operand=SquareBracesNodes(nodes=[UnarOperationNode(operator=Token(type=EXPR, text='expr', pos=6), operand=MathExpNodes(nodes=[VariableNode(variable=Token(type=LINK_VARIABLE, text='\$a', pos=12)), OperationNode(operation=Token(type=PLUS, text='+', pos=15)), VariableNode(variable=Token(type=LINK_VARIABLE, text='\$b', pos=17)), OperationNode(operation=Token(type=PLUS, text='+', pos=20)), ValueNode(value=Token(type=INTEGER, text='2', pos=22))]))]))])"

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `expr test 6`() {
        val code = "puts [expr {\$a + \$b + 2}];"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode(codeStrings=[UnarOperationNode(operator=Token(type=PUTS, text='puts', pos=0), operand=SquareBracesNodes(nodes=[UnarOperationNode(operator=Token(type=EXPR, text='expr', pos=6), operand=MathExpNodes(nodes=[VariableNode(variable=Token(type=LINK_VARIABLE, text='\$a', pos=12)), OperationNode(operation=Token(type=PLUS, text='+', pos=15)), VariableNode(variable=Token(type=LINK_VARIABLE, text='\$b', pos=17)), OperationNode(operation=Token(type=PLUS, text='+', pos=20)), ValueNode(value=Token(type=INTEGER, text='2', pos=22))]))]))])"
        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `expr test 7`() {
        val code = "puts [expr \"123\" + \"312\"]\n"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode(codeStrings=[UnarOperationNode(operator=Token(type=PUTS, text='puts', pos=0), operand=SquareBracesNodes(nodes=[UnarOperationNode(operator=Token(type=EXPR, text='expr', pos=6), operand=MathExpNodes(nodes=[ValueNode(value=Token(type=INTEGER, text='123', pos=12)), OperationNode(operation=Token(type=PLUS, text='+', pos=17)), ValueNode(value=Token(type=INTEGER, text='312', pos=20))]))]))])"
        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `expr test 8`() {
        val code = "puts [expr pow(2, 4)]\n"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode(codeStrings=[UnarOperationNode(operator=Token(type=PUTS, text='puts', pos=0), operand=SquareBracesNodes(nodes=[UnarOperationNode(operator=Token(type=EXPR, text='expr', pos=6), operand=MathExpNodes(nodes=[MathFunctionNode(mathFun=Token(type=POW, text='pow', pos=11), arguments=[ValueNode(value=Token(type=INTEGER, text='2', pos=15)), ValueNode(value=Token(type=INTEGER, text='4', pos=18))])]))]))])"
        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `expr test 9`() {
        val code = "puts [expr rand()]\n"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode(codeStrings=[UnarOperationNode(operator=Token(type=PUTS, text='puts', pos=0), operand=SquareBracesNodes(nodes=[UnarOperationNode(operator=Token(type=EXPR, text='expr', pos=6), operand=MathExpNodes(nodes=[MathFunctionNode(mathFun=Token(type=RAND, text='rand', pos=11), arguments=[])]))]))])"
        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `expr test 10`() {
        val code = "puts [expr sqrt([expr pow(2, 4)])]\n"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode(codeStrings=[UnarOperationNode(operator=Token(type=PUTS, text='puts', pos=0), operand=SquareBracesNodes(nodes=[UnarOperationNode(operator=Token(type=EXPR, text='expr', pos=6), operand=MathExpNodes(nodes=[MathFunctionNode(mathFun=Token(type=SQRT, text='sqrt', pos=11), arguments=[SquareBracesNodes(nodes=[UnarOperationNode(operator=Token(type=EXPR, text='expr', pos=17), operand=MathExpNodes(nodes=[MathFunctionNode(mathFun=Token(type=POW, text='pow', pos=22), arguments=[ValueNode(value=Token(type=INTEGER, text='2', pos=26)), ValueNode(value=Token(type=INTEGER, text='4', pos=29))])]))])])]))]))])"
        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `comment test 1`() {
        val code = "# Comment also can be parsed;"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode(codeStrings=[CommentNode(comment=Token(type=COMMENT, text='# Comment also can be parsed', pos=0))])"

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `switch case test 1`() {
        val code = "switch \$x \"one\" \"puts one\" \"two\" \"puts two\" \"default\" \"puts none\";"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode(codeStrings=[SwitchNode(string=Token(type=LINK_VARIABLE, text='\$x', pos=7), cases=[SwitchCase(value=Token(type=STRING, text='one', pos=11), body=CurlyBracesNodes(nodes=[UnarOperationNode(operator=Token(type=PUTS, text='puts', pos=17), operand=ValueNode(value=Token(type=STRING, text='one', pos=22)))])), SwitchCase(value=Token(type=STRING, text='two', pos=28), body=CurlyBracesNodes(nodes=[UnarOperationNode(operator=Token(type=PUTS, text='puts', pos=34), operand=ValueNode(value=Token(type=STRING, text='two', pos=39)))])), SwitchCase(value=Token(type=DEFAULT, text='default', pos=45), body=CurlyBracesNodes(nodes=[UnarOperationNode(operator=Token(type=PUTS, text='puts', pos=55), operand=ValueNode(value=Token(type=STRING, text='none', pos=60)))]))], isSubstitutionsAllowed=false)])"

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

        val expected = "StatementsNode(codeStrings=[SwitchNode(string=Token(type=LINK_VARIABLE, text='\$x', pos=7), cases=[SwitchCase(value=Token(type=STRING, text='one', pos=14), body=CurlyBracesNodes(nodes=[UnarOperationNode(operator=Token(type=PUTS, text='puts', pos=21), operand=ValueNode(value=Token(type=STRING, text='one', pos=26)))])), SwitchCase(value=Token(type=STRING, text='two', pos=36), body=CurlyBracesNodes(nodes=[UnarOperationNode(operator=Token(type=PUTS, text='puts', pos=43), operand=ValueNode(value=Token(type=STRING, text='two', pos=48)))])), SwitchCase(value=Token(type=DEFAULT, text='default', pos=57), body=CurlyBracesNodes(nodes=[UnarOperationNode(operator=Token(type=PUTS, text='puts', pos=68), operand=ValueNode(value=Token(type=STRING, text='none', pos=73)))]))], isSubstitutionsAllowed=false)])"

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

        val expected = "StatementsNode(codeStrings=[SwitchNode(string=Token(type=LINK_VARIABLE, text='\$x', pos=7), cases=[SwitchCase(value=Token(type=LINK_VARIABLE, text='\$z', pos=14), body=CurlyBracesNodes(nodes=[BinOperationNode(operator=Token(type=SET, text='set', pos=20), whomAssign=VariableNode(variable=Token(type=VARIABLE, text='y1', pos=24)), whatAssign=SquareBracesNodes(nodes=[UnarOperationNode(operator=Token(type=EXPR, text='expr', pos=28), operand=MathExpNodes(nodes=[VariableNode(variable=Token(type=LINK_VARIABLE, text='\$y', pos=33)), OperationNode(operation=Token(type=PLUS, text='+', pos=35)), ValueNode(value=Token(type=INTEGER, text='1', pos=36))]))])), UnarOperationNode(operator=Token(type=PUTS, text='puts', pos=40), operand=QuotationNodes(nodes=[StringNode(string='match '), VariableNode(variable=Token(type=LINK_VARIABLE, text='\$z', pos=52)), StringNode(string='. '), VariableNode(variable=Token(type=LINK_VARIABLE, text='\$y', pos=56)), StringNode(string=' + '), VariableNode(variable=Token(type=LINK_VARIABLE, text='\$z', pos=61)), StringNode(string=' is '), VariableNode(variable=Token(type=LINK_VARIABLE, text='\$y1', pos=67))]))])), SwitchCase(value=Token(type=STRING, text='one', pos=78), body=CurlyBracesNodes(nodes=[BinOperationNode(operator=Token(type=SET, text='set', pos=84), whomAssign=VariableNode(variable=Token(type=VARIABLE, text='y1', pos=88)), whatAssign=SquareBracesNodes(nodes=[UnarOperationNode(operator=Token(type=EXPR, text='expr', pos=92), operand=MathExpNodes(nodes=[VariableNode(variable=Token(type=LINK_VARIABLE, text='\$y', pos=97)), OperationNode(operation=Token(type=PLUS, text='+', pos=99)), ValueNode(value=Token(type=INTEGER, text='1', pos=100))]))])), UnarOperationNode(operator=Token(type=PUTS, text='puts', pos=104), operand=QuotationNodes(nodes=[StringNode(string='match one '), VariableNode(variable=Token(type=LINK_VARIABLE, text='\$y', pos=120)), StringNode(string=' plus one is '), VariableNode(variable=Token(type=LINK_VARIABLE, text='\$y1', pos=135))]))])), SwitchCase(value=Token(type=DEFAULT, text='default', pos=145), body=CurlyBracesNodes(nodes=[UnarOperationNode(operator=Token(type=PUTS, text='puts', pos=155), operand=QuotationNodes(nodes=[VariableNode(variable=Token(type=LINK_VARIABLE, text='\$x', pos=161)), StringNode(string=' none')]))]))], isSubstitutionsAllowed=true)])"

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

        val expected = "StatementsNode(codeStrings=[SwitchNode(string=Token(type=LINK_VARIABLE, text='\$x', pos=7), cases=[SwitchCase(value=Token(type=LINK_VARIABLE, text='\$z', pos=15), body=CurlyBracesNodes(nodes=[BinOperationNode(operator=Token(type=SET, text='set', pos=21), whomAssign=VariableNode(variable=Token(type=VARIABLE, text='y1', pos=25)), whatAssign=SquareBracesNodes(nodes=[UnarOperationNode(operator=Token(type=EXPR, text='expr', pos=29), operand=MathExpNodes(nodes=[VariableNode(variable=Token(type=LINK_VARIABLE, text='\$y', pos=34)), OperationNode(operation=Token(type=PLUS, text='+', pos=36)), ValueNode(value=Token(type=INTEGER, text='1', pos=37))]))])), UnarOperationNode(operator=Token(type=PUTS, text='puts', pos=41), operand=QuotationNodes(nodes=[StringNode(string='match '), VariableNode(variable=Token(type=LINK_VARIABLE, text='\$z', pos=53)), StringNode(string='. '), VariableNode(variable=Token(type=LINK_VARIABLE, text='\$y', pos=57)), StringNode(string=' + '), VariableNode(variable=Token(type=LINK_VARIABLE, text='\$z', pos=62)), StringNode(string=' is '), VariableNode(variable=Token(type=LINK_VARIABLE, text='\$y1', pos=68))]))])), SwitchCase(value=Token(type=STRING, text='one', pos=79), body=CurlyBracesNodes(nodes=[BinOperationNode(operator=Token(type=SET, text='set', pos=85), whomAssign=VariableNode(variable=Token(type=VARIABLE, text='y1', pos=89)), whatAssign=SquareBracesNodes(nodes=[UnarOperationNode(operator=Token(type=EXPR, text='expr', pos=93), operand=MathExpNodes(nodes=[VariableNode(variable=Token(type=LINK_VARIABLE, text='\$y', pos=98)), OperationNode(operation=Token(type=PLUS, text='+', pos=100)), ValueNode(value=Token(type=INTEGER, text='1', pos=101))]))])), UnarOperationNode(operator=Token(type=PUTS, text='puts', pos=105), operand=QuotationNodes(nodes=[StringNode(string='match one '), VariableNode(variable=Token(type=LINK_VARIABLE, text='\$y', pos=121)), StringNode(string=' plus one is '), VariableNode(variable=Token(type=LINK_VARIABLE, text='\$y1', pos=136))]))])), SwitchCase(value=Token(type=DEFAULT, text='default', pos=146), body=CurlyBracesNodes(nodes=[UnarOperationNode(operator=Token(type=PUTS, text='puts', pos=156), operand=QuotationNodes(nodes=[VariableNode(variable=Token(type=LINK_VARIABLE, text='\$x', pos=162)), StringNode(string=' none')]))]))], isSubstitutionsAllowed=false)])"

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `if test 1`() {
        val code = "if {\$x == 2} {puts \"\$x is 2\"} elseif {\$x == 3} {puts \"\$x is 3\"} else {puts \"\$x is none\"}\n"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode(codeStrings=[IfNode(branches=[IfBranch(condition=BracesNodes(nodes=[VariableNode(variable=Token(type=LINK_VARIABLE, text='\$x', pos=4)), OperationNode(operation=Token(type=IS_EQUAL, text='==', pos=7)), ValueNode(value=Token(type=INTEGER, text='2', pos=10))]), body=CurlyBracesNodes(nodes=[UnarOperationNode(operator=Token(type=PUTS, text='puts', pos=14), operand=QuotationNodes(nodes=[VariableNode(variable=Token(type=LINK_VARIABLE, text='\$x', pos=20)), StringNode(string=' is 2')]))])), IfBranch(condition=BracesNodes(nodes=[VariableNode(variable=Token(type=LINK_VARIABLE, text='\$x', pos=38)), OperationNode(operation=Token(type=IS_EQUAL, text='==', pos=41)), ValueNode(value=Token(type=INTEGER, text='3', pos=44))]), body=CurlyBracesNodes(nodes=[UnarOperationNode(operator=Token(type=PUTS, text='puts', pos=48), operand=QuotationNodes(nodes=[VariableNode(variable=Token(type=LINK_VARIABLE, text='\$x', pos=54)), StringNode(string=' is 3')]))])), IfBranch(condition=null, body=CurlyBracesNodes(nodes=[UnarOperationNode(operator=Token(type=PUTS, text='puts', pos=70), operand=QuotationNodes(nodes=[VariableNode(variable=Token(type=LINK_VARIABLE, text='\$x', pos=76)), StringNode(string=' is none')]))]))])])"
        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `if test 2`() {
        val code = "if {\$x == 2} {puts \"\$x is 2\"} else {puts \"\$x is none\"}\n"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode(codeStrings=[IfNode(branches=[IfBranch(condition=BracesNodes(nodes=[VariableNode(variable=Token(type=LINK_VARIABLE, text='\$x', pos=4)), OperationNode(operation=Token(type=IS_EQUAL, text='==', pos=7)), ValueNode(value=Token(type=INTEGER, text='2', pos=10))]), body=CurlyBracesNodes(nodes=[UnarOperationNode(operator=Token(type=PUTS, text='puts', pos=14), operand=QuotationNodes(nodes=[VariableNode(variable=Token(type=LINK_VARIABLE, text='\$x', pos=20)), StringNode(string=' is 2')]))])), IfBranch(condition=null, body=CurlyBracesNodes(nodes=[UnarOperationNode(operator=Token(type=PUTS, text='puts', pos=36), operand=QuotationNodes(nodes=[VariableNode(variable=Token(type=LINK_VARIABLE, text='\$x', pos=42)), StringNode(string=' is none')]))]))])])"

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `if test 3`() {
        val code = "if {\$x == 2} {puts \"\$x is 2\"} \n"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode(codeStrings=[IfNode(branches=[IfBranch(condition=BracesNodes(nodes=[VariableNode(variable=Token(type=LINK_VARIABLE, text='\$x', pos=4)), OperationNode(operation=Token(type=IS_EQUAL, text='==', pos=7)), ValueNode(value=Token(type=INTEGER, text='2', pos=10))]), body=CurlyBracesNodes(nodes=[UnarOperationNode(operator=Token(type=PUTS, text='puts', pos=14), operand=QuotationNodes(nodes=[VariableNode(variable=Token(type=LINK_VARIABLE, text='\$x', pos=20)), StringNode(string=' is 2')]))]))])])"

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

        val expected = "StatementsNode(codeStrings=[IfNode(branches=[IfBranch(condition=BracesNodes(nodes=[VariableNode(variable=Token(type=LINK_VARIABLE, text='\$a', pos=5)), OperationNode(operation=Token(type=IS_EQUAL, text='==', pos=8)), ValueNode(value=Token(type=INTEGER, text='10', pos=11))]), body=CurlyBracesNodes(nodes=[CommentNode(comment=Token(type=COMMENT, text='# if condition is true then print the following ', pos=21)), UnarOperationNode(operator=Token(type=PUTS, text='puts', pos=73), operand=QuotationNodes(nodes=[StringNode(string='Value of a is 10')]))])), IfBranch(condition=BracesNodes(nodes=[VariableNode(variable=Token(type=LINK_VARIABLE, text='\$a', pos=108)), OperationNode(operation=Token(type=IS_EQUAL, text='==', pos=111)), ValueNode(value=Token(type=INTEGER, text='20', pos=114))]), body=CurlyBracesNodes(nodes=[CommentNode(comment=Token(type=COMMENT, text='# if else if condition is true ', pos=124)), UnarOperationNode(operator=Token(type=PUTS, text='puts', pos=159), operand=QuotationNodes(nodes=[StringNode(string='Value of a is 20')]))])), IfBranch(condition=BracesNodes(nodes=[VariableNode(variable=Token(type=LINK_VARIABLE, text='\$a', pos=194)), OperationNode(operation=Token(type=IS_EQUAL, text='==', pos=197)), ValueNode(value=Token(type=INTEGER, text='30', pos=200))]), body=CurlyBracesNodes(nodes=[CommentNode(comment=Token(type=COMMENT, text='# if else if condition is true ', pos=210)), UnarOperationNode(operator=Token(type=PUTS, text='puts', pos=245), operand=QuotationNodes(nodes=[StringNode(string='Value of a is 30')]))])), IfBranch(condition=null, body=CurlyBracesNodes(nodes=[CommentNode(comment=Token(type=COMMENT, text='# if none of the conditions is true ', pos=281)), UnarOperationNode(operator=Token(type=PUTS, text='puts', pos=321), operand=QuotationNodes(nodes=[StringNode(string='None of the values is matching')]))]))])])"

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `if test 5`() {
        val code = "if {\$x == 2 || \$x == 3} {puts \"\$x is 2\"} else {puts \"\$x is none\"}\n"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode(codeStrings=[IfNode(branches=[IfBranch(condition=BracesNodes(nodes=[VariableNode(variable=Token(type=LINK_VARIABLE, text='\$x', pos=4)), OperationNode(operation=Token(type=IS_EQUAL, text='==', pos=7)), ValueNode(value=Token(type=INTEGER, text='2', pos=10)), OperationNode(operation=Token(type=OR, text='||', pos=12)), VariableNode(variable=Token(type=LINK_VARIABLE, text='\$x', pos=15)), OperationNode(operation=Token(type=IS_EQUAL, text='==', pos=18)), ValueNode(value=Token(type=INTEGER, text='3', pos=21))]), body=CurlyBracesNodes(nodes=[UnarOperationNode(operator=Token(type=PUTS, text='puts', pos=25), operand=QuotationNodes(nodes=[VariableNode(variable=Token(type=LINK_VARIABLE, text='\$x', pos=31)), StringNode(string=' is 2')]))])), IfBranch(condition=null, body=CurlyBracesNodes(nodes=[UnarOperationNode(operator=Token(type=PUTS, text='puts', pos=47), operand=QuotationNodes(nodes=[VariableNode(variable=Token(type=LINK_VARIABLE, text='\$x', pos=53)), StringNode(string=' is none')]))]))])])"

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `if test 6`() {
        val code = "if {\$y == true || (\$x == 2 && \$b != 3)} {set x 2} elseif {\$bool == true} {set x 3} else {set x 5}\n"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode(codeStrings=[IfNode(branches=[IfBranch(condition=BracesNodes(nodes=[VariableNode(variable=Token(type=LINK_VARIABLE, text='\$y', pos=4)), OperationNode(operation=Token(type=IS_EQUAL, text='==', pos=7)), ValueNode(value=Token(type=TRUE, text='true', pos=10)), OperationNode(operation=Token(type=OR, text='||', pos=15)), BracesNodes(nodes=[VariableNode(variable=Token(type=LINK_VARIABLE, text='\$x', pos=19)), OperationNode(operation=Token(type=IS_EQUAL, text='==', pos=22)), ValueNode(value=Token(type=INTEGER, text='2', pos=25)), OperationNode(operation=Token(type=AND, text='&&', pos=27)), VariableNode(variable=Token(type=LINK_VARIABLE, text='\$b', pos=30)), OperationNode(operation=Token(type=IS_NOT_EQUAL, text='!=', pos=33)), ValueNode(value=Token(type=INTEGER, text='3', pos=36))])]), body=CurlyBracesNodes(nodes=[BinOperationNode(operator=Token(type=SET, text='set', pos=41), whomAssign=VariableNode(variable=Token(type=VARIABLE, text='x', pos=45)), whatAssign=ValueNode(value=Token(type=INTEGER, text='2', pos=47)))])), IfBranch(condition=BracesNodes(nodes=[VariableNode(variable=Token(type=LINK_VARIABLE, text='\$bool', pos=58)), OperationNode(operation=Token(type=IS_EQUAL, text='==', pos=64)), ValueNode(value=Token(type=TRUE, text='true', pos=67))]), body=CurlyBracesNodes(nodes=[BinOperationNode(operator=Token(type=SET, text='set', pos=74), whomAssign=VariableNode(variable=Token(type=VARIABLE, text='x', pos=78)), whatAssign=ValueNode(value=Token(type=INTEGER, text='3', pos=80)))])), IfBranch(condition=null, body=CurlyBracesNodes(nodes=[BinOperationNode(operator=Token(type=SET, text='set', pos=89), whomAssign=VariableNode(variable=Token(type=VARIABLE, text='x', pos=93)), whatAssign=ValueNode(value=Token(type=INTEGER, text='5', pos=95)))]))])])"

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `if test 7`() {
        val code = "if {\$x == 2} {puts \"\$x is 2\"} elseif {\$x == 3} {puts \"\$x is 3\"}\n"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode(codeStrings=[IfNode(branches=[IfBranch(condition=BracesNodes(nodes=[VariableNode(variable=Token(type=LINK_VARIABLE, text='\$x', pos=4)), OperationNode(operation=Token(type=IS_EQUAL, text='==', pos=7)), ValueNode(value=Token(type=INTEGER, text='2', pos=10))]), body=CurlyBracesNodes(nodes=[UnarOperationNode(operator=Token(type=PUTS, text='puts', pos=14), operand=QuotationNodes(nodes=[VariableNode(variable=Token(type=LINK_VARIABLE, text='\$x', pos=20)), StringNode(string=' is 2')]))])), IfBranch(condition=BracesNodes(nodes=[VariableNode(variable=Token(type=LINK_VARIABLE, text='\$x', pos=38)), OperationNode(operation=Token(type=IS_EQUAL, text='==', pos=41)), ValueNode(value=Token(type=INTEGER, text='3', pos=44))]), body=CurlyBracesNodes(nodes=[UnarOperationNode(operator=Token(type=PUTS, text='puts', pos=48), operand=QuotationNodes(nodes=[VariableNode(variable=Token(type=LINK_VARIABLE, text='\$x', pos=54)), StringNode(string=' is 3')]))]))])])"
        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `if test 8`() {
        val code = "if  {\$x == [expr pow(2,4)]} {puts yes} else {puts wrong}"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode(codeStrings=[IfNode(branches=[IfBranch(condition=BracesNodes(nodes=[VariableNode(variable=Token(type=LINK_VARIABLE, text='\$x', pos=5)), OperationNode(operation=Token(type=IS_EQUAL, text='==', pos=8)), SquareBracesNodes(nodes=[UnarOperationNode(operator=Token(type=EXPR, text='expr', pos=12), operand=MathExpNodes(nodes=[MathFunctionNode(mathFun=Token(type=POW, text='pow', pos=17), arguments=[ValueNode(value=Token(type=INTEGER, text='2', pos=21)), ValueNode(value=Token(type=INTEGER, text='4', pos=23))])]))])]), body=CurlyBracesNodes(nodes=[UnarOperationNode(operator=Token(type=PUTS, text='puts', pos=29), operand=ValueNode(value=Token(type=STRING, text='yes', pos=34)))])), IfBranch(condition=null, body=CurlyBracesNodes(nodes=[UnarOperationNode(operator=Token(type=PUTS, text='puts', pos=45), operand=ValueNode(value=Token(type=STRING, text='wrong', pos=50)))]))])])"
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

        val expected = "StatementsNode(codeStrings=[WhileLoopNode(condition=BracesNodes(nodes=[VariableNode(variable=Token(type=LINK_VARIABLE, text='\$x', pos=7)), OperationNode(operation=Token(type=GREATER_OR_EQUAL, text='>=', pos=10)), ValueNode(value=Token(type=INTEGER, text='1', pos=13)), OperationNode(operation=Token(type=AND, text='&&', pos=15)), VariableNode(variable=Token(type=LINK_VARIABLE, text='\$x', pos=18)), OperationNode(operation=Token(type=LESS_OR_EQUAL, text='<=', pos=21)), ValueNode(value=Token(type=INTEGER, text='0', pos=24))]), body=CurlyBracesNodes(nodes=[BinOperationNode(operator=Token(type=SET, text='set', pos=29), whomAssign=VariableNode(variable=Token(type=VARIABLE, text='x', pos=33)), whatAssign=ValueNode(value=Token(type=INTEGER, text='4', pos=35))), UnarOperationNode(operator=Token(type=PUTS, text='puts', pos=37), operand=QuotationNodes(nodes=[StringNode(string='x is '), VariableNode(variable=Token(type=LINK_VARIABLE, text='\$x', pos=48))])), IfNode(branches=[IfBranch(condition=BracesNodes(nodes=[VariableNode(variable=Token(type=LINK_VARIABLE, text='\$x', pos=56)), OperationNode(operation=Token(type=GREATER, text='>', pos=59)), ValueNode(value=Token(type=INTEGER, text='4', pos=61))]), body=CurlyBracesNodes(nodes=[TCLKeywordsNode(keyword=Token(type=BREAK, text='break', pos=65))]))])]))])"

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

        val expected = "StatementsNode(codeStrings=[WhileLoopNode(condition=BracesNodes(nodes=[VariableNode(variable=Token(type=LINK_VARIABLE, text='\$x', pos=7)), OperationNode(operation=Token(type=GREATER_OR_EQUAL, text='>=', pos=10)), ValueNode(value=Token(type=INTEGER, text='1', pos=13)), OperationNode(operation=Token(type=AND, text='&&', pos=15)), VariableNode(variable=Token(type=LINK_VARIABLE, text='\$x', pos=18)), OperationNode(operation=Token(type=LESS_OR_EQUAL, text='<=', pos=21)), ValueNode(value=Token(type=INTEGER, text='0', pos=24))]), body=CurlyBracesNodes(nodes=[BinOperationNode(operator=Token(type=SET, text='set', pos=29), whomAssign=VariableNode(variable=Token(type=VARIABLE, text='x', pos=33)), whatAssign=ValueNode(value=Token(type=INTEGER, text='4', pos=35))), UnarOperationNode(operator=Token(type=PUTS, text='puts', pos=37), operand=QuotationNodes(nodes=[StringNode(string='x is '), VariableNode(variable=Token(type=LINK_VARIABLE, text='\$x', pos=48))])), IfNode(branches=[IfBranch(condition=BracesNodes(nodes=[VariableNode(variable=Token(type=LINK_VARIABLE, text='\$x', pos=56)), OperationNode(operation=Token(type=GREATER, text='>', pos=59)), ValueNode(value=Token(type=INTEGER, text='4', pos=61))]), body=CurlyBracesNodes(nodes=[TCLKeywordsNode(keyword=Token(type=BREAK, text='break', pos=65))]))])]))])"

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `for test 1`() {
        val code = "for {puts \"start\"; set i 0} {\$i > 0} {incr i; puts \"incremented\"} {puts \"command\"}"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode(codeStrings=[ForLoopNode(initBlock=[UnarOperationNode(operator=Token(type=PUTS, text='puts', pos=5), operand=QuotationNodes(nodes=[StringNode(string='start')])), BinOperationNode(operator=Token(type=SET, text='set', pos=19), whomAssign=VariableNode(variable=Token(type=VARIABLE, text='i', pos=23)), whatAssign=ValueNode(value=Token(type=INTEGER, text='0', pos=25)))], conditionsBlock=[BracesNodes(nodes=[VariableNode(variable=Token(type=LINK_VARIABLE, text='\$i', pos=29)), OperationNode(operation=Token(type=GREATER, text='>', pos=32)), ValueNode(value=Token(type=INTEGER, text='0', pos=34))])], counterBlock=[IncrNode(variable=VariableNode(variable=Token(type=VARIABLE, text='i', pos=43)), value=1), UnarOperationNode(operator=Token(type=PUTS, text='puts', pos=46), operand=QuotationNodes(nodes=[StringNode(string='incremented')]))], commandBlock=[UnarOperationNode(operator=Token(type=PUTS, text='puts', pos=67), operand=QuotationNodes(nodes=[StringNode(string='command')]))])])"

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `for test 2`() {
        val code = "for {set i 5} {\$i > 0 && \$i <= 100} {incr i -1;} {}"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode(codeStrings=[ForLoopNode(initBlock=[BinOperationNode(operator=Token(type=SET, text='set', pos=5), whomAssign=VariableNode(variable=Token(type=VARIABLE, text='i', pos=9)), whatAssign=ValueNode(value=Token(type=INTEGER, text='5', pos=11)))], conditionsBlock=[BracesNodes(nodes=[VariableNode(variable=Token(type=LINK_VARIABLE, text='\$i', pos=15)), OperationNode(operation=Token(type=GREATER, text='>', pos=18)), ValueNode(value=Token(type=INTEGER, text='0', pos=20)), OperationNode(operation=Token(type=AND, text='&&', pos=22)), VariableNode(variable=Token(type=LINK_VARIABLE, text='\$i', pos=25)), OperationNode(operation=Token(type=LESS_OR_EQUAL, text='<=', pos=28)), ValueNode(value=Token(type=INTEGER, text='100', pos=31))])], counterBlock=[IncrNode(variable=VariableNode(variable=Token(type=VARIABLE, text='i', pos=42)), value=-1)], commandBlock=[])])"

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `return test 1`() {
        val code = "proc foo {} {\n" +
                "return " +
                "}\n"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode(codeStrings=[ProcNode(functionName=StringNode(string='foo'), args=[], body=CurlyBracesNodes(nodes=[ReturnNode(returnValue=null)]))])"

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `return test 2`() {
        val code = "proc foo {} {\n" +
                "return 0" +
                "}\n"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode(codeStrings=[ProcNode(functionName=StringNode(string='foo'), args=[], body=CurlyBracesNodes(nodes=[ReturnNode(returnValue=ValueNode(value=Token(type=INTEGER, text='0', pos=21)))]))])"

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `return test 3`() {
        val code = "proc foo {} {\n" +
                "return 2.54" +
                "}\n"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode(codeStrings=[ProcNode(functionName=StringNode(string='foo'), args=[], body=CurlyBracesNodes(nodes=[ReturnNode(returnValue=ValueNode(value=Token(type=FLOAT, text='2.54', pos=21)))]))])"

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `return test 4`() {
        val code = "proc foo {} {\n" +
                "return \"hello world\"" +
                "}\n"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode(codeStrings=[ProcNode(functionName=StringNode(string='foo'), args=[], body=CurlyBracesNodes(nodes=[ReturnNode(returnValue=QuotationNodes(nodes=[StringNode(string='hello world')]))]))])"

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `return test 5`() {
        val code = "proc foo {} {\n" +
                "return hello" +
                "}\n"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode(codeStrings=[ProcNode(functionName=StringNode(string='foo'), args=[], body=CurlyBracesNodes(nodes=[ReturnNode(returnValue=StringNode(string='hello'))]))])"

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `return test 6`() {
        val code = "proc foo {} {\n" +
                "return [expr 2 + 3]" +
                "}\n"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode(codeStrings=[ProcNode(functionName=StringNode(string='foo'), args=[], body=CurlyBracesNodes(nodes=[ReturnNode(returnValue=SquareBracesNodes(nodes=[UnarOperationNode(operator=Token(type=EXPR, text='expr', pos=22), operand=MathExpNodes(nodes=[ValueNode(value=Token(type=INTEGER, text='2', pos=27)), OperationNode(operation=Token(type=PLUS, text='+', pos=29)), ValueNode(value=Token(type=INTEGER, text='3', pos=31))]))]))]))])"

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

        val expected = "StatementsNode(codeStrings=[ProcNode(functionName=StringNode(string='sum'), args=[VariableNode(variable=Token(type=VARIABLE, text='arg1', pos=10)), VariableNode(variable=Token(type=VARIABLE, text='arg2', pos=15))], body=CurlyBracesNodes(nodes=[BinOperationNode(operator=Token(type=SET, text='set', pos=24), whomAssign=VariableNode(variable=Token(type=VARIABLE, text='x', pos=28)), whatAssign=SquareBracesNodes(nodes=[UnarOperationNode(operator=Token(type=EXPR, text='expr', pos=31), operand=MathExpNodes(nodes=[VariableNode(variable=Token(type=LINK_VARIABLE, text='\$arg1', pos=36)), OperationNode(operation=Token(type=PLUS, text='+', pos=41)), VariableNode(variable=Token(type=LINK_VARIABLE, text='\$arg2', pos=42))]))])), ReturnNode(returnValue=VariableNode(variable=Token(type=LINK_VARIABLE, text='\$x', pos=58)))]))])"

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `proc test 2`() {
        val code = "proc sum {} {\n" +
                "\treturn 0" +
                "}\n"
        val asl = Parser(Lexer(code).lexAnalysis()).parseCode()
        val actual = asl.toString()

        val expected = "StatementsNode(codeStrings=[ProcNode(functionName=StringNode(string='sum'), args=[], body=CurlyBracesNodes(nodes=[ReturnNode(returnValue=ValueNode(value=Token(type=INTEGER, text='0', pos=22)))]))])"

        Assertions.assertEquals(expected, actual)
    }
}