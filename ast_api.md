## Rules
* In switch case must be default branch
* In the end of the code must be ; or new line

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


## Values

**ValueNode**
* `value: Token`
  * Integer
  * Float
  * String

**CommentNode**
* `value: Token`
  * TokenType.COMMENT


## Commands
**puts**
* ValueNode
* SquareBracesNodes
* CurlyBracesNodes
* QuotationNodes

## Operations

**UnarOperationNode**
* `operator: Token`
* `operand: ExpressionNode`

Ex: puts

**BinOperationNode**
* `operator: Token`
* `whomAssign: VariableNode`
* `whatAssign: ExpressionNode`

Ex: set


## Functions

**ProcNode**
* `functionName: StringNode`
* `args: List<VariableNode>`
* `body: ExpressionNode`
  * SquareBracesNodes


**ReturnNode**
* `returnValue: ExpressionNode?`
  * ValueNode
  * VariableNode
  * StringNode
  * SquareBracesNodes
  * null - means the return value is nothing.