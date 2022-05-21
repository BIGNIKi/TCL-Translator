## Rules
* In switch case must be default branch


## Lambda
**LambdaExprNode**
* `args: List<VariableNode>`
* `body: ExpressionNode`
  * Everything? 
* `exprAsString: StringNode`

**ApplyNode**
* `lambdaExpr: ExpressionNode?`
  * VariableNode
  * LambdaExprNode
* `args: List<ExpressionNode>`
  * ValueNode
  * VariableNode


## Proc
**ReturnNode**
* `returnValue: ExpressionNode?`
  * null - corresponds to empty return value
  * ValueNode
  * VariableNode
  * StringNode
  * QuotationNodes
  * SquareBracesNodes

**ProcNode**
* `functionName: StringNode`
* `args: List<VariableNode>`
* `body: ExpressionNode`
  * Everything? 

**ProcCallNode**
* `functionName: StringNode`
* `args: List<ExpressionNode>`
  * StringNode
  * SquareBracesNodes
  * 


## For
**IncrNode**
* `variable: VariableNode`
* `value: Int`

**ForLoopNode**
* `initBlock: List<ExpressionNode>`
  * Everything? 
* `conditionsBlock: List<ExpressionNode>`
  * BracesNodes
    * ValueNode
    * VariableNode
    * OperationNode
    * BracesNodes
    * SquareBracesNodes
* `counterBlock: List<ExpressionNode>`
  * IncrNode
* `commandBlock: List<ExpressionNode>`
  * CurlyBracesNodes
    * Everything?


## While
**WhileLoopNode**
* `condition: ExpressionNode`
  * BracesNodes
    * ValueNode
    * VariableNode
    * OperationNode
    * BracesNodes
    * SquareBracesNodes
* `body: ExpressionNode`
  * CurlyBracesNodes
    * Everything?

## If
**IfNode**
* `branches: MutableList<IfBranch>`

**IfBranch**
* `condition: ExpressionNode?`
  * BracesNodes 
    * ValueNode
    * VariableNode
    * OperationNode
    * BracesNodes
    * SquareBracesNodes
* `body: ExpressionNode`
  * CurlyBracesNodes
    * Everything?

## Switch
**SwitchNode**
* `string: Token`
  * TokenType.LINK_VARIABLE
* `cases: List<SwitchCase>`
* `isSubstitutionsAllowed: Boolean`

**SwitchCase**
* `value: Token`
  * TokenType.STRING
  * TokenType.LINK_VARIABLE
  * TokenType.DEFAULT
* `body: ExpressionNode`
  * CurlyBracesNodes
  

## Expression
**MathExpNodes**
* `nodes: List<ExpressionNode>`
  * ValueNode
  * OperationNode
  * VariableNode
  * MathFunctionNode
  * BracesNodes

**MathFunctionNode**
* `mathFun: Token,`
  * TokenType.SQRT
  * TokenType.RAND
  * TokenType.POW
* `arguments: List<ExpressionNode>`
  * ValueNode
  * SquareBracesNodes

**OperationNode**
* `operation: Token`
  * TokenType.PLUS
  * TokenType.MINUS
  * TokenType.DIVISION
  * TokenType.MULTIPLICATION
  * TokenType.REMINDER

## Values
**ValueNode**
* `value: Token`
  * TokenType.INTEGER
  * TokenType.FLOAT
  * TokenType.STRING
  * TokenType.TRUE
  * TokenType.FALSE

**CommentNode**
* `value: Token`
  * TokenType.COMMENT


## Commands
**puts**
* ValueNode
* SquareBracesNodes
* CurlyBracesNodes
* QuotationNodes

**puts**
* MathExpNodes


## Operations
**UnarOperationNode**
* `operator: Token`
* `operand: ExpressionNode`

Ex: puts, expr

**BinOperationNode**
* `operator: Token`
* `whomAssign: VariableNode`
* `whatAssign: ExpressionNode`

Ex: set
