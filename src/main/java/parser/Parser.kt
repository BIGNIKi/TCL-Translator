package parser

import ast.*
import lexer.Token
import lexer.TokenType
import lexer.convertTo

class Parser(private val tokens: List<Token>) {
    var pos: Int = 0
    val variablesScope: HashMap<String, Any> = hashMapOf()
    val proceduresScope: MutableList<String> = mutableListOf()

    fun parseCode(): ExpressionNode {
        val root = StatementsNode()
        while (pos < tokens.size) {
            val codeString = parseExpression() // отдельно взятая строка кода
            removeSpaces()

            if (pos < tokens.size) {
                require(listOf(TokenType.SEMICOLON))
            }

            root.addNode(codeString)
        }
        return root
    }

    /**
     * Метод парсит отдельно взятую строчку
     */
    private fun parseExpression(): ExpressionNode {
        return when {
            isCurrentTokenTypeEqualTo(TokenType.SET) -> {
                parseSetExpr()
            }
            isCurrentTokenTypeEqualTo(TokenType.PUTS) -> {
                parsePutsExpr()
            }
            isCurrentTokenTypeEqualTo(TokenType.COMMENT) -> {
                val commentToken = match(TokenType.COMMENT)!!
                CommentNode(commentToken)
            }
            isCurrentTokenTypeEqualTo(TokenType.SWITCH) -> {
                parseSwitchExpr()
            }
            isCurrentTokenTypeEqualTo(TokenType.IF) -> {
                parseIfExpr()
            }
            isCurrentTokenTypeEqualTo(TokenType.WHILE) -> {
                parseWhileExpr()
            }
            isCurrentTokenTypeEqualTo(TokenType.FOR) -> {
                parseForExpr()
            }
            isCurrentTokenTypeEqualTo(TokenType.INCR) -> {
                parseIncrExpr()
            }
            isCurrentTokenTypeEqualTo(TokenType.RETURN) -> {
                parseReturnExpr()
            }
            isCurrentTokenTypeEqualTo(TokenType.PROC) -> {
                parseProcExpr()
            }
            isCurrentTokenTypeEqualTo(tclSingleKeywordsList) -> {
                TCLKeywordsNode(match(tclSingleKeywordsList)!!)
            }
            else -> {
                throw Exception("Unknown TokenType at ${tokens[pos].pos}")
            }
        }

    }

    private fun parseProcExpr(): ExpressionNode {
        match(TokenType.PROC)!!
        removeSpaces()

        // parse name of proc
        val nameOfProc = match(TokenType.VARIABLE)?.text ?: throw Exception("Expected function name at ${tokens[pos].pos}")
        removeSpaces()

        match(TokenType.LCUR) ?: throw Exception("Expected function arguments at ${tokens[pos].pos} inside {...}")

        // parse args of proc
        val args: MutableList<VariableNode> = mutableListOf()
        while (!isCurrentTokenTypeEqualTo(TokenType.RCUR)) {
            removeSpaces()
            if (isCurrentTokenTypeEqualTo(TokenType.VARIABLE)) {
                val arg = match(TokenType.VARIABLE)!!
                args.add(VariableNode(arg))
            }
            removeSpaces()
        }
        match(TokenType.RCUR)!!

        // parse body of proc
        removeSpacesAndNewLines()
        match(TokenType.LCUR) ?: throw Exception("Expected start of proc body at ${tokens[pos].pos} inside {...}")
        removeSpacesAndNewLines()
        val body = parseBody()

        return ProcNode(functionName = StringNode(nameOfProc), args = args, body = body)
    }

    private fun parseReturnExpr(): ExpressionNode {
        match(TokenType.RETURN)!!
        removeSpaces()

        return if (isCurrentTokenTypeEqualTo(listOf(TokenType.INTEGER, TokenType.FLOAT, TokenType.STRING))) {
            val returnValue = match(listOf(TokenType.INTEGER, TokenType.FLOAT))!!
            ReturnNode(ValueNode(returnValue))
        } else if (isCurrentTokenTypeEqualTo(TokenType.LINK_VARIABLE)) {
            val returnValue = match(TokenType.LINK_VARIABLE)!!
            ReturnNode(VariableNode(returnValue))
        } else if (isCurrentTokenTypeEqualTo(listOf(TokenType.VARIABLE, TokenType.STRING))) {
            val returnValue = match(listOf(TokenType.VARIABLE, TokenType.STRING))!!.text
            ReturnNode(StringNode(returnValue))
        } else if (isCurrentTokenTypeEqualTo(TokenType.QUOT)) {
            val returnValue = parseQuotExpression()
            ReturnNode(returnValue as QuotationNodes)
        } else if (isCurrentTokenTypeEqualTo(TokenType.LSQU)) {
            val returnValue = parseSquareBracesExpression()
            ReturnNode(returnValue as SquareBracesNodes)
        } else {
            ReturnNode(null)
        }
    }

