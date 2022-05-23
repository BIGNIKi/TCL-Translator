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
                else if(uON.getOperand() instanceof QuotationNodes) // кавычки
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
                            sB.append(sN.getString().replace("\"", "\\\""));
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

            if(bON.getOperator().getType().equals(TokenType.SET)) // это SET
            {
                if(bON.getWhatAssign() instanceof QuotationNodes) // set X "text"
                {
                    QuotationNodes qN = (QuotationNodes)bON.getWhatAssign();
                    if(qN.getNodes().get(0) instanceof StringNode)
                    {
                        StringNode sN = (StringNode)qN.getNodes().get(0);
                        lMain.addLocalVariable(bON.getWhomAssign().getVariable().getText(), pool.get("java.lang.String"));
                        lMain.insertBefore(bON.getWhomAssign().getVariable().getText()+"="+"\""+sN.getString()+"\""+";\n");
                    }
                }
                else if(bON.getWhatAssign() instanceof ValueNode)
                {

                    ValueNode vN = (ValueNode)bON.getWhatAssign();
                    if(vN.getValue().getType().equals(TokenType.FLOAT))
                    {
                        float fl = Float.parseFloat(vN.getValue().getText());
                        lMain.addLocalVariable(bON.getWhomAssign().getVariable().getText(), pool.get("java.lang.Float"));
                        lMain.insertBefore(bON.getWhomAssign().getVariable().getText()+"= new Float("+ Float.toString(fl) + ")"+";\n");
                    }
                    else if(vN.getValue().getType().equals(TokenType.INTEGER))
                    {
                        int intulya = Integer.parseInt(vN.getValue().getText());
                        lMain.addLocalVariable(bON.getWhomAssign().getVariable().getText(), pool.get("java.lang.Integer"));
                        lMain.insertBefore(bON.getWhomAssign().getVariable().getText()+"= new Integer("+ Integer.toString(intulya) + ")"+";\n");
                    }
                }
            }
        }
    }
}
