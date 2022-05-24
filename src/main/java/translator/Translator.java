package translator;

import ast.*;
import javassist.*;
import lexer.TokenType;

public class Translator
{
    private final CtClass cc; // некий класс

    private final ClassPool pool; // ClassPool - это хэш-таблица, в которой хранится CtClass

    private CtMethod lMain; // метод evaluate

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
                if(uON.getOperand() instanceof ValueNode) // например, string
                {
                    ValueNode vN = (ValueNode)uON.getOperand();
                    //lMain.addLocalVariable(vN.getValue()., pool.get("java.lang.String")); // создается объект типа String
                    lMain.insertAfter("{System.out.println(\"" + vN.getValue().getText() + "\");}\n");
                }
                else if(uON.getOperand() instanceof QuotationNodes) // кавычки ""
                {
                    QuotationNodes qN = (QuotationNodes)uON.getOperand();
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
                            SolveSquareBraces(sBN, null);
                            sB.append("TEMP_VAR.toString()");
                        }
                        isFirstIteration = false;
                    }
                    lMain.insertAfter("{System.out.println(" + sB + ");}\n");
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
                }
                else if(uON.getOperand() instanceof VariableNode) // puts $X
                {
                    VariableNode vN = (VariableNode)uON.getOperand();
                    if(vN.getVariable().getType().equals(TokenType.LINK_VARIABLE))
                    {
                        //vN.getVariable().getText().substring(1);
                        lMain.insertAfter("{System.out.println(" + vN.getVariable().getText().substring(1) + ");}\n");
                    }
                }

                //lMain.addLocalVariable(uON.getOperand()., pool.get("java.lang.String")); // создается объект типа String
                //lMain.insertBefore("varName"+"=" + "" + ";");
            }

            System.out.println("Started");
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
            if(qN.getNodes().get(0) instanceof StringNode)
            {
                StringNode sN = (StringNode)qN.getNodes().get(0);
                lMain.addLocalVariable(bON.getWhomAssign().getVariable().getText(), pool.get("java.lang.Object"));
                lMain.insertBefore(bON.getWhomAssign().getVariable().getText()+"="+"\""+sN.getString()+"\""+";\n");
                return sN.getString();
            }
            else if(qN.getNodes().get(0) instanceof SquareBracesNodes)
            {
                SquareBracesNodes sBN = (SquareBracesNodes)qN.getNodes().get(0);
                lMain.addLocalVariable(bON.getWhomAssign().getVariable().getText(), pool.get("java.lang.Object"));
                Object ob = SolveSquareBraces(sBN, bON);
                lMain.insertBefore(bON.getWhomAssign().getVariable().getText()+"="+"\""+ob.toString()+"\""+";\n");
                return ob.toString();
            }
            return null;
        }
        else if(bON.getWhatAssign() instanceof ValueNode) // set X 10
        {
            ValueNode vN = (ValueNode)bON.getWhatAssign();
            if(vN.getValue().getType().equals(TokenType.FLOAT))
            {
                float fl = Float.parseFloat(vN.getValue().getText());
                lMain.addLocalVariable(bON.getWhomAssign().getVariable().getText(), pool.get("java.lang.Object"));
                lMain.insertBefore(bON.getWhomAssign().getVariable().getText()+"= new Float("+ Float.toString(fl) + ")"+";\n");
                return fl;
            }
            else if(vN.getValue().getType().equals(TokenType.INTEGER))
            {
                int intulya = Integer.parseInt(vN.getValue().getText());
                lMain.addLocalVariable(bON.getWhomAssign().getVariable().getText(), pool.get("java.lang.Object"));
                lMain.insertBefore(bON.getWhomAssign().getVariable().getText()+"= new Integer("+ Integer.toString(intulya) + ")"+";\n");
                return intulya;
            }
        }
        else if(bON.getWhatAssign() instanceof SquareBracesNodes) // "[]"
        {
            SquareBracesNodes sBN = (SquareBracesNodes)bON.getWhatAssign();
            SolveSquareBraces(sBN, bON);
            String varName = bON.getWhomAssign().getVariable().getText();
            lMain.addLocalVariable(varName, pool.get("java.lang.Object"));
            lMain.insertAfter(varName + "= TEMP_VAR;\n");
            return null;
        }
        else if(bON.getWhatAssign() instanceof CurlyBracesNodes)
        {
            CurlyBracesNodes cBN = (CurlyBracesNodes)bON.getWhatAssign();
            if(cBN.getNodes().get(0) instanceof StringNode)
            {
                StringNode sN = (StringNode)cBN.getNodes().get(0);
                lMain.addLocalVariable(bON.getWhomAssign().getVariable().getText(), pool.get("java.lang.Object"));
                lMain.insertBefore(bON.getWhomAssign().getVariable().getText()+"="+"\""+sN.getString().replace("\\", "\\\\").replace("\"", "\\\"") +"\""+";\n");
                return sN.getString();
            }
        }
        return null;
    }

    private Object SolveSquareBraces(SquareBracesNodes sBN, BinOperationNode bON) throws Exception
    {
        for(ExpressionNode exN : sBN.getNodes())
        {
            if(exN instanceof BinOperationNode) // например set
            {
                BinOperationNode bOON = (BinOperationNode)exN;
                Object ob = DoBinOperationNode(bOON);
                lMain.addLocalVariable(bON.getWhomAssign().getVariable().getText(), pool.get("java.lang.Object"));
                if(ob instanceof Integer)
                {
                    Integer intu = (Integer) ob;
                    lMain.insertBefore(bON.getWhomAssign().getVariable().getText()+"= new Integer("+ Integer.toString(intu) + ")"+";\n");
                    return intu;
                }
                else if(ob instanceof Float)
                {
                    Float floatik = (Float) ob;
                    lMain.insertBefore(bON.getWhomAssign().getVariable().getText()+"= new Float("+ Float.toString(floatik) + ")"+";\n");
                    return floatik;
                }
                else if(ob instanceof String)
                {
                    String str = (String) ob;
                    lMain.insertBefore(bON.getWhomAssign().getVariable().getText()+"="+"\""+str+"\""+";\n");
                    return str;
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
                        String firstParam = null; // имя первой переменной
                        TokenType tokType = null;
                        lMain.addLocalVariable("TEMP_VAR", pool.get("java.lang.Object")); // объявление временной переменной для расчетов
                        for(ExpressionNode eN : mEN.getNodes())
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
                                        //lMain.insertAfter("Object TEMP_VAR = this.add(X, Y);\nSystem.out.println(TEMP_VAR);\n");
                                        firstParam = "TEMP_VAR";
                                        tokType = null;
                                    }
                                    else if(tokType.equals(TokenType.MINUS))
                                    {
                                        String str = "TEMP_VAR = sub("+ firstParam + "," + vN.getVariable().getText().substring(1) + ")"+";\n";
                                        lMain.insertAfter(str);
                                        //lMain.insertAfter("Object TEMP_VAR = this.add(X, Y);\nSystem.out.println(TEMP_VAR);\n");
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

                                    //firstParam = "TEMP_VAR";
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
                                    // TODO: SQRT
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
                        }
                    }
                }
            }
        }
        return null;
    }
}