    private fun parseIncrExpr(): ExpressionNode {
        match(TokenType.INCR)!!
        removeSpaces()

        val variable = match(TokenType.VARIABLE) ?: throw Exception("Expected counter variable after incr at ${tokens[pos].pos}")
        removeSpaces()

        var value = 1;
        if (isCurrentTokenTypeEqualTo(TokenType.INTEGER)) {
            value = match(TokenType.INTEGER)!!.text.toInt()
        }

        return IncrNode(variable = VariableNode(variable = variable), value = value)
    }

    private fun parseForExpr(): ExpressionNode {
        removeSpaces()
        match(TokenType.FOR)!!
        removeSpaces()

        val forLoopNode = ForLoopNode()

        // block 1. Initialization
        match(TokenType.LCUR) ?: throw Exception("Expected initialization block at ${tokens[pos].pos}")
        removeSpacesAndNewLines()
        while(!isCurrentTokenTypeEqualTo(TokenType.RCUR)) {
            forLoopNode.addExpressionToInitBlock(parseExpression())
            removeSpacesAndNewLines()
        }
        incCurrentPos()
        removeSpacesAndNewLines()

        // block 2. Condition
        match(TokenType.LCUR) ?: throw Exception("Expected condition block at ${tokens[pos].pos}")
        removeSpacesAndNewLines()
        forLoopNode.addExpressionToConditionBlock(parseCondition())

        // block 3. Counter
        match(TokenType.LCUR) ?: throw Exception("Expected counter block at ${tokens[pos].pos}")
        removeSpacesAndNewLines()
        while(!isCurrentTokenTypeEqualTo(TokenType.RCUR)) {
            forLoopNode.addExpressionToCounterBlock(parseExpression())
            removeSpacesAndNewLines()
        }
        incCurrentPos()
        removeSpacesAndNewLines()

        // block 4. Command
        match(TokenType.LCUR) ?: throw Exception("Expected command block at ${tokens[pos].pos}")
        removeSpacesAndNewLines()
        while(!isCurrentTokenTypeEqualTo(TokenType.RCUR)) {
            forLoopNode.addExpressionToCommandBlock(parseExpression())
            removeSpacesAndNewLines()
        }
        incCurrentPos()
        removeSpacesAndNewLines()

        return forLoopNode
    }

    private fun parseWhileExpr(): ExpressionNode {

        removeSpaces()
        match(TokenType.WHILE)!!
        removeSpaces()

        match(listOf(TokenType.QUOT, TokenType.LCUR)) ?: throw Exception("Expected condition at ${tokens[pos].pos}")
        removeSpaces()
        val condition = parseCondition()


        // parse body of while loop
        removeSpacesAndNewLines()
        match(TokenType.LCUR) ?: throw Exception("Expected start of if body at ${tokens[pos].pos}")
        removeSpacesAndNewLines()
        val body = parseBody()

        return WhileLoopNode(condition = condition, body = body)
    }

    private fun parseIfExpr(): ExpressionNode {
        val ifNode = IfNode()

        match(TokenType.IF)!!
        removeSpaces()
        val trueBranch = parseIfBranch()
        ifNode.addBranch(trueBranch)

        while (isCurrentTokenTypeEqualTo(TokenType.ELSEIF)) {
            incPosAndTrim()
            ifNode.addBranch(parseIfBranch())
        }

        if (isCurrentTokenTypeEqualTo(TokenType.ELSE)) {
            incPosAndTrim()
            ifNode.addBranch(parseIfBranch(true))
        }

        return ifNode
    }

