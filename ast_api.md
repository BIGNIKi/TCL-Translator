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