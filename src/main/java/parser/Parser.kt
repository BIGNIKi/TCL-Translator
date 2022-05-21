package parser

import ast.*
import lexer.Token
import lexer.TokenType
import lexer.convertTo

class Parser(private val tokens: List<Token>) {
    var pos: Int = 0
    val variablesScope: HashMap<String, Any> = hashMapOf()
    val proceduresScope: HashMap<String, Int> = hashMapOf()

    fun parseCode(): ExpressionNode {
        val root = StatementsNode()
        while (pos < tokens.size) {
            val codeString = parseExpression() // отдельно взятая строка кода
            removeSpacesAndNewLines()
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
            isCurrentTokenTypeEqualTo(TokenType.EXPR) -> {
                parseExpr()
            }
            isCurrentTokenTypeEqualTo(TokenType.APPLY) -> {
                parseApplyExpr()
            }
            isCurrentTokenTypeEqualTo(tclSingleKeywordsList) -> {
                TCLKeywordsNode(match(tclSingleKeywordsList)!!)
            }
            // it corresponds to defined recently function
            isCurrentTokenTypeEqualTo(TokenType.VARIABLE) -> {
                parseProcCallExpr()
            }
            else -> {
                throw Exception("Unknown TokenType at ${tokens[pos].pos}")
            }
        }
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
     * Grammar
     * Case 1: No replacement is made inside the curly brackets. Inteprite as String
     * Case 2. Lambda expression. {arg1 arg2 ... argN {command} }
     */
    private fun parseCurlyBracesExpression(): ExpressionNode {
        match(TokenType.LCUR)!!

        val posBeforeParse = pos
        val exprAsString = parseCurlyBracesExpressionAsString()
        val posAfterParse = pos

        // parse second time, trying to identify lambda expression
        pos = posBeforeParse
        return try {
            val lambdaNode = parseLambdaExpression()
            pos = posAfterParse
            lambdaNode.addStringRepresentation(exprAsString.getFirstNode() as StringNode)
            lambdaNode
        } catch (e: Exception) {
            pos = posAfterParse
            exprAsString
        }
    }

    private fun parseCurlyBracesExpressionAsString(): CurlyBracesNodes {
        val curlyBracesNode = CurlyBracesNodes()
        val stringNode = StringNode()

        var rcurCounter = 1
        while (pos < tokens.size) {
            if (isCurrentTokenTypeEqualTo(TokenType.RCUR) && rcurCounter == 1) {
                incCurrentPos()
                if (stringNode.string.isNotEmpty()) {
                    // finish forming string Node
                    curlyBracesNode.addNode(stringNode)
                }
                return curlyBracesNode
            }

            val token = getCurrentToken()
            stringNode.join(token.text)

            if (token.type == TokenType.LCUR) { rcurCounter++ }
            else if (token.type == TokenType.RCUR) { rcurCounter-- }
        }

        throw Exception("Missing closing }")
    }

    private fun parseSquareBracesExpression(): ExpressionNode {
        match(TokenType.LSQU)!!
        removeSpaces()

        val squareBracesNode = SquareBracesNodes()
        squareBracesNode.addNode(parseExpression())

        removeSpacesAndNewLines()
        match(TokenType.RSQU) ?: throw Exception("Expected closing ] at ${tokens[pos].pos}")

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

    /**
     * Case 1. apply $lambda val1 val2 valN
     * Case 2. apply {{val1 val2 valN}{body}} val1 val2 valN
     */
    private fun parseApplyExpr(): ExpressionNode {
        match(TokenType.APPLY)!!
        val applyNode = ApplyNode()

        // parse lambda
        removeSpaces()
        when {
            isCurrentTokenTypeEqualTo(TokenType.LINK_VARIABLE) -> {
                val lambda = match(TokenType.LINK_VARIABLE)!!
                applyNode.lambdaExpr = VariableNode(lambda)
            }
            isCurrentTokenTypeEqualTo(TokenType.LCUR) -> {
                match(TokenType.LCUR)!!
                applyNode.lambdaExpr = parseLambdaExpression()
            }
            else -> throw Exception("Unknown TokenType in apply expression at ${tokens[pos].pos}")
        }

        // parse args
        removeSpaces()
        while (true) {
            when {
                isCurrentTokenTypeEqualTo(listOf(TokenType.INTEGER, TokenType.FLOAT, TokenType.STRING)) -> {
                    val arg = match(listOf(TokenType.INTEGER, TokenType.FLOAT, TokenType.STRING))!!
                    applyNode.args.add(ValueNode(arg))
                }
                isCurrentTokenTypeEqualTo(TokenType.VARIABLE) -> {
                    val arg = match(TokenType.VARIABLE)!!
                    val stringNode = arg.convertTo(TokenType.STRING)
                    applyNode.args.add(ValueNode(stringNode))
                }
                isCurrentTokenTypeEqualTo(TokenType.LINK_VARIABLE) -> {
                    val arg = match(TokenType.LINK_VARIABLE)!!
                    applyNode.args.add(VariableNode(arg))
                }
                isCurrentTokenTypeEqualTo(TokenType.SPACE) -> { incCurrentPos() }
                else -> break
            }
        }

        if (pos != tokens.size) {
            removeSpacesAndNewLines()
        }

        return applyNode
    }

    /**
     * lambda { {arg1 arg2 argN} { body } }
     */
    private fun parseLambdaExpression(): LambdaExprNode {

        // parse args of lambda
        match(TokenType.LCUR) ?: throw Exception("Expected args of lambda at ${tokens[pos].pos}")

        val args: MutableList<VariableNode> = mutableListOf()
        while (!isCurrentTokenTypeEqualTo(TokenType.RCUR)) {
            removeSpaces()
            if (isCurrentTokenTypeEqualTo(TokenType.VARIABLE)) {
                val arg = match(TokenType.VARIABLE)!!
                args.add(VariableNode(arg))
                removeSpaces()
            } else {
                throw Exception("Expected arg token type at ${tokens[pos].pos}")
            }
        }

        match(TokenType.RCUR)!!

        // parse body of lambda
        removeSpaces()
        match(TokenType.LCUR) ?: throw Exception("Expected start of lambda body at ${tokens[pos].pos}")
        try {
            val body = parseBody()
            match(TokenType.RCUR) ?: throw Exception("Expected } in lambda expr at ${tokens[pos].pos}")
            removeSpaces()
            return LambdaExprNode(args = args, body = body)
        } catch (e: Exception) {
            throw e
        }
    }

    private fun parseProcCallExpr(): ExpressionNode {
        val nameOfFun = match(TokenType.VARIABLE)!!.text
        removeSpaces()

        val args: MutableList<ExpressionNode> = mutableListOf()
        if (proceduresScope.containsKey(nameOfFun)) {
            repeat(proceduresScope[nameOfFun]!!) {
                when {
                    isCurrentTokenTypeEqualTo(TokenType.LCUR) -> {
                        args.add(parseCurlyBracesExpression())
                    }
                    isCurrentTokenTypeEqualTo(TokenType.QUOT) -> {
                        incCurrentPos()
                        val stringNode = StringNode()
                        while (!isCurrentTokenTypeEqualTo(TokenType.QUOT)) {
                            stringNode.join(getCurrentToken().text)
                        }
                        match(TokenType.QUOT)!!
                        removeSpacesAndNewLines()

                        args.add(stringNode)
                    }
                    isCurrentTokenTypeEqualTo(TokenType.LSQU) -> {
                        args.add(parseSquareBracesExpression())
                    }
                    isCurrentTokenTypeEqualTo(listOf(TokenType.INTEGER, TokenType.FLOAT, TokenType.STRING)) -> {
                        val arg = match(listOf(TokenType.INTEGER, TokenType.FLOAT, TokenType.STRING))!!
                        args.add(ValueNode(arg))
                    }
                    isCurrentTokenTypeEqualTo(TokenType.LINK_VARIABLE) -> {
                        val arg = match(TokenType.LINK_VARIABLE)!!
                        args.add(VariableNode(arg))
                    }
                }

                removeSpaces()
            }

            // if there are 0 args
            if (proceduresScope[nameOfFun]!! == 0) {
                incCurrentPos()
                removeSpaces()
                incCurrentPos()
            }
        } else {
            throw Exception("Unknown function name at ${tokens[pos].pos}")
        }

        removeSpacesAndNewLines()

        return ProcCallNode(functionName = StringNode(nameOfFun), args = args)
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

        // save info about function
        proceduresScope[nameOfProc] = args.size

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

    private fun parseForExpr(): ExpressionNode {
        match(TokenType.FOR)!!
        removeSpaces()

        val forLoopNode = ForLoopNode()

        // block 1. Initialization
        match(TokenType.LCUR) ?: throw Exception("Expected initialization block at ${tokens[pos].pos}")
        removeSpacesAndNewLines()
        while (!isCurrentTokenTypeEqualTo(TokenType.RCUR)) {
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
        while (!isCurrentTokenTypeEqualTo(TokenType.RCUR)) {
            forLoopNode.addExpressionToCounterBlock(parseExpression())
            removeSpacesAndNewLines()
        }
        incCurrentPos()
        removeSpacesAndNewLines()

        // block 4. Command
        match(TokenType.LCUR) ?: throw Exception("Expected command block at ${tokens[pos].pos}")
        removeSpacesAndNewLines()
        while (!isCurrentTokenTypeEqualTo(TokenType.RCUR)) {
            forLoopNode.addExpressionToCommandBlock(parseExpression())
            removeSpacesAndNewLines()
        }
        incCurrentPos()
        removeSpacesAndNewLines()

        return forLoopNode
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

    private fun parseWhileExpr(): ExpressionNode {
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

        // parse body of branch of if
        removeSpacesAndNewLines()
        match(TokenType.LCUR) ?: throw Exception("Expected start of if body at ${tokens[pos].pos}")

        removeSpacesAndNewLines()
        val body = parseBody()

        return IfBranch(condition = condition, body = body)
    }

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
            isCurrentTokenTypeEqualTo(TokenType.LSQU) -> {
                parseSquareBracesExpression()
            }
            isCurrentTokenTypeEqualTo(listOf(TokenType.SPACE)) -> {
                incCurrentPos()
                null
            }
            else -> throw Exception("Unknown TokenType at ${tokens[pos].pos}")
        }
    }

    private fun parseSwitchExpr(): ExpressionNode {
        match(TokenType.SWITCH)!!
        removeSpaces()

        val string = match(TokenType.LINK_VARIABLE) ?: throw Exception("Expected link variable of switch at ${tokens[pos].pos}")
        removeSpaces()

        var isSubstitutionsAllowed = false
        if (isCurrentTokenTypeEqualTo(TokenType.CANCEL_SYMBOL)) {
            isSubstitutionsAllowed = true
            incCurrentPos()
        } else if (isCurrentTokenTypeEqualTo(TokenType.LCUR)) {
            incCurrentPos()
        }
        removeSpacesAndNewLines()

        val cases: MutableList<SwitchCase> = mutableListOf()

        do {
            val switchCase = parseSwitchCase()
            switchCase?.let { cases.add(it) }
            if (switchCase?.value?.type == TokenType.DEFAULT) {
                break
            }
        } while (switchCase != null)

        if (isCurrentTokenTypeEqualTo(TokenType.RCUR)) {
            incCurrentPos()
        }

        return SwitchNode(string = string, cases = cases, isSubstitutionsAllowed = isSubstitutionsAllowed)
    }

    private fun parseSwitchCase(): SwitchCase? {
        removeSpaces()
        if (!isCurrentTokenTypeEqualTo(TokenType.QUOT)) {
            return null
        }
        match(TokenType.QUOT)!!
        removeSpaces()

        val value = if (isCurrentTokenTypeEqualTo(TokenType.VARIABLE)) {
            val token = match(TokenType.VARIABLE)!!
            token.convertTo(TokenType.STRING)
        } else if (isCurrentTokenTypeEqualTo(TokenType.LINK_VARIABLE)) {
            match(TokenType.LINK_VARIABLE)!!
        } else if (isCurrentTokenTypeEqualTo(TokenType.DEFAULT)) {
            match(TokenType.DEFAULT)!!
        } else {
            throw Exception("Switch case: unknown token at ${tokens[pos].pos}")
        }

        removeSpaces()
        match(TokenType.QUOT)!! // closing " of value
        removeSpaces()

        if (!isCurrentTokenTypeEqualTo(listOf(TokenType.QUOT, TokenType.LCUR))) {
            throw Exception("Switch case: expected body of case at ${tokens[pos].pos}")
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

                if (isCurrentTokenTypeEqualTo(TokenType.RCUR)) {
                    removeSpaces()
                    break
                }
            }
        } else {
            throw Exception("Expected start of switch case at ${tokens[pos].pos}")
        }

        if (isCurrentTokenTypeEqualTo(TokenType.CANCEL_SYMBOL)) {
            incCurrentPos()
        }

        removeSpacesAndNewLines()

        if (isCurrentTokenTypeEqualTo(TokenType.RCUR)) {
            incCurrentPos()
        }
        removeSpacesAndNewLines()

        return SwitchCase(value = value, body = body)
    }

    private fun parseExpr(): ExpressionNode {
        val exprOperator = match(TokenType.EXPR)!!
        removeSpaces()

        val mathExpNode = MathExpNodes()
        var bracesCounter = 1
        while (bracesCounter != 0) {
            if (isCurrentTokenTypeEqualTo(TokenType.SEMICOLON)) {
                incCurrentPos()
                incCurrentPos()
                break
            }

            if (isCurrentTokenTypeEqualTo(listOf(TokenType.LCUR, TokenType.LSQU))) { bracesCounter++ }
            else if (isCurrentTokenTypeEqualTo(listOf(TokenType.RCUR, TokenType.RSQU))) { bracesCounter-- }

            if (pos >= tokens.size) {
                throw Exception("Missing closing ] of math expression")
            }

            val node = parseExprFormula()
            node?.let { mathExpNode.addNode(it) }
        }
        pos--

        if (mathExpNode.nodes.isEmpty()) {
            throw Exception("Missing expression body")
        }

        return UnarOperationNode(exprOperator, mathExpNode)
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
            isCurrentTokenTypeEqualTo(mathOperationsList) -> {
                val operationToken = match(mathOperationsList)!!
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
            isCurrentTokenTypeEqualTo(mathFunctionsList) -> {
                val mathFun = getCurrentToken()

                val args = when (mathFun.type) {
                    TokenType.SQRT -> { parseMathFunArgs(1) }
                    TokenType.RAND -> { parseMathFunArgs(0) }
                    TokenType.POW -> { parseMathFunArgs(2) }
                    else -> { throw Exception("Unknown function at ${tokens[pos].pos}") }
                }

                MathFunctionNode(mathFun = mathFun, arguments = args)
            }
            isCurrentTokenTypeEqualTo(listOf(TokenType.QUOT, TokenType.LCUR, TokenType.RCUR, TokenType.SPACE, TokenType.RSQU)) -> {
                incCurrentPos()
                null
            }
            else -> throw Exception("Unknown TokenType at ${tokens[pos].pos}")
        }
    }

    private fun parseMathFunArgs(argsNumber: Int): List<ExpressionNode> {
        val args: MutableList<ExpressionNode> = mutableListOf()

        match(TokenType.LPAR) ?: throw Exception("Expected start of function's argument at ${tokens[pos].pos}")

        for (counter in argsNumber downTo 1 step 1) {
            val value = when {
                isCurrentTokenTypeEqualTo(listOf(TokenType.INTEGER, TokenType.FLOAT, TokenType.STRING)) -> {
                    val token = match(listOf(TokenType.INTEGER, TokenType.FLOAT, TokenType.STRING))!!
                    ValueNode(token)
                }
                isCurrentTokenTypeEqualTo(TokenType.LSQU) -> {
                    parseSquareBracesExpression()
                }
                else -> {
                    throw Exception("Unknown math function arg type at ${tokens[pos].pos}")
                }
            }
            args.add(value)

            removeSpaces()
            if (counter > 1) {
                match(TokenType.SYMBOL) ?: throw Exception("Expected ',' before next arg at ${tokens[pos].pos}")
            }
            removeSpaces()
        }

        match(TokenType.RPAR) ?: throw Exception("Expected end of function's argument at ${tokens[pos].pos}")

        return args
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

        removeSpacesAndNewLines()

        return UnarOperationNode(putsOperator, rightFormulaNode)
    }

    private fun parseSetExpr(): ExpressionNode {
        val setOperator = match(TokenType.SET)!!
        removeSpaces()

        val variable = match(TokenType.VARIABLE) ?: throw Exception("The variable was expected at ${tokens[pos].pos} position")
        removeSpaces()

        val rightFormulaNode = parseSetOrPutsRightFormula()

        removeSpacesAndNewLines()

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

        val mathOperationsList = listOf(
            TokenType.PLUS,
            TokenType.MINUS,
            TokenType.DIVISION,
            TokenType.MULTIPLICATION,
            TokenType.REMINDER
        )

        val mathFunctionsList = listOf(
            TokenType.SQRT,
            TokenType.RAND,
            TokenType.POW,
        )
    }
}