    private fun parseIfBranch(isElseBranch: Boolean = false): IfBranch {

        var condition: ExpressionNode? = null
        if (!isElseBranch) {
            // parse condition of true branch of if
            match(listOf(TokenType.QUOT, TokenType.LCUR)) ?: throw Exception("Expected condition at ${tokens[pos].pos}")
            removeSpaces()
            condition = parseCondition()
        }

        // parse body of true branch of if
        removeSpacesAndNewLines()
        // todo (скобки не обязательны, надо поправить)
        match(TokenType.LCUR) ?: throw Exception("Expected start of if body at ${tokens[pos].pos}")

        removeSpacesAndNewLines()
        val body = parseBody()

        return IfBranch(condition = condition, body = body)
    }

    private fun parseBody(): ExpressionNode {
        val body = CurlyBracesNodes()

        while (true) {
            body.addNode(parseExpression())

            removeSpacesAndNewLines()
            if (isCurrentTokenTypeEqualTo(TokenType.RCUR)) {
                incPosAndTrim()
                break
            }
        }

        return body
    }

    /**
     * Case 1. link variable
     * Case 2. number
     * Case 3. expr
     */
    private fun parseCondition(): ExpressionNode {

        val bracesNodes = BracesNodes()

        while (!isCurrentTokenTypeEqualTo(listOf(TokenType.RCUR, TokenType.QUOT))) {
            val expression = parsLogicalConditions()
            expression?.let { bracesNodes.addNode(it) }
        }

        removeSpacesAndNewLines()
        match(listOf(TokenType.RCUR, TokenType.QUOT))!!
        removeSpacesAndNewLines()

        return bracesNodes
    }

    private fun parsLogicalConditions(): ExpressionNode? {
        return when {
            isCurrentTokenTypeEqualTo(TokenType.INTEGER) -> {
                val integerToken = match(TokenType.INTEGER)!!
                ValueNode(integerToken)
            }
            isCurrentTokenTypeEqualTo(TokenType.FLOAT) -> {
                val floatToken = match(TokenType.FLOAT)!!
                ValueNode(floatToken)
            }
            isCurrentTokenTypeEqualTo(listOf(TokenType.TRUE, TokenType.FALSE)) -> {
                val boolean = match(listOf(TokenType.TRUE, TokenType.FALSE))!!
                ValueNode(boolean)
            }
            isCurrentTokenTypeEqualTo(TokenType.LINK_VARIABLE) -> {
                val linkVariable = match(TokenType.LINK_VARIABLE)!!
                VariableNode(linkVariable)
            }
            isCurrentTokenTypeEqualTo(operationsList) -> {
                val operationNode = match(operationsList)!!
                OperationNode(operationNode)
            }
            isCurrentTokenTypeEqualTo(TokenType.LPAR) -> {
                incCurrentPos()
                val bracesNodes = BracesNodes()
                while (!isCurrentTokenTypeEqualTo(TokenType.RPAR)) {
                    val expression = parsLogicalConditions()
                    expression?.let { bracesNodes.addNode(it) }
                }

                incCurrentPos()
                return if (bracesNodes.nodes.isEmpty()) {
                    null
                } else {
                    bracesNodes
                }
            }
            isCurrentTokenTypeEqualTo(listOf(TokenType.SPACE)) -> {
                incCurrentPos()
                null
            }
            else -> throw Exception("Unknown TokenType at ${tokens[pos].pos}")
        }
    }

