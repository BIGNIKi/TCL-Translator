## Commands


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


**RetrunNode**
* `returnValue: ExpressionNode?`
  * ValueNode
  * VariableNode
  * StringNode
  * SquareBracesNodes
  * null - means the return value is nothing.