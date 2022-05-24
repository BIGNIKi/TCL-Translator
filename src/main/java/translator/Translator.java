package translator;

import ast.*;
import javassist.*;
import lexer.TokenType;

import java.util.ArrayList;
import java.util.List;

public class Translator
{
    private final CtClass cc; // некий класс

    private final ClassPool pool; // ClassPool - это хэш-таблица, в которой хранится CtClass

    private CtMethod lMain; // метод evaluate

    private int localVars = 0; // номер следующей локальной переменной в методе evaluate
    private List<String> _varNames = new ArrayList<>(); // имена переменных


    public Translator() throws Exception
    {
        pool = ClassPool.getDefault();
        cc = pool.get("translator.helpers.Source");
        cc.setName("TCLSource");
    }

    public void generateClass(ExpressionNode eN) throws CannotCompileException, NotFoundException
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
                lMain.insertAfter(bON.getWhomAssign().getVariable().getText()+"= new Float("+ Float.toString(fl) + ")"+";\n");
                return fl;
            }
            else if(vN.getValue().getType().equals(TokenType.INTEGER))
            {
                int intulya = Integer.parseInt(vN.getValue().getText());
                AddLocalVarIfNeeded(bON.getWhomAssign().getVariable().getText());
                lMain.insertAfter(bON.getWhomAssign().getVariable().getText()+"= new Integer("+ Integer.toString(intulya) + ")"+";\n");
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

    private Object SolveSquareBraces(SquareBracesNodes sBN, String nameOfVar) throws Exception
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
                    lMain.insertAfter(nameOfVar+"= new Integer("+ Integer.toString(intu) + ")"+";\n");
                    lMain.addLocalVariable("TEMP_VAR", pool.get("java.lang.Object"));
                    lMain.insertAfter("TEMP_VAR = " + nameOfVar + ";\n");
                    return intu;
                }
                else if(ob instanceof Float)
                {
                    Float floatik = (Float) ob;
                    lMain.insertAfter(nameOfVar+"= new Float("+ Float.toString(floatik) + ")"+";\n");
                    lMain.addLocalVariable("TEMP_VAR", pool.get("java.lang.Object"));
                    lMain.insertAfter("TEMP_VAR = " + nameOfVar + ";\n");
                    return floatik;
                }
                else if(ob instanceof String)
                {
                    String str = (String) ob;
                    //String forTest = bON.getWhomAssign().getVariable().getText()+"="+"\""+str+"\""+";\n";
                    String forTest = nameOfVar+"="+str+";\n";
                    lMain.insertAfter(forTest);
                    lMain.addLocalVariable("TEMP_VAR", pool.get("java.lang.Object"));
                    lMain.insertAfter("TEMP_VAR = " + nameOfVar + ";\n");
                    return str;
                }
                else if(ob == null)
                {
                    //lMain.addLocalVariable("TEMP_VAR", pool.get("java.lang.Object"));
                    //String str = "TEMP_VAR";
                    //lMain.insertAfter(bON.getWhomAssign().getVariable().getText()+"="+ str +";\n");
                    //return str;
                    return null;
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
        return null;
    }

    private void SolveBracesAndSquareArihmetic(List<ExpressionNode> nodes) throws Exception // решает арифметические приколы
    {
        // TODO: отсортировать ноды в порядке арифм операций

        String firstParam = null; // имя первой переменной
        TokenType tokType = null;
        lMain.addLocalVariable("TEMP_VAR", pool.get("java.lang.Object")); // объявление временной переменной для расчетов
        for(ExpressionNode eN : nodes)
        {
            if(eN instanceof VariableNode)
            {
                VariableNode vN = (VariableNode)eN;
                if(firstParam == null)
                {
                    firstParam = vN.getVariable().getText().substring(1);
                }
                else
                {
                    //lMain.addLocalVariable("TEMP_VAR", pool.get("java.lang.Object"));
                    if(tokType.equals(TokenType.PLUS))
                    {
                        String str = "TEMP_VAR = add("+ firstParam + "," + vN.getVariable().getText().substring(1) + ")"+";\n";
                        lMain.insertAfter(str);
                        firstParam = "TEMP_VAR";
                        tokType = null;
                    }
                    else if(tokType.equals(TokenType.MINUS))
                    {
                        String str = "TEMP_VAR = sub("+ firstParam + "," + vN.getVariable().getText().substring(1) + ")"+";\n";
                        lMain.insertAfter(str);
                        firstParam = "TEMP_VAR";
                        tokType = null;
                    }
                    else if(tokType.equals(TokenType.MULTIPLICATION))
                    {
                        String str = "TEMP_VAR = mul("+ firstParam + "," + vN.getVariable().getText().substring(1) + ")"+";\n";
                        lMain.insertAfter(str);
                        firstParam = "TEMP_VAR";
                        tokType = null;
                    }
                }
            }
            else if(eN instanceof OperationNode)
            {
                OperationNode oN = (OperationNode)eN;
                if(oN.getOperation().getType().equals(TokenType.PLUS))
                {
                    tokType = TokenType.PLUS;
                    //lMain.addLocalVariable("TEMP_VAR", pool.get("java.lang.Object"));
                    //lMain.insertBefore("TEMP_VAR = add("+ Integer.toString(intulya) + ")"+";\n");
                }
                else if(oN.getOperation().getType().equals(TokenType.MINUS))
                {
                    tokType = TokenType.MINUS;
                }
                else if(oN.getOperation().getType().equals(TokenType.MULTIPLICATION))
                {
                    tokType = TokenType.MULTIPLICATION;
                }
            }
            else if(eN instanceof ValueNode) // чиселка
            {
                if(tokType != null && tokType.equals(TokenType.PLUS)) // уже есть знак до (и выражение соответсвенно)
                {
                    ValueNode vN = (ValueNode)eN;
                    if(vN.getValue().getType().equals(TokenType.FLOAT))
                    {
                        String newFloat = "new Float(" + vN.getValue().getText() +")";
                        lMain.insertAfter("TEMP_VAR = add("+ firstParam + "," + newFloat + ")"+";\n");
                    }
                    else if(vN.getValue().getType().equals(TokenType.INTEGER))
                    {
                        String newInteger = "new Integer(" + vN.getValue().getText() +")";
                        lMain.insertAfter("TEMP_VAR = add("+ firstParam + "," + newInteger + ")"+";\n");
                    }

                    //firstParam = "TEMP_VAR";
                    tokType = null;
                }
                else if(tokType != null && tokType.equals(TokenType.MINUS))
                {
                    ValueNode vN = (ValueNode)eN;
                    if(vN.getValue().getType().equals(TokenType.FLOAT))
                    {
                        String newFloat = "new Float(" + vN.getValue().getText() +")";
                        lMain.insertAfter("TEMP_VAR = sub("+ firstParam + "," + newFloat + ")"+";\n");
                    }
                    else if(vN.getValue().getType().equals(TokenType.INTEGER))
                    {
                        String newInteger = "new Integer(" + vN.getValue().getText() +")";
                        lMain.insertAfter("TEMP_VAR = sub("+ firstParam + "," + newInteger + ")"+";\n");
                    }

                    tokType = null;
                }
                else if(tokType != null && tokType.equals(TokenType.MULTIPLICATION))
                {
                    ValueNode vN = (ValueNode)eN;
                    if(vN.getValue().getType().equals(TokenType.FLOAT))
                    {
                        String newFloat = "(Object) new Float(" + vN.getValue().getText() +")";
                        lMain.insertAfter("TEMP_VAR = mul("+ firstParam + "," + newFloat + ")"+";\n");
                    }
                    else if(vN.getValue().getType().equals(TokenType.INTEGER))
                    {
                        String newInteger = "(Object) new Integer(" + vN.getValue().getText() +")";
                        lMain.insertAfter("TEMP_VAR = mul("+ firstParam + "," + newInteger + ")"+";\n");
                    }

                    tokType = null;
                }
                else if(tokType == null) // сработает, когда цифра стоит в выражении первой
                {
                    ValueNode vN = (ValueNode)eN;
                    firstParam = "TEMP_VAR";
                    if(vN.getValue().getType().equals(TokenType.FLOAT))
                    {
                        String newFloat = "new Float(" + vN.getValue().getText() +")";
                        lMain.insertAfter("TEMP_VAR = " + newFloat +";\n");
                    }
                    else if(vN.getValue().getType().equals(TokenType.INTEGER))
                    {
                        String newInteger = "new Integer(" + vN.getValue().getText() +")";
                        lMain.insertAfter("TEMP_VAR = " + newInteger +";\n");
                    }

                }
            }
            else if(eN instanceof MathFunctionNode)
            {
                MathFunctionNode mFN = (MathFunctionNode)eN;
                if(mFN.getMathFun().getType().equals(TokenType.SQRT)) //SQRT
                {
                    if(mFN.getArguments().get(0) instanceof VariableNode)
                    {
                        VariableNode vN = (VariableNode)mFN.getArguments().get(0);
                        lMain.insertAfter("TEMP_VAR = sqrt(" + vN.getVariable().getText().substring(1) +");\n");
                    }
                    else if(mFN.getArguments().get(0) instanceof ValueNode)
                    {
                        ValueNode vN = (ValueNode)mFN.getArguments().get(0);
                        if(vN.getValue().getType().equals(TokenType.INTEGER))
                        {
                            String to = "(Object) new Integer(" + vN.getValue().getText() + ")";
                            lMain.insertAfter("TEMP_VAR = sqrt(" + to +");\n");
                        }
                        else if(vN.getValue().getType().equals(TokenType.FLOAT))
                        {
                            String to = "(Object) new Float(" + vN.getValue().getText() + ")";
                            lMain.insertAfter("TEMP_VAR = sqrt(" + to +");\n");
                        }
                    }
                }
            }
            else if(eN instanceof BracesNodes) // (...)
            {
                BracesNodes bN = (BracesNodes)eN;
                var nodesNew = bN.getNodes();
                SolveBracesAndSquareArihmetic(nodesNew);
                lMain.insertAfter("TEMP_VAR = TEMP_VAR;\n");
                firstParam = "TEMP_VAR";
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