    /**
     * Case 1. [expr 1 + 2 * 3]
     * Case 2. [expr (1 + 2) * 3]
     * Case 3. [expr log(30)]
     * Case 4. [expr $a + $b]
     * Case 5. [expr "$a + $b"]
     * Case 6. [expr {$a + $b}]
     */
    private fun parseExprFormula(): ExpressionNode? {
        return when {
            isCurrentTokenTypeEqualTo(TokenType.INTEGER) -> {
                val integerToken = match(TokenType.INTEGER)!!
                ValueNode(integerToken)
            }
            isCurrentTokenTypeEqualTo(TokenType.FLOAT) -> {
                val floatToken = match(TokenType.FLOAT)!!
                ValueNode(floatToken)
            }
            isCurrentTokenTypeEqualTo(TokenType.OPERATION) -> {
                val operationToken = match(TokenType.OPERATION)!!
                OperationNode(operationToken)
            }
            isCurrentTokenTypeEqualTo(TokenType.LPAR) -> {
                incCurrentPos()
                val bracesNodes = BracesNodes()
                while (!isCurrentTokenTypeEqualTo(TokenType.RPAR)) {
                    val expression = parseExprFormula()
                    expression?.let { bracesNodes.addNode(it) }
                }

                incCurrentPos()
                return if (bracesNodes.nodes.isEmpty()) {
                    null
                } else {
                    bracesNodes
                }
            }
            isCurrentTokenTypeEqualTo(TokenType.LINK_VARIABLE) -> {
                val linkVariable = match(TokenType.LINK_VARIABLE)!!
                VariableNode(linkVariable)
            }
            // todo (в зависимости от функции можно проверять тип принимаемого аргумента)
            isCurrentTokenTypeEqualTo(listOf(TokenType.SQRT)) -> {
                val mathFun = getCurrentToken()
                match(TokenType.LPAR) ?: throw Exception("Expected argument body () at $pos")
                val argument = match(TokenType.INTEGER) ?: throw Exception("Expected argument inside () at $pos")
                match(TokenType.RPAR) ?: throw Exception("Expected argument body () at $pos")

                val mathFunctionNode = MathFunctionNode(mathFun, ValueNode(argument))
                mathFunctionNode
            }
            isCurrentTokenTypeEqualTo(listOf(TokenType.QUOT, TokenType.LCUR, TokenType.RCUR, TokenType.SPACE)) -> {
                incCurrentPos()
                null
            }
            else -> throw Exception("Unknown TokenType at ${tokens[pos].pos}")
        }

    }

    /**
     * Grammar
     * Case 1: No replacement is made inside the curly brackets
     */
    private fun parseCurlyBracesExpression(): ExpressionNode {
        match(TokenType.LCUR)!!

        val curlyBracesNode = CurlyBracesNodes()
        val stringNode = StringNode()

        while (pos < tokens.size) {
            if (isCurrentTokenTypeEqualTo(TokenType.RCUR)) {
                incCurrentPos()
                if (stringNode.string.isNotEmpty()) {
                    // finish forming string Node
                    curlyBracesNode.addNode(stringNode)
                }
                return curlyBracesNode
            }

            val token = getCurrentToken()
            stringNode.join(token.text)
        }

        throw Exception("Missing closing }")
    }

    private fun parseSquareBracesExpression(): ExpressionNode {
        match(TokenType.LSQU)!!
        removeSpaces()

        val squareBracesNode = SquareBracesNodes()
        when {
            isCurrentTokenTypeEqualTo(TokenType.SET) -> {
                val expression = parseSetExpr()
                squareBracesNode.addNode(expression)
            }
            isCurrentTokenTypeEqualTo(TokenType.PUTS) -> {
                val expression = parsePutsExpr()
                squareBracesNode.addNode(expression)
            }
            isCurrentTokenTypeEqualTo(TokenType.EXPR) -> {
                val expression = parseExpr()
                squareBracesNode.addNode(expression)
            }
            else -> {
                throw Exception("Unknown TokenType at ${tokens[pos].pos}")
            }
        }

        removeSpaces()
        incCurrentPos()

        return squareBracesNode
    }

