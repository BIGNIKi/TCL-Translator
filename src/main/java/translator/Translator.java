package translator;

import ast.*;
import javassist.*;
import lexer.Token;
import lexer.TokenType;
import translator.helpers.IntRef;

import java.util.ArrayList;
import java.util.List;

public class Translator
{
    private final CtClass cc; // некий класс

    private final ClassPool pool; // ClassPool - это хэш-таблица, в которой хранится CtClass

    private CtMethod lMain; // метод evaluate

    //private int localVars = 0; // номер следующей локальной переменной в методе evaluate
    private final List<String> _varNames = new ArrayList<>(); // имена переменных


    public Translator() throws Exception
    {
        pool = ClassPool.getDefault();
        cc = pool.get("translator.helpers.Source");
        cc.setName("TCLSource");
    }

    public void generateClass(ExpressionNode eN)
    {
        try
        {
            lMain = CtNewMethod.make("public void evaluate() throws Exception {\n}", cc); // создает метод в класс cc

            for(var node : ((StatementsNode)eN).getCodeStrings())
            {
                ProcessBlock(node);
            }

            cc.addMethod(lMain);


            CtMethod method1 = CtNewMethod.make("public static void main(String[] args){}", cc);
            method1.addLocalVariable("source", pool.get("TCLSource")); // задефайнили переменную типа LispSource
            method1.insertAfter("source = new TCLSource(); "); // создали экземпляр объекта
            //method1.insertAfter("source.initGlobals();"); // определение глобальных переменных
            method1.insertAfter("source.evaluate(); "); // вызвали метод у объекта

            cc.addMethod(method1);

            writeToFile();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        //return null;
    }

    public void BuildAndRun()
    {
        try
        {
            Class<?> clazz = cc.toClass();
            Object source = clazz.getDeclaredConstructor().newInstance();
            clazz.getDeclaredMethod("main", String[].class).invoke(source, (Object)new String[0]);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

    }

    /**
     * пишет .class файл в корневую директори
     */
    private void writeToFile()
    {
        try
        {
            cc.writeFile();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void ProcessBlock(ExpressionNode node) throws Exception
    {
        if(node instanceof UnarOperationNode) // нашли унарный оператор (напрмер puts)
        {
            UnarOperationNode uON = (UnarOperationNode) node;

            if(uON.getOperator().getType().equals(TokenType.PUTS)) // это PUTS
            {
                SolvePUTS(uON);
            }
        }
        else if(node instanceof BinOperationNode) // например set
        {
            BinOperationNode bON = (BinOperationNode) node;
            DoBinOperationNode(bON);
        }
    }

    private Object DoBinOperationNode(BinOperationNode bON) throws Exception
    {
        if(bON.getOperator().getType().equals(TokenType.SET)) // это SET
        {
            return DoSet(bON);
        }
        return null;
    }

    private Object DoSet(BinOperationNode bON) throws Exception
    {
        if(bON.getWhatAssign() instanceof QuotationNodes) // set X "text"
        {
            QuotationNodes qN = (QuotationNodes)bON.getWhatAssign();
            var ob = SolveQuatationNode(qN, bON.getWhomAssign().getVariable().getText());
            AddLocalVarIfNeeded(bON.getWhomAssign().getVariable().getText());
            String finall = bON.getWhomAssign().getVariable().getText()+"=" + ob + ";\n";
            lMain.insertAfter(finall);
            return ob.toString();
        }
        else if(bON.getWhatAssign() instanceof ValueNode) // set X 10
        {
            ValueNode vN = (ValueNode)bON.getWhatAssign();
            if(vN.getValue().getType().equals(TokenType.FLOAT))
            {
                float fl = Float.parseFloat(vN.getValue().getText());
                AddLocalVarIfNeeded(bON.getWhomAssign().getVariable().getText());
                lMain.insertAfter(bON.getWhomAssign().getVariable().getText()+"= new Float("+ fl + ")"+";\n");
                return fl;
            }
            else if(vN.getValue().getType().equals(TokenType.INTEGER))
            {
                int intulya = Integer.parseInt(vN.getValue().getText());
                AddLocalVarIfNeeded(bON.getWhomAssign().getVariable().getText());
                lMain.insertAfter(bON.getWhomAssign().getVariable().getText()+"= new Integer("+ intulya + ")"+";\n");
                return intulya;
            }
        }
        else if(bON.getWhatAssign() instanceof SquareBracesNodes) // "[]"
        {
            SquareBracesNodes sBN = (SquareBracesNodes)bON.getWhatAssign();
            SolveSquareBraces(sBN, bON.getWhomAssign().getVariable().getText()); // BLABLA
            String varName = bON.getWhomAssign().getVariable().getText();
            AddLocalVarIfNeeded(varName);
            lMain.insertAfter(varName + "= TEMP_VAR;\n");
            return null;
        }
        else if(bON.getWhatAssign() instanceof CurlyBracesNodes) // {...}
        {
            CurlyBracesNodes cBN = (CurlyBracesNodes)bON.getWhatAssign();
            if(cBN.getNodes().get(0) instanceof StringNode)
            {
                StringNode sN = (StringNode)cBN.getNodes().get(0);
                AddLocalVarIfNeeded(bON.getWhomAssign().getVariable().getText());
                lMain.insertAfter(bON.getWhomAssign().getVariable().getText()+"="+"\""+sN.getString().replace("\\", "\\\\").replace("\"", "\\\"") +"\""+";\n");
                return "\"" + sN.getString() + "\"";
            }
        }
        return null;
    }

    private void SolveSquareBraces(SquareBracesNodes sBN, String nameOfVar) throws Exception
    {
        for(ExpressionNode exN : sBN.getNodes())
        {
            if(exN instanceof BinOperationNode) // например set
            {
                BinOperationNode bOON = (BinOperationNode)exN;
                Object ob = DoBinOperationNode(bOON);
                if(nameOfVar != null)
                {
                    AddLocalVarIfNeeded(nameOfVar);
                }
                else if(bOON.getWhomAssign().getVariable().getType().equals(TokenType.VARIABLE))
                {
                    nameOfVar = bOON.getWhomAssign().getVariable().getText();
                    AddLocalVarIfNeeded(nameOfVar);
                }

                if(ob instanceof Integer)
                {
                    Integer intu = (Integer) ob;
                    lMain.insertAfter(nameOfVar+"= new Integer("+ intu + ")"+";\n");
                    lMain.addLocalVariable("TEMP_VAR", pool.get("java.lang.Object"));
                    lMain.insertAfter("TEMP_VAR = " + nameOfVar + ";\n");
                    return;
                }
                else if(ob instanceof Float)
                {
                    Float floatik = (Float) ob;
                    lMain.insertAfter(nameOfVar+"= new Float("+ floatik + ")"+";\n");
                    lMain.addLocalVariable("TEMP_VAR", pool.get("java.lang.Object"));
                    lMain.insertAfter("TEMP_VAR = " + nameOfVar + ";\n");
                    return;
                }
                else if(ob instanceof String)
                {
                    String str = (String) ob;
                    String forTest = nameOfVar+"="+str+";\n";
                    lMain.insertAfter(forTest);
                    lMain.addLocalVariable("TEMP_VAR", pool.get("java.lang.Object"));
                    lMain.insertAfter("TEMP_VAR = " + nameOfVar + ";\n");
                    return;
                }
            }
            else if(exN instanceof UnarOperationNode) // напрмер expr
            {
                UnarOperationNode uON = (UnarOperationNode)exN;

                if(uON.getOperator().getType().equals(TokenType.EXPR)) // expr
                {
                    if(uON.getOperand() instanceof MathExpNodes)
                    {
                        MathExpNodes mEN = (MathExpNodes)uON.getOperand();

                        var nodes = mEN.getNodes();
                        SolveBracesAndSquareArihmetic(nodes);
                    }
                }
                else if(uON.getOperator().getType().equals(TokenType.PUTS))
                {
                    String code = SolvePUTS(uON);
                    lMain.addLocalVariable("TEMP_VAR", pool.get("java.lang.Object")); // объявление временной переменной для расчетов
                    lMain.insertAfter("TEMP_VAR = " + code +";\n");
                }
            }
        }
    }

    private void SolveBracesAndSquareArihmetic(List<ExpressionNode> nodes) throws Exception // решает арифметические приколы
    {
        IntRef numOfUnicVar = new IntRef();
        numOfUnicVar._val = 0;
        List<ExpressionNode> dynamicNodes = new ArrayList<>(nodes);
        boolean canStop = false;
        while(!canStop)
        {
            for(int i = 0; i<dynamicNodes.size(); i++)
            {
                ExpressionNode eN = dynamicNodes.get(i);

                if(eN instanceof OperationNode)
                {
                    OperationNode oN = (OperationNode)eN;
                    if(oN.getOperation().getType().equals(TokenType.PLUS))
                    {
                        dynamicNodes = SolveSign(TokenType.PLUS, dynamicNodes, i, numOfUnicVar, "add");
                        break;
                    }
                    else if(oN.getOperation().getType().equals(TokenType.MINUS))
                    {
                        dynamicNodes = SolveSign(TokenType.MINUS, dynamicNodes, i, numOfUnicVar, "sub");
                        break;
                    }
                    else if(oN.getOperation().getType().equals(TokenType.MULTIPLICATION))
                    {
                        dynamicNodes = SolveSign(TokenType.MULTIPLICATION, dynamicNodes, i, numOfUnicVar, "mul");
                        break;
                    }
                    else if(oN.getOperation().getType().equals(TokenType.DIVISION))
                    {
                        dynamicNodes = SolveSign(TokenType.DIVISION, dynamicNodes, i, numOfUnicVar, "div");
                        break;
                    }
                    else if(oN.getOperation().getType().equals(TokenType.REMINDER))
                    {
                        dynamicNodes = SolveSign(TokenType.REMINDER, dynamicNodes, i, numOfUnicVar, "reminder");
                        break;
                    }
                }
                else if(dynamicNodes.size() == 1 && eN instanceof ValueNode)
                {
                    ValueNode vN = (ValueNode)eN;
                    String nameOfUniqVar = "UNIQ_VAR_" + numOfUnicVar._val;
                    numOfUnicVar._val++;

                    String nameOfVar = "TEMP_VAR";
                    lMain.addLocalVariable(nameOfVar, pool.get("java.lang.Object"));
                    if(vN.getValue().getType().equals(TokenType.FLOAT))
                    {
                        String newFloat = "new Float(" + vN.getValue().getText() +")";
                        lMain.insertAfter(nameOfVar + " = " + newFloat +";\n");
                    }
                    else if(vN.getValue().getType().equals(TokenType.INTEGER))
                    {
                        String newInteger = "new Integer(" + vN.getValue().getText() +")";
                        lMain.insertAfter(nameOfVar + " = " + newInteger +";\n");
                    }
                    String str = nameOfUniqVar + " = "+ nameOfVar +";\n";
                    lMain.addLocalVariable(nameOfUniqVar, pool.get("java.lang.Object"));
                    lMain.insertAfter(str);

                    Token newTok = new Token(TokenType.LINK_VARIABLE, "$"+nameOfUniqVar, 0);
                    VariableNode newNode = new VariableNode(newTok);
                    dynamicNodes.add(1, newNode);
                    dynamicNodes.remove(0);
                    canStop = true;
                    break;
                }
                else if(dynamicNodes.size() == 1 && eN instanceof VariableNode)
                {
                    canStop = true;
                    break;
                }
                else if(dynamicNodes.size() == 1 && eN instanceof MathFunctionNode)
                {
                    MathFunctionNode mFN = (MathFunctionNode)eN;
                    if(mFN.getMathFun().getType().equals(TokenType.SQRT))
                    {
                        MakeArguments(mFN.getArguments());
                        String nameOfUniqVar = "UNIQ_VAR_" + numOfUnicVar._val;
                        numOfUnicVar._val++;
                        String nameOfVar = "ARG_0";
                        String str = nameOfUniqVar + " = sqrt("+ nameOfVar +");\n";
                        lMain.addLocalVariable(nameOfUniqVar, pool.get("java.lang.Object"));
                        lMain.insertAfter(str);

                        Token newTok = new Token(TokenType.LINK_VARIABLE, "$"+nameOfUniqVar, 0);
                        VariableNode newNode = new VariableNode(newTok);
                        dynamicNodes.add(1, newNode);
                        dynamicNodes.remove(0);
                        canStop = true;
                        break;
                    }
                    else if(mFN.getMathFun().getType().equals(TokenType.POW))
                    {
                        MakeArguments(mFN.getArguments());
                        String nameOfUniqVar = "UNIQ_VAR_" + numOfUnicVar._val;
                        numOfUnicVar._val++;
                        String nameOfVar = "ARG_0";
                        String nameOfVar1 = "ARG_1";
                        String str = nameOfUniqVar + " = pow("+ nameOfVar +","+ nameOfVar1 +");\n";
                        lMain.addLocalVariable(nameOfUniqVar, pool.get("java.lang.Object"));
                        lMain.insertAfter(str);

                        Token newTok = new Token(TokenType.LINK_VARIABLE, "$"+nameOfUniqVar, 0);
                        VariableNode newNode = new VariableNode(newTok);
                        dynamicNodes.add(1, newNode);
                        dynamicNodes.remove(0);
                        canStop = true;
                        break;
                    }
                    else if(mFN.getMathFun().getType().equals(TokenType.RAND))
                    {
                        String nameOfUniqVar = "UNIQ_VAR_" + numOfUnicVar._val;
                        numOfUnicVar._val++;
                        String str = nameOfUniqVar + " = rand();\n";
                        lMain.addLocalVariable(nameOfUniqVar, pool.get("java.lang.Object"));
                        lMain.insertAfter(str);

                        Token newTok = new Token(TokenType.LINK_VARIABLE, "$"+nameOfUniqVar, 0);
                        VariableNode newNode = new VariableNode(newTok);
                        dynamicNodes.add(1, newNode);
                        dynamicNodes.remove(0);
                        canStop = true;
                        break;
                    }
                }
            }
        }
        VariableNode vN = (VariableNode)dynamicNodes.get(0);
        lMain.addLocalVariable("TEMP_VAR", pool.get("java.lang.Object"));
        String str = "TEMP_VAR = " + vN.getVariable().getText().substring(1) + ";\n";
        lMain.insertAfter(str);
    }

    private void AddSolveForMathFunc()
    {
        
    }

    // для выбранного знака подставляет переменные
    private List<ExpressionNode> SolveSign(TokenType tT, List<ExpressionNode> dynamicNodes, int id, IntRef numOfUnicVar, String nameOfFunc) throws Exception
    {
        ExpressionNode firstNode = dynamicNodes.get(id-2);
        MakeArgumentForExpression(firstNode, "0");
        String nameFirstVar = "ARGUM_"+"0";
        ExpressionNode secondNode = dynamicNodes.get(id-1);
        MakeArgumentForExpression(secondNode, "1");
        String nameSecondVar = "ARGUM_"+"1";

        String nameOfUniqVar = "UNIQ_VAR_" + numOfUnicVar._val;
        numOfUnicVar._val++;

        //String str = nameOfUniqVar + " = add("+ nameFirstVar + "," + nameSecondVar + ")"+";\n";
        String str = nameOfUniqVar + " = " + nameOfFunc + "("+ nameFirstVar + "," + nameSecondVar + ")"+";\n";
        lMain.addLocalVariable(nameOfUniqVar, pool.get("java.lang.Object"));
        lMain.insertAfter(str);
        dynamicNodes = RemakeOperationList(dynamicNodes, id, tT, nameOfUniqVar);
        return dynamicNodes;
    }

    // создает переменные для аргументов в вызове функций
    private void MakeArguments(List<ExpressionNode> nodes) throws Exception
    {
        for(int i = 0; i<nodes.size(); i++)
        {
            String nameOfVar = "ARG_" + i;
            lMain.addLocalVariable(nameOfVar, pool.get("java.lang.Object"));
            ExpressionNode eN = nodes.get(i);
            if(eN instanceof ValueNode)
            {
                ValueNode vN = (ValueNode)eN;
                if(vN.getValue().getType().equals(TokenType.FLOAT))
                {
                    String newFloat = "new Float(" + vN.getValue().getText() +")";
                    lMain.insertAfter(nameOfVar + " = " + newFloat +";\n");
                }
                else if(vN.getValue().getType().equals(TokenType.INTEGER))
                {
                    String newInteger = "new Integer(" + vN.getValue().getText() +")";
                    lMain.insertAfter(nameOfVar + " = " + newInteger +";\n");
                }
            }
            else if(eN instanceof VariableNode)
            {
                VariableNode vN = (VariableNode)eN;
                lMain.insertAfter(nameOfVar + " = " + vN.getVariable().getText().substring(1) +";\n");
            }
        }
    }

    private List<ExpressionNode> RemakeOperationList(List<ExpressionNode> dynamicNodes, int fromWhichPosition, TokenType operator, String nameOfVar)
    {
        if(operator.equals(TokenType.PLUS) || operator.equals(TokenType.MINUS)
                || operator.equals(TokenType.MULTIPLICATION) || operator.equals(TokenType.DIVISION)
                || operator.equals(TokenType.REMINDER))
        {
            Token newTok = new Token(TokenType.LINK_VARIABLE, "$"+nameOfVar, 0);
            VariableNode newNode = new VariableNode(newTok);
            dynamicNodes.add(fromWhichPosition+1, newNode);
            dynamicNodes.remove(fromWhichPosition);
            dynamicNodes.remove(fromWhichPosition-1);
            dynamicNodes.remove(fromWhichPosition-2);
            return dynamicNodes;
        }
        return null;
    }

    private void MakeArgumentForExpression(ExpressionNode node, String argNum) throws Exception
    {
        if(node instanceof ValueNode) // число
        {
            ValueNode vN = (ValueNode)node;
            String nameOfVar = "ARGUM_"+argNum;
            lMain.addLocalVariable(nameOfVar, pool.get("java.lang.Object"));
            if(vN.getValue().getType().equals(TokenType.FLOAT))
            {
                String newFloat = "new Float(" + vN.getValue().getText() +")";
                lMain.insertAfter(nameOfVar + " = " + newFloat +";\n");
            }
            else if(vN.getValue().getType().equals(TokenType.INTEGER))
            {
                String newInteger = "new Integer(" + vN.getValue().getText() +")";
                lMain.insertAfter(nameOfVar + " = " + newInteger +";\n");
            }
        }
        else if(node instanceof VariableNode)
        {
            VariableNode vN = (VariableNode)node;
            String nameOfVar = "ARGUM_"+argNum;
            lMain.addLocalVariable(nameOfVar, pool.get("java.lang.Object"));
            lMain.insertAfter(nameOfVar + " = " + vN.getVariable().getText().substring(1) +";\n");
        }
        else if(node instanceof MathFunctionNode)
        {
            MathFunctionNode mFN = (MathFunctionNode)node;
            if(mFN.getMathFun().getType().equals(TokenType.SQRT))
            {
                String nameOfVar = "ARGUM_"+argNum;
                //SolveBracesAndSquareArihmetic(mFN.getArguments());
                MakeArguments(mFN.getArguments());
                String str = nameOfVar + " = sqrt("+ "ARG_0" +");\n";
                lMain.addLocalVariable(nameOfVar, pool.get("java.lang.Object"));
                lMain.insertAfter(str);
            }
            else if(mFN.getMathFun().getType().equals(TokenType.POW))
            {
                String nameOfVar = "ARGUM_"+argNum;
                //SolveBracesAndSquareArihmetic(mFN.getArguments());
                MakeArguments(mFN.getArguments());
                String str = nameOfVar + " = pow("+ "ARG_0" + "," + "ARG_1" +");\n";
                lMain.addLocalVariable(nameOfVar, pool.get("java.lang.Object"));
                lMain.insertAfter(str);
            }
            else if(mFN.getMathFun().getType().equals(TokenType.RAND))
            {
                String nameOfVar = "ARGUM_"+argNum;
                String str = nameOfVar + " = rand();\n";
                lMain.addLocalVariable(nameOfVar, pool.get("java.lang.Object"));
                lMain.insertAfter(str);
            }
        }
    }

    private StringBuilder SolveQuatationNode(QuotationNodes qN, String nameOfVar) throws Exception
    {
        StringBuilder sB = new StringBuilder();
        boolean isFirstIteration = true;
        for(ExpressionNode eN : qN.getNodes()) // все ноды в кавычках
        {
            if(!isFirstIteration)
            {
                sB.append("+");
            }
            if(eN instanceof StringNode)
            {
                StringNode sN = (StringNode)eN;
                sB.append("\"");
                sB.append(sN.getString());
                sB.append("\"");
            }
            else if(eN instanceof VariableNode)
            {
                VariableNode vN = (VariableNode)eN;
                String toAdd = vN.getVariable().getText().substring(1);
                sB.append(toAdd);
                sB.append(".toString() ");
            }
            else if(eN instanceof SquareBracesNodes)
            {
                SquareBracesNodes sBN = (SquareBracesNodes)eN;
                SolveSquareBraces(sBN, nameOfVar);
                sB.append("TEMP_VAR.toString()");
            }
            isFirstIteration = false;
        }
        return sB;
    }

    private String SolvePUTS(UnarOperationNode uON) throws Exception
    {
        if(uON.getOperand() instanceof ValueNode) // например, string
        {
            ValueNode vN = (ValueNode)uON.getOperand();
            //lMain.addLocalVariable(vN.getValue()., pool.get("java.lang.String")); // создается объект типа String
            String sB = vN.getValue().getText();
            lMain.insertAfter("{System.out.println(\"" + sB + "\");}\n");
            return sB;
        }
        else if(uON.getOperand() instanceof QuotationNodes) // кавычки ""
        {
            QuotationNodes qN = (QuotationNodes)uON.getOperand();
            var sB = SolveQuatationNode(qN, null);
            lMain.insertAfter("{System.out.println(" + sB + ");}\n");
            return sB.toString();
        }
        else if(uON.getOperand() instanceof CurlyBracesNodes) // {bla bla}
        {
            CurlyBracesNodes cBN = (CurlyBracesNodes)uON.getOperand();
            StringBuilder sB = new StringBuilder();
            for(ExpressionNode eN : cBN.getNodes()) // все ноды в кавычках
            {
                if(eN instanceof StringNode)
                {
                    StringNode sN = (StringNode)eN;
                    sB.append(sN.getString().replace("\\", "\\\\").replace("\"", "\\\""));
                }
            }
            lMain.insertAfter("{System.out.println(\"" + sB + "\");}\n");
            return sB.toString();
        }
        else if(uON.getOperand() instanceof VariableNode) // puts $X
        {
            VariableNode vN = (VariableNode)uON.getOperand();
            if(vN.getVariable().getType().equals(TokenType.LINK_VARIABLE))
            {
                String sB = vN.getVariable().getText().substring(1);
                lMain.insertAfter("{System.out.println(" + sB + ");}\n");
                return sB;
            }
        }
        else if(uON.getOperand() instanceof SquareBracesNodes)
        {
            SquareBracesNodes sBN = (SquareBracesNodes)uON.getOperand();
            SolveSquareBraces(sBN, null);
            String sB = "TEMP_VAR";
            lMain.insertAfter("{System.out.println(" + "TEMP_VAR" + ");}\n");
            return sB;
        }
        return null;
    }

    private void AddLocalVarIfNeeded(String nameOfVar) throws Exception
    {
        for(String str : _varNames)
        {
            if(str.equals(nameOfVar))
            {
                return;
            }
        }
        _varNames.add(nameOfVar);
        lMain.addLocalVariable(nameOfVar, pool.get("java.lang.Object"));
    }
}