    /**
     * Grammar
     * Case 1: Variable (which corresponds to string variable)
     * Case 2: Space character (we must consider all space related characters while concatenating string)
     * Case 3: Semicolon character (we must consider \n character while concatenating string)
     * Case 4: Cancel symbol
     * Case 5: Link variable
     * Case 6: Internal curly braces expression
     * Case 7: Internal square braces expression
     * Case 8: Right curly brace (in case that we used cancel symbol on left curly brace)
     * Case 9: Right square brace (in case that we used cancel symbol on left square brace)
     */
    private fun parseQuotExpression(): ExpressionNode {
        match(TokenType.QUOT)!!

        val quotationsNode = QuotationNodes()
        var stringNode = StringNode()

        while (pos < tokens.size) {
            when {
                isCurrentTokenTypeEqualTo(TokenType.VARIABLE) -> {
                    val string = match(TokenType.VARIABLE)!!
                    stringNode.join(string.text)
                }
                isCurrentTokenTypeEqualTo(TokenType.SPACE) -> {
                    val space = match(TokenType.SPACE)!!
                    stringNode.join(space.text)
                }
                isCurrentTokenTypeEqualTo(TokenType.SEMICOLON) -> {
                    val semicolon = match(TokenType.SEMICOLON)!!
                    stringNode.join(semicolon.text)
                }
                isCurrentTokenTypeEqualTo(TokenType.CANCEL_SYMBOL) -> {
                    incCurrentPos()
                }
                // If /$a then substitution is canceled otherwise we return variable node
                isCurrentTokenTypeEqualTo(TokenType.LINK_VARIABLE) -> {
                    val linkVariable = peekCurrentToken()
                    val isCancelSymbolSet = checkIfCancelSymbolSet()
                    incCurrentPos()
                    if (isCancelSymbolSet) {
                        stringNode.join(linkVariable.text)
                    } else {

                        if (stringNode.string.isNotEmpty()) {
                            // finish forming string Node
                            quotationsNode.addNode(stringNode)
                            stringNode = StringNode()
                        }

                        quotationsNode.addNode(VariableNode(linkVariable))
                    }
                }
                isCurrentTokenTypeEqualTo(TokenType.LCUR) -> {
                    val leftCurlyBrace = match(TokenType.LCUR)!!
                    stringNode.join(leftCurlyBrace.text)
                }
                isCurrentTokenTypeEqualTo(TokenType.LSQU) -> {
                    val leftSquareBrace = peekCurrentToken()

                    val isCancelSymbolSet = checkIfCancelSymbolSet()
                    if (isCancelSymbolSet) {
                        stringNode.join(leftSquareBrace.text)
                        incCurrentPos()
                    } else {

                        if (stringNode.string.isNotEmpty()) {
                            // finish forming string Node
                            quotationsNode.addNode(stringNode)
                            stringNode = StringNode()
                        }

                        val squareBracketsExpression = parseSquareBracesExpression()
                        quotationsNode.addNode(squareBracketsExpression)
                    }
                }
                isCurrentTokenTypeEqualTo(TokenType.RCUR) -> {
                    val rightCurlyBrace = match(TokenType.RCUR)!!
                    stringNode.join(rightCurlyBrace.text)
                }
                isCurrentTokenTypeEqualTo(TokenType.RSQU) -> {
                    val rightSquareBrace = match(TokenType.RSQU)!!
                    stringNode.join(rightSquareBrace.text)
                }
                // if we reach the end of equation, we simply return quotationsNode
                isCurrentTokenTypeEqualTo(TokenType.QUOT) -> {
                    incCurrentPos()
                    if (stringNode.string.isNotEmpty()) {
                        // finish forming string Node
                        quotationsNode.addNode(stringNode)
                    }
                    return quotationsNode
                }
                else -> {
                    val token = getCurrentToken()
                    stringNode.join(token.text)
                }
            }
        }

        throw Exception("Missing closing \"")
    }

    private fun parseSwitchExpr(): ExpressionNode {
        removeSpaces()
        match(TokenType.SWITCH)!!

        removeSpaces()
        val string = match(TokenType.LINK_VARIABLE)!!

        removeSpaces()
        var isSubstitutionsAllowed = false
        if (isCurrentTokenTypeEqualTo(TokenType.CANCEL_SYMBOL)) {
            isSubstitutionsAllowed = true
            incCurrentPos()
        } else if (isCurrentTokenTypeEqualTo(TokenType.LCUR)) {
            incCurrentPos()
        }

        removeSpaces()
        while (isCurrentTokenTypeEqualTo(TokenType.SEMICOLON)) {
            incCurrentPos()
        }
        removeSpaces()

        val cases: MutableList<SwitchCase> = mutableListOf()

        do {
            val switchCase = parseSwitchCase()
            switchCase?.let { cases.add(it) }
            if (switchCase?.value?.type == TokenType.DEFAULT || pos == tokens.size) {
                break
            }
        } while (switchCase != null)

        return SwitchNode(string = string, cases = cases, isSubstitutionsAllowed = isSubstitutionsAllowed)
    }

    /**
     * Grammar
     * Case 1. Value can be LINK_VARIABLE: switch $x "$a" ...
     * Case 2. Value can be VARIABLE (Number) : switch $x "one" ...
     * Case 3. Value can be DEFAULT: switch $x "default" ...
     */
    private fun parseSwitchCase(): SwitchCase? {
        removeSpaces()
        if (!isCurrentTokenTypeEqualTo(TokenType.QUOT)) {
            return null
        }
        match(TokenType.QUOT)!!
        removeSpaces()

        val value = if (isCurrentTokenTypeEqualTo(TokenType.VARIABLE)) {
            val token = match(TokenType.VARIABLE)!!
            Token(type = TokenType.STRING, token.text, token.pos)
        } else if (isCurrentTokenTypeEqualTo(TokenType.LINK_VARIABLE)) {
            match(TokenType.LINK_VARIABLE)!!
        } else if (isCurrentTokenTypeEqualTo(TokenType.DEFAULT)) {
            match(TokenType.DEFAULT)!!
        } else {
            throw Exception("Switch case: unknown token at $pos")
        }

        removeSpaces()
        match(TokenType.QUOT)!! // closing " of value
        removeSpaces()

        if (!isCurrentTokenTypeEqualTo(listOf(TokenType.QUOT, TokenType.LCUR))) {
            throw Exception("Switch case: expected body of case at $pos")
        }

        val body = CurlyBracesNodes()

        if (isCurrentTokenTypeEqualTo(TokenType.QUOT)) {
            incPosAndTrim()
            body.addNode(parseExpression())
            incPosAndTrim()
        } else if (isCurrentTokenTypeEqualTo(TokenType.LCUR)) {
            incPosAndTrim()
            while (true) {
                body.addNode(parseExpression())

                removeSpaces()
                if (isCurrentTokenTypeEqualTo(TokenType.RCUR)) {
                    incPosAndTrim()
                    break
                }
                incCurrentPos()
                removeSpaces()
            }
        } else {
            throw Exception("Expected start of switch case")
        }

        if (isCurrentTokenTypeEqualTo(TokenType.CANCEL_SYMBOL)) {
            incCurrentPos()
        }
        removeSpaces()

        while (isCurrentTokenTypeEqualTo(TokenType.SEMICOLON)) {
            incCurrentPos()
        }
        removeSpaces()

        if (isCurrentTokenTypeEqualTo(TokenType.RCUR)) {
            incCurrentPos()
        }
        removeSpaces()

        return SwitchCase(value = value, body = body)
    }

    private fun parseExpr(): ExpressionNode {
        removeSpaces()
        val exprOperator = match(TokenType.EXPR)!!

        val mathExpNode = MathExpNodes()
        while (!isCurrentTokenTypeEqualTo(TokenType.RSQU)) {
            if (pos == tokens.size - 1) {
                throw Exception("Missing closing ]")
            }

            val node = parseExprFormula()
            node?.let { mathExpNode.addNode(it) }
        }

        if (mathExpNode.nodes.isEmpty()) {
            throw Exception("Missing expression body")
        }
        // val rightFormulaNode = parseExprFormula()

        return UnarOperationNode(exprOperator, mathExpNode)
    }

    private fun parseSetOrPutsRightFormula(): ExpressionNode {
        return when {
            // it corresponds to single string like "set a hello;"
            isCurrentTokenTypeEqualTo(TokenType.VARIABLE) -> {
                val variable = match(TokenType.VARIABLE)!!
                val stringToken = variable.convertTo(TokenType.STRING)
                ValueNode(stringToken)
            }
            isCurrentTokenTypeEqualTo(TokenType.INTEGER) -> {
                val integerToken = match(TokenType.INTEGER)!!
                ValueNode(integerToken)
            }
            isCurrentTokenTypeEqualTo(TokenType.FLOAT) -> {
                val floatToken = match(TokenType.FLOAT)!!
                ValueNode(floatToken)
            }
            isCurrentTokenTypeEqualTo(TokenType.QUOT) -> {
                parseQuotExpression()
            }
            isCurrentTokenTypeEqualTo(TokenType.LSQU) -> {
                parseSquareBracesExpression()
            }
            isCurrentTokenTypeEqualTo(TokenType.LCUR) -> {
                parseCurlyBracesExpression()
            }
            else -> throw Exception("Unknown TokenType at ${tokens[pos].pos}")
        }
    }

    private fun parsePutsExpr(): ExpressionNode {
        removeSpaces()
        val putsOperator = match(TokenType.PUTS)!!

        removeSpaces()
        val rightFormulaNode = parseSetOrPutsRightFormula()

        return UnarOperationNode(putsOperator, rightFormulaNode)
    }

    private fun parseSetExpr(): ExpressionNode {
        val setOperator = match(TokenType.SET)!!
        removeSpaces()

        val variable = match(TokenType.VARIABLE) ?: throw Exception("The variable was expected at ${tokens[pos].pos} position")
        removeSpaces()

        val rightFormulaNode = parseSetOrPutsRightFormula()

        return BinOperationNode(operator = setOperator, whomAssign = VariableNode(variable), whatAssign = rightFormulaNode)
    }

    /**
     * Используется если мы в конце ожидаем; или если у нас есть '(' значит мы ожидаем ')'
     */
    private fun require(tokenTypes: List<TokenType>): Token {
        tokenTypes.forEach { tokenType ->
            val token = match(tokenType)
            token?.let { return it }
        }

        val expectedTokens = tokenTypes.joinToString("or ") { it.label }
        throw Exception("on position $pos expected $expectedTokens")
    }

    /**
     * По текущей позиции возвращает токен из списка и сдвигает на один текущую позицию
     */
    private fun match(tokenType: TokenType): Token? {
        if (pos < tokens.size) {
            val currentToken = tokens[pos]
            if (tokenType == currentToken.type) {
                pos += 1
                return currentToken
            }
        }
        return null
    }

    private fun match(tokenType: List<TokenType>): Token? {
        if (pos < tokens.size) {
            val currentToken = tokens[pos]
            val isFound = tokenType.find { it == currentToken.type }
            isFound?.let {
                pos += 1
                return currentToken
            }
        }
        return null
    }

    /**
     * Сравнивает текущий тип токена и tokenType
     */
    private fun isCurrentTokenTypeEqualTo(tokenType: TokenType): Boolean {
        if (pos < tokens.size) {
            val currentToken = tokens[pos]
            if (tokenType == currentToken.type) {
                return true
            }
        }
        return false
    }

    /**
     * Сравнивает текущий тип токена и список tokenType
     */
    private fun isCurrentTokenTypeEqualTo(tokenType: List<TokenType>): Boolean {
        if (pos < tokens.size) {
            val currentToken = tokens[pos]
            val isFound = tokenType.find { it == currentToken.type }
            isFound?.let { return true }
        }
        return false
    }

    private fun checkIfCancelSymbolSet(): Boolean {
        pos -= 1
        if (pos < 0) return false
        val cancelSymbol = isCurrentTokenTypeEqualTo(TokenType.CANCEL_SYMBOL)
        pos += 1
        return cancelSymbol
    }

    /**
     * Получить текущий токен по текущей позиции и увеличить позицию на 1
     */
    private fun getCurrentToken(): Token {
        return tokens[pos++]
    }

    private fun peekCurrentToken(): Token {
        return tokens[pos]
    }

    /**
     * Сдвигает текущую позицию в списке токенов на один
     */
    private fun incCurrentPos() {
        pos++
    }

    /**
     * Сдвигает текущую позицию в списке токенов на один пока текущий токен это Space
     */
    private fun removeSpaces() {
        while (isCurrentTokenTypeEqualTo(TokenType.SPACE)) {
            incCurrentPos()
        }
    }

    private fun removeSpacesAndNewLines() {
        while (isCurrentTokenTypeEqualTo(listOf(TokenType.SPACE, TokenType.SEMICOLON))) {
            incCurrentPos()
        }
    }

    private fun incPosAndTrim() {
        removeSpacesAndNewLines()
        incCurrentPos()
        removeSpacesAndNewLines()
    }

    companion object {
        val operationsList = listOf(
            TokenType.IS_EQUAL,
            TokenType.IS_NOT_EQUAL,
            TokenType.AND,
            TokenType.OR,
            TokenType.GREATER_OR_EQUAL,
            TokenType.LESS_OR_EQUAL,
            TokenType.LESS,
            TokenType.GREATER
        )

        val tclSingleKeywordsList = listOf(
            TokenType.CONTINUE,
            TokenType.BREAK
        )
    }
}