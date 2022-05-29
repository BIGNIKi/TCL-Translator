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

    private final ClassPool pool; // ClassPool - это хэш-таблица CtClass'ов, в которой будут храниться все классы
    // позволяет считывать файл класса по требованию, дабы составить обертку над java классом
    // называемую CtClass, чтобы после иметь возможность его видоизменять

    private CtMethod lMain; // метод evaluate

    //private int localVars = 0; // номер следующей локальной переменной в методе evaluate
    private final List<String> _varNames = new ArrayList<>(); // имена переменных


    public Translator() throws Exception
    {
        pool = ClassPool.getDefault(); // подсасываем все классы из default директории
        cc = pool.makeClass("TCLSource"); // создали класс
        cc.setSuperclass(pool.get("translator.helpers.BaseSource")); // отнаследовали его от BaseSource
        cc.addConstructor(CtNewConstructor.make("public TCLSource(){}", cc)); // бахнули публичный конструктор
    }

    public void generateClass(ExpressionNode eN)
    {
        try
        {
            lMain = CtNewMethod.make("public void evaluate() throws Exception {\n}", cc); // создает метод в класс cc

            StringBuilder code = new StringBuilder();
            for(var node : ((StatementsNode)eN).getCodeStrings())
            {
                code.append(ProcessBlock(node, lMain));
            }
            lMain.insertAfter(code.toString());

            cc.addMethod(lMain);


            CtMethod method1 = CtNewMethod.make("public static void main(String[] args){}", cc);
            method1.addLocalVariable("source", pool.get("TCLSource")); // задефайнили переменную типа LispSource
            method1.insertAfter("source = new TCLSource(); "); // создали экземпляр объекта
            method1.insertAfter("source.evaluate(); "); // вызвали метод у объекта

            cc.addMethod(method1);

            writeToFile();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
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
            cc.writeFile(); // запись .class файла на диск
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    // возвращает код
    private String ProcessBlock(ExpressionNode node, CtMethod method) throws Exception
    {
        StringBuilder codeResult = new StringBuilder();

        if(node instanceof UnarOperationNode) // нашли унарный оператор (напрмер puts)
        {
            UnarOperationNode uON = (UnarOperationNode) node;

            if(uON.getOperator().getType().equals(TokenType.PUTS)) // это PUTS
            {
                VarAndCode vAC = SolvePUTS(uON, method);
                if(vAC._allCode != null)
                {
                    codeResult.append(vAC._allCode);
                }
                codeResult.append("System.out.println(").append(vAC._nameOfVar).append(".toString());\n");
            }
        }
        else if(node instanceof BinOperationNode) // например set
        {
            BinOperationNode bON = (BinOperationNode) node;
            VarAndCode vAC = DoBinOperationNode(bON, method);
            codeResult.append(vAC._allCode);
        }
        else if(node instanceof SwitchNode) // switch
        {
            SwitchNode sN = (SwitchNode)node;
            VarAndCode vAC = SolveSwitch(sN, method);
            codeResult.append(vAC._allCode);
        }
        else if(node instanceof IfNode) // if
        {
            IfNode iN = (IfNode)node;
            String code = SolveIfElse(iN, method);
            codeResult.append(code);
        }
        else if(node instanceof WhileLoopNode) // while
        {
            WhileLoopNode wLN = (WhileLoopNode)node;
            codeResult.append(SolveWhileCycle(wLN, method));
        }
        else if(node instanceof TCLKeywordsNode) // break, continue
        {
            TCLKeywordsNode tclKN = (TCLKeywordsNode)node;
            codeResult.append(tclKN.getKeyword().getText()).append(";\n");
        }
        else if(node instanceof ForLoopNode) // for'чик
        {
            ForLoopNode fLN = (ForLoopNode)node;
            codeResult.append(SolveForLoop(fLN, method));
        }
        else if(node instanceof IncrNode)
        {
            IncrNode iN = (IncrNode)node;
            codeResult.append(SolveIncr(iN));
        }
        else if(node instanceof ProcNode)
        {
            ProcNode pN = (ProcNode)node;
            MakeNewMethod(pN);
        }
        else if(node instanceof ReturnNode)
        {
            ReturnNode rN = (ReturnNode)node;
            if(rN.getReturnValue() instanceof VariableNode) // ссылочная переменная
            {
                VariableNode vN = (VariableNode)rN.getReturnValue();
                codeResult.append("return ").append(vN.getVariable().getText().substring(1)).append(";\n");
            }
        }

        return codeResult.toString();
    }

    private void MakeNewMethod(ProcNode pN) throws Exception
    {
        String code = "private Object ";
        StringBuilder codik = new StringBuilder(pN.getFunctionName().getString()). append("(");
        for(int i = 0; i<pN.getArgs().size(); i++)
        {
            if(i != 0)
                codik.append(",");
            codik.append("Object arg_").append(i);
        }
        codik.append(")").append("throws Exception {return null;}");
        code += codik.toString();
        CtMethod newMethod = CtNewMethod.make(code, cc); // создали метод
    }

    private String SolveIncr(IncrNode iN)
    {
        StringBuilder res = new StringBuilder();

        res.append(iN.getVariable().getVariable().getText())
                .append(" = add(").append(iN.getVariable().getVariable().getText()).append(", new Integer(").append(iN.getValue()).append("));\n");

        return res.toString();
    }

    private String SolveForLoop(ForLoopNode fLN, CtMethod method) throws Exception
    {
        StringBuilder code = new StringBuilder();

        code.append("for(");
        // БЛОК инициализации
        StringBuilder temp = new StringBuilder();
        for(int i = 0; i<fLN.getInitBlock().size(); i++)
        {
            ExpressionNode eN = fLN.getInitBlock().get(i);
            String be = ProcessBlock(eN, method).replace(";\n", ",");
            temp.append(be);
        }
        String codik =temp.toString();//.replace(";", ",");
        if(codik.length() >= 1)
            codik = codik.substring(0, codik.length()-1);
        codik += ";";
        code.append(codik);
        // БЛОК инициализации

        // БЛОК для условия
        //get(0) - так как случайно туда добавили List, а по факту в листе всегда один элемент
        if(fLN.getConditionsBlock().get(0) instanceof BracesNodes)
        {
            BracesNodes bN = (BracesNodes)fLN.getConditionsBlock().get(0);

            code.append(MakeCondition(bN)); // добавляем условия
        }
        code.append(";");
        // БЛОК для условия

        // БЛОК счётчика
        temp = new StringBuilder();
        for(int i = 0; i<fLN.getCounterBlock().size(); i++)
        {
            ExpressionNode eN = fLN.getCounterBlock().get(i);
            String be = ProcessBlock(eN, method).replace(";\n", ",");
            temp.append(be);
        }
        codik = temp.toString();//.replace(";", ",");
        if(codik.length() >= 1)
            codik = codik.substring(0, codik.length()-1);
        code.append(codik);
        code.append(")\n{\n");
        // БЛОК счётчика

        // БЛОК body
        for(int i = 0; i < fLN.getCommandBlock().size(); i++)
        {
            ExpressionNode eN = fLN.getCommandBlock().get(i);
            code.append(ProcessBlock(eN, method));
        }
        // БЛОК body

        code.append("}\n");
        return code.toString();
    }

    private String SolveWhileCycle(WhileLoopNode wLN, CtMethod method) throws Exception
    {
        StringBuilder code = new StringBuilder();

        code.append("while");
        if(wLN.getCondition() instanceof BracesNodes)
        {
            BracesNodes bN = (BracesNodes)wLN.getCondition();
            code.append(MakeCondition(bN));
            code.append("\n");
        }
        // далее - тело цикла
        code.append("{\n");
        if(wLN.getBody() instanceof CurlyBracesNodes)
        {
            CurlyBracesNodes cBN = (CurlyBracesNodes)wLN.getBody();
            for(int i = 0; i < cBN.getNodes().size(); i++)
            {
                ExpressionNode eN = cBN.getNodes().get(i);
                code.append(ProcessBlock(eN, method));
            }
        }
        code.append("}\n");

        return code.toString();
    }

    private String SolveIfElse(IfNode iN, CtMethod method) throws Exception
    {
        StringBuilder code = new StringBuilder();

        for(int i = 0; i < iN.getBranches().size(); i++)
        {
            IfBranch iB = iN.getBranches().get(i);
            code.append(AddCondition(iB, i));
            code.append("\n{\n");

            // вставляем тело выражения
            if(iB.getBody() instanceof CurlyBracesNodes)
            {
                CurlyBracesNodes cBN = (CurlyBracesNodes)iB.getBody();
                for(var node : cBN.getNodes())
                {
                    code.append(ProcessBlock(node, method));
                }
            }
            // вставляем тело выражения

            code.append("}\n");
        }

        return code.toString();
    }

    // добавляет if() или else() или else
    private String AddCondition(IfBranch iB, int orderNum)
    {
        StringBuilder result = new StringBuilder();

        if(orderNum == 0) // самое первое
            result.append("if");
        else if(iB.getCondition() == null) // когда без условия
            result.append("else");
        else
            result.append("else if");

        if(iB.getCondition() != null && iB.getCondition() instanceof BracesNodes)
        {
            BracesNodes bN = (BracesNodes)iB.getCondition();
            result.append(MakeCondition(bN)); // добавляем условия для if или elseif
        }
        // запись условия в скобках if(***)

        return result.toString();
    }

    // делает условия для if'ов, while'ов, for'ов и тп
    // на выходе имеем "(***)"
    private String MakeCondition(BracesNodes bN)
    {
        StringBuilder resultRET = new StringBuilder();

        resultRET.append("(");

        int numOfArg = 0; // 0 - первый аргумент 1 - второй аргумент (для булевских функций)
        String nameOfFirstArg = "";
        for(int i = 0; i<bN.getNodes().size(); i++)
        {
            ExpressionNode eN = bN.getNodes().get(i);
            if(eN instanceof VariableNode) // ссылочная перменная
            {
                VariableNode vN = (VariableNode)eN;
                //result.append(vN.getVariable().getText().substring(1));
                String res = vN.getVariable().getText().substring(1);
                if(numOfArg == 0)
                {
                    nameOfFirstArg = res;
                    numOfArg = 1;
                }
                else if(numOfArg == 1)
                {
                    resultRET.append(res).append(")");
                    nameOfFirstArg = "";
                    numOfArg = 0;
                }
            }
            // операция всегда будет после первого аргумента и не факт, что вообще будет
            else if(eN instanceof OperationNode) // операция == >= <= < > !=
            {
                OperationNode oN = (OperationNode)eN;
                if(oN.getOperation().getType().equals(TokenType.IS_EQUAL)) // ==
                {
                    resultRET.append("IS_EQUAL(").append(nameOfFirstArg).append(",");
                }
                else if(oN.getOperation().getType().equals(TokenType.GREATER_OR_EQUAL))
                {
                    resultRET.append("GREATER_OR_EQUAL(").append(nameOfFirstArg).append(",");
                }
                else if(oN.getOperation().getType().equals(TokenType.IS_NOT_EQUAL))
                {
                    resultRET.append("IS_NOT_EQUAL(").append(nameOfFirstArg).append(",");
                }
                else if(oN.getOperation().getType().equals(TokenType.AND))
                {
                    if(nameOfFirstArg.equals("true") || nameOfFirstArg.equals("false"))
                        resultRET.append(nameOfFirstArg);
                    resultRET.append(" && ");
                    nameOfFirstArg = "";
                    numOfArg = 0;
                }
                else if(oN.getOperation().getType().equals(TokenType.OR))
                {
                    if(nameOfFirstArg.equals("true") || nameOfFirstArg.equals("false"))
                        resultRET.append(nameOfFirstArg);
                    resultRET.append(" || ");
                    nameOfFirstArg = "";
                    numOfArg = 0;
                }
                else if(oN.getOperation().getType().equals(TokenType.LESS_OR_EQUAL))
                {
                    resultRET.append("LESS_OR_EQUAL(").append(nameOfFirstArg).append(",");
                }
                else if(oN.getOperation().getType().equals(TokenType.GREATER))
                {
                    resultRET.append("GREATER(").append(nameOfFirstArg).append(",");
                }
                else if(oN.getOperation().getType().equals(TokenType.LESS))
                {
                    resultRET.append("LESS(").append(nameOfFirstArg).append(",");
                }
            }
            else if(eN instanceof ValueNode) // значение - Integer Float String true false
            {
                ValueNode vN = (ValueNode)eN;
                String res = "";
                if(vN.getValue().getType().equals(TokenType.INTEGER)) // Integer
                {
                    //result.append("new Integer(").append(vN.getValue().getText()).append(")");
                    res = "new Integer(" + vN.getValue().getText() + ")";
                }
                else if(vN.getValue().getType().equals(TokenType.FLOAT)) // Float
                {
                    res = "new Float(" + vN.getValue().getText() + ")";
                }
                else if(vN.getValue().getType().equals(TokenType.STRING))
                {
                    res = "new String(\"" + vN.getValue().getText() + "\")";
                }
                else if(vN.getValue().getType().equals(TokenType.TRUE))
                {
                    res = "true";
                }
                else if(vN.getValue().getType().equals(TokenType.FALSE))
                {
                    res = "false";
                }

                if(numOfArg == 0)
                {
                    nameOfFirstArg = res;
                    numOfArg = 1;
                }
                else if(numOfArg == 1)
                {
                    resultRET.append(res).append(")");
                    nameOfFirstArg = "";
                    numOfArg = 0;
                }
            }
        }

        if(nameOfFirstArg.equals("true") || nameOfFirstArg.equals("false"))
            resultRET.append(nameOfFirstArg);

        resultRET.append(")");

        return resultRET.toString();
    }

    // Switch в TCL умеет сравнивать только строки
    private VarAndCode SolveSwitch(SwitchNode sN, CtMethod method) throws Exception
    {
        VarAndCode resultRET = new VarAndCode();
        resultRET._nameOfVar = null; // нет возвращаемого значения

        StringBuilder result = new StringBuilder();

        // Если default нет и все остальные case'ы в пролете, то вернет пустую строку
        if(sN.getString().getType().equals(TokenType.LINK_VARIABLE)) // switch по ссылочной переменной
        {
            String nameOfVar = "TEMP_STRING"; // здесь будет лежать переменная, по которой мы switch'каемся
            method.addLocalVariable(nameOfVar, pool.get("java.lang.String"));
            result.append(nameOfVar).append(" = ").append(sN.getString().getText().substring(1)).append(".toString()").append(";\n");

            for(int i = 0; i<sN.getCases().size(); i++)
            {
                SwitchCase sC = sN.getCases().get(i);
                AddVarsForSwitch(sC, method);
            }

            for(int i = 0; i<sN.getCases().size(); i++)
            {
                SwitchCase sC = sN.getCases().get(i);
                AddSwitchStatement(i == 0, nameOfVar, sC, result, method);
            }
        }

        resultRET._allCode = result.toString();
        return resultRET;
    }

    // заранее объявляем переменные для switch'а, так как его поведение сложно предсказуемо
    private void AddVarsForSwitch(SwitchCase sC, CtMethod method) throws Exception
    {
        if(sC.getBody() instanceof CurlyBracesNodes)
        {
            CurlyBracesNodes cBN = (CurlyBracesNodes)sC.getBody();
            for(int i = 0; i<cBN.getNodes().size(); i++)
            {
                ExpressionNode eN = cBN.getNodes().get(i);
                if(eN instanceof BinOperationNode)
                {
                    BinOperationNode bON = (BinOperationNode)eN;
                    if(bON.getOperator().getType().equals(TokenType.SET))
                    {
                        AddLocalVarIfNeeded(bON.getWhomAssign().getVariable().getText(), method);
                    }
                }
            }
        }
    }

    // isIf - true - первый if
    // метод будет видоизменять result, формируя конструкцию if() else if...
    private void AddSwitchStatement(boolean isIf, String nameOfVar, SwitchCase sC, StringBuilder result, CtMethod method) throws Exception
    {
        if(sC.getValue().getType().equals(TokenType.DEFAULT) && isIf)
            result.append("if(true)\n{");
        else if (sC.getValue().getType().equals(TokenType.DEFAULT) && !isIf)
            result.append("else\n{");
        else if(isIf)
        {
            String str = "";
            if(sC.getValue().getType().equals(TokenType.LINK_VARIABLE))
                str = nameOfVar + ".equals(" + sC.getValue().getText().substring(1) + ".toString())";
            else if(sC.getValue().getType().equals(TokenType.STRING))
                str = nameOfVar + ".equals(\"" + sC.getValue().getText() + "\")";

            result.append("if(").append(str).append(")\n{");
        }
        else
        {
            String str = "";
            if(sC.getValue().getType().equals(TokenType.LINK_VARIABLE))
                str = nameOfVar + ".equals(" + sC.getValue().getText().substring(1) + ".toString())";
            else if(sC.getValue().getType().equals(TokenType.STRING))
            {
                str = nameOfVar + ".equals(\"" + sC.getValue().getText() + "\")";
            }

            result.append("else if(").append(str).append(")\n{");
        }

        // добавляем логику в подифные выражения

        if(sC.getBody() instanceof CurlyBracesNodes)
        {
            CurlyBracesNodes cBN = (CurlyBracesNodes)sC.getBody();
            for(int i = 0; i<cBN.getNodes().size(); i++)
            {
                ExpressionNode eN = cBN.getNodes().get(i);
                if(eN instanceof UnarOperationNode)
                {
                    UnarOperationNode uON = (UnarOperationNode)eN;
                    if(uON.getOperator().getType().equals(TokenType.PUTS))
                    {
                        VarAndCode var2print = SolvePUTS(uON, method);
                        if(var2print._allCode != null)
                        {
                            result.append(var2print._allCode);
                        }
                        result.append("System.out.println(").append(var2print._nameOfVar).append(".toString());\n");
                    }
                }
                else if(eN instanceof BinOperationNode)
                {
                    BinOperationNode bON = (BinOperationNode)eN;
                    if(bON.getOperator().getType().equals(TokenType.SET))
                    {
                        VarAndCode vAC = DoSet(bON, method);

                        result.append(vAC._allCode);
                    }
                }
            }
        }

        // добавляем логику в подифные выражения

        result.append("}\n");
    }

    private VarAndCode DoBinOperationNode(BinOperationNode bON, CtMethod method) throws Exception
    {
        if(bON.getOperator().getType().equals(TokenType.SET)) // это SET
        {
            return DoSet(bON, method);
        }
        return null;
    }

    private VarAndCode DoSet(BinOperationNode bON, CtMethod method) throws Exception
    {
        VarAndCode vAC = new VarAndCode();
        if(bON.getWhatAssign() instanceof QuotationNodes) // set X "text"
        {
            QuotationNodes qN = (QuotationNodes)bON.getWhatAssign();
            BeforeAndToString ob = SolveQuatationNode(qN, bON.getWhomAssign().getVariable().getText(), method);
            String finall = ob.codeBefore;

            AddLocalVarIfNeeded(bON.getWhomAssign().getVariable().getText(), method);
            finall += bON.getWhomAssign().getVariable().getText()+"=" + ob.textToString + ";\n";

            vAC._nameOfVar = bON.getWhomAssign().getVariable().getText();
            vAC._allCode = finall;
            return vAC;
        }
        else if(bON.getWhatAssign() instanceof ValueNode) // set X 10
        {
            ValueNode vN = (ValueNode)bON.getWhatAssign();
            if(vN.getValue().getType().equals(TokenType.FLOAT))
            {
                float fl = Float.parseFloat(vN.getValue().getText());
                AddLocalVarIfNeeded(bON.getWhomAssign().getVariable().getText(), method);

                vAC._nameOfVar = bON.getWhomAssign().getVariable().getText();
                vAC._allCode = bON.getWhomAssign().getVariable().getText()+"= new Float("+ fl + ")"+";\n";
                return vAC;
            }
            else if(vN.getValue().getType().equals(TokenType.INTEGER))
            {
                int intulya = Integer.parseInt(vN.getValue().getText());
                AddLocalVarIfNeeded(bON.getWhomAssign().getVariable().getText(), method);

                vAC._nameOfVar = bON.getWhomAssign().getVariable().getText();
                vAC._allCode = bON.getWhomAssign().getVariable().getText()+"= new Integer("+ intulya + ")"+";\n";
                return vAC;
            }
            else if(vN.getValue().getType().equals(TokenType.STRING))
            {
                String stringulya = vN.getValue().getText();
                AddLocalVarIfNeeded(bON.getWhomAssign().getVariable().getText(), method);

                vAC._nameOfVar = bON.getWhomAssign().getVariable().getText();
                vAC._allCode = bON.getWhomAssign().getVariable().getText()+"= new String(\""+ stringulya + "\")"+";\n";
                return vAC;
            }
        }
        else if(bON.getWhatAssign() instanceof SquareBracesNodes) // "[]"
        {
            SquareBracesNodes sBN = (SquareBracesNodes)bON.getWhatAssign();

            VarAndCode bla = SolveSquareBraces(sBN, bON.getWhomAssign().getVariable().getText(), method); // BLABLA

            StringBuilder codeText = new StringBuilder(bla._allCode);


            String varName = bON.getWhomAssign().getVariable().getText();
            AddLocalVarIfNeeded(varName, method);

            vAC._nameOfVar = varName;
            codeText.append(varName).append("= TEMP_VAR;\n");
            vAC._allCode = codeText.toString();
            return vAC;
        }
        else if(bON.getWhatAssign() instanceof CurlyBracesNodes) // {...}
        {
            CurlyBracesNodes cBN = (CurlyBracesNodes)bON.getWhatAssign();
            if(cBN.getNodes().get(0) instanceof StringNode)
            {
                StringNode sN = (StringNode)cBN.getNodes().get(0);
                AddLocalVarIfNeeded(bON.getWhomAssign().getVariable().getText(), method);

                vAC._nameOfVar = bON.getWhomAssign().getVariable().getText();
                vAC._allCode = bON.getWhomAssign().getVariable().getText()+"="+"\""+sN.getString().replace("\\", "\\\\").replace("\"", "\\\"") +"\""+";\n";
                return vAC;
            }
        }
        return null;
    }

    // []
    // nameOfVar - кому назначить возвращаемое от [] значение
    // возвращает код для [...]
    // хранит свой результат в TEMP_VAR
    private VarAndCode SolveSquareBraces(SquareBracesNodes sBN, String nameOfVar, CtMethod method) throws Exception
    {
        VarAndCode vACResult = new VarAndCode();
        vACResult._nameOfVar = "TEMP_VAR";

        StringBuilder codeText = new StringBuilder();

        for(ExpressionNode exN : sBN.getNodes())
        {
            if(exN instanceof BinOperationNode) // например set
            {
                BinOperationNode bOON = (BinOperationNode)exN;
                VarAndCode vAC = DoBinOperationNode(bOON, method);
                if(nameOfVar != null)
                {
                    AddLocalVarIfNeeded(nameOfVar, method);
                }
                else if(bOON.getWhomAssign().getVariable().getType().equals(TokenType.VARIABLE))
                {
                    nameOfVar = bOON.getWhomAssign().getVariable().getText();
                    AddLocalVarIfNeeded(nameOfVar, method);
                }

                codeText.append(vAC._allCode);

                method.addLocalVariable("TEMP_VAR", pool.get("java.lang.Object"));
                codeText.append("TEMP_VAR = ").append(vAC._nameOfVar).append(";\n");
            }
            else if(exN instanceof UnarOperationNode) // напрмер expr или puts
            {
                UnarOperationNode uON = (UnarOperationNode)exN;

                if(uON.getOperator().getType().equals(TokenType.EXPR)) // expr
                {
                    if(uON.getOperand() instanceof MathExpNodes)
                    {
                        MathExpNodes mEN = (MathExpNodes)uON.getOperand();

                        var nodes = mEN.getNodes();
                        VarAndCode vAC = SolveBracesAndSquareArihmetic(nodes, method);
                        codeText.append(vAC._allCode);
                    }
                }
                else if(uON.getOperator().getType().equals(TokenType.PUTS))
                {
                    VarAndCode code = SolvePUTS(uON, method);
                    if(code._allCode != null)
                    {
                        codeText.append(code._allCode);
                    }
                    codeText.append("System.out.println(").append(code._nameOfVar).append(".toString());\n");
                    method.addLocalVariable("TEMP_VAR", pool.get("java.lang.Object")); // объявление временной переменной для расчетов
                    codeText.append("TEMP_VAR = ").append(code._nameOfVar).append(".toString();\n");
                }
            }
            else if(exN instanceof IncrNode) // increment
            {
                IncrNode iN = (IncrNode)exN;
                codeText.append(SolveIncr(iN));
                method.addLocalVariable("TEMP_VAR", pool.get("java.lang.Object")); // объявление временной переменной для расчетов
                codeText.append("TEMP_VAR = ").append(iN.getVariable().getVariable().getText()).append(".toString();\n");
            }
        }
        vACResult._allCode = codeText.toString();
        return vACResult;
    }

    private VarAndCode SolveBracesAndSquareArihmetic(List<ExpressionNode> nodes, CtMethod method) throws Exception // решает арифметические приколы
    {
        VarAndCode vACResult = new VarAndCode();
        StringBuilder allCodeText = new StringBuilder();

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
                        dynamicNodes = SolveSign(TokenType.PLUS, dynamicNodes, i, numOfUnicVar, "add", allCodeText,method);
                        break;
                    }
                    else if(oN.getOperation().getType().equals(TokenType.MINUS))
                    {
                        dynamicNodes = SolveSign(TokenType.MINUS, dynamicNodes, i, numOfUnicVar, "sub", allCodeText,method);
                        break;
                    }
                    else if(oN.getOperation().getType().equals(TokenType.MULTIPLICATION))
                    {
                        dynamicNodes = SolveSign(TokenType.MULTIPLICATION, dynamicNodes, i, numOfUnicVar, "mul", allCodeText,method);
                        break;
                    }
                    else if(oN.getOperation().getType().equals(TokenType.DIVISION))
                    {
                        dynamicNodes = SolveSign(TokenType.DIVISION, dynamicNodes, i, numOfUnicVar, "div", allCodeText,method);
                        break;
                    }
                    else if(oN.getOperation().getType().equals(TokenType.REMINDER))
                    {
                        dynamicNodes = SolveSign(TokenType.REMINDER, dynamicNodes, i, numOfUnicVar, "reminder", allCodeText,method);
                        break;
                    }
                }
                // все кейсы ниже рассматриваются не только здесь, но и в MakeArgumentForExpression
                else if(dynamicNodes.size() == 1 && eN instanceof ValueNode)
                {
                    ValueNode vN = (ValueNode)eN;
                    String nameOfUniqVar = "UNIQ_VAR_" + numOfUnicVar._val;
                    numOfUnicVar._val++;

                    String nameOfVar = "TEMP_VAR";
                    method.addLocalVariable(nameOfVar, pool.get("java.lang.Object"));
                    if(vN.getValue().getType().equals(TokenType.FLOAT))
                    {
                        String newFloat = "new Float(" + vN.getValue().getText() +")";
                        allCodeText.append(nameOfVar).append(" = ").append(newFloat).append(";\n");
                    }
                    else if(vN.getValue().getType().equals(TokenType.INTEGER))
                    {
                        String newInteger = "new Integer(" + vN.getValue().getText() +")";
                        allCodeText.append(nameOfVar).append(" = ").append(newInteger).append(";\n");
                    }
                    String str = nameOfUniqVar + " = "+ nameOfVar +";\n";
                    method.addLocalVariable(nameOfUniqVar, pool.get("java.lang.Object"));
                    allCodeText.append(str);

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
                        VarAndCode vAC = AddSolveForMathFunc(mFN, numOfUnicVar, "sqrt", 1, method);
                        allCodeText.append(vAC._allCode);

                        MakeFinalToken(vAC._nameOfVar, dynamicNodes);
                        canStop = true;
                        break;
                    }
                    else if(mFN.getMathFun().getType().equals(TokenType.POW))
                    {
                        VarAndCode vAC = AddSolveForMathFunc(mFN, numOfUnicVar, "pow", 2, method);
                        allCodeText.append(vAC._allCode);

                        MakeFinalToken(vAC._nameOfVar, dynamicNodes);
                        canStop = true;
                        break;
                    }
                    else if(mFN.getMathFun().getType().equals(TokenType.RAND))
                    {
                        VarAndCode vAC = AddSolveForMathFunc(mFN, numOfUnicVar, "rand", 0, method);
                        allCodeText.append(vAC._allCode);

                        MakeFinalToken(vAC._nameOfVar, dynamicNodes);
                        canStop = true;
                        break;
                    }
                }
            }
        }
        VariableNode vN = (VariableNode)dynamicNodes.get(0);
        method.addLocalVariable("TEMP_VAR", pool.get("java.lang.Object"));
        String str = "TEMP_VAR = " + vN.getVariable().getText().substring(1) + ";\n";
        allCodeText.append(str);

        vACResult._nameOfVar = "TEMP_VAR";
        vACResult._allCode = allCodeText.toString();

        return vACResult;
    }

    // добавление последнего токена в выражение (арифметическое)
    // dynamicNodes - здесь должна оставаться последняя нода с сылочной перменной с именем nameOfUniqVar
    private void MakeFinalToken(String nameOfUniqVar, List<ExpressionNode> dynamicNodes)
    {
        Token newTok = new Token(TokenType.LINK_VARIABLE, "$"+nameOfUniqVar, 0);
        VariableNode newNode = new VariableNode(newTok);
        dynamicNodes.add(1, newNode);
        dynamicNodes.remove(0);
    }

    private VarAndCode AddSolveForMathFunc(MathFunctionNode mFN, IntRef numOfUnicVar, String nameOfFunc, int numOfArgs, CtMethod method) throws Exception
    {
        VarAndCode result = new VarAndCode();
        StringBuilder str = new StringBuilder();

        String code = MakeArguments(mFN.getArguments(),method);
        str.append(code);

        String nameOfUniqVar = "UNIQ_VAR_" + numOfUnicVar._val;
        result._nameOfVar = nameOfUniqVar; // задали имя переменной
        numOfUnicVar._val++;
        str.append(nameOfUniqVar);
        str.append(" = ").append(nameOfFunc).append("(");

        for(int i = 0; i<numOfArgs; i++)
        {
            String nameOfVar = "ARG_" + i;
            str.append(nameOfVar);
            if(i != numOfArgs-1)
                str.append(",");
        }

        str.append(");\n");
        method.addLocalVariable(nameOfUniqVar, pool.get("java.lang.Object"));
        result._allCode = str.toString();

        return result;
    }

    // для выбранного знака подставляет переменные
    private List<ExpressionNode> SolveSign(TokenType tT, List<ExpressionNode> dynamicNodes, int id,
        IntRef numOfUnicVar, String nameOfFunc, StringBuilder CodeForExpr, CtMethod method) throws Exception
    {
        ExpressionNode firstNode = dynamicNodes.get(id-2);
        MakeArgumentForExpression(firstNode, "0", CodeForExpr,method);
        String nameFirstVar = "ARGUM_"+"0";
        ExpressionNode secondNode = dynamicNodes.get(id-1);
        MakeArgumentForExpression(secondNode, "1", CodeForExpr,method);
        String nameSecondVar = "ARGUM_"+"1";

        String nameOfUniqVar = "UNIQ_VAR_" + numOfUnicVar._val;
        numOfUnicVar._val++;

        String str = nameOfUniqVar + " = " + nameOfFunc + "("+ nameFirstVar + "," + nameSecondVar + ")"+";\n";
        method.addLocalVariable(nameOfUniqVar, pool.get("java.lang.Object"));
        CodeForExpr.append(str);
        dynamicNodes = RemakeOperationList(dynamicNodes, id, tT, nameOfUniqVar);
        return dynamicNodes;
    }

    // создает переменные для аргументов в вызове функций
    // ARG_0, ARG_1 и тд
    // возвращает код, который нужно исполнить для заполнения аргументов
    private String MakeArguments(List<ExpressionNode> nodes, CtMethod method) throws Exception
    {
        StringBuilder result = new StringBuilder();

        for(int i = 0; i<nodes.size(); i++)
        {
            String nameOfVar = "ARG_" + i;
            method.addLocalVariable(nameOfVar, pool.get("java.lang.Object"));
            ExpressionNode eN = nodes.get(i);
            if(eN instanceof ValueNode)
            {
                ValueNode vN = (ValueNode)eN;
                if(vN.getValue().getType().equals(TokenType.FLOAT))
                {
                    String newFloat = "new Float(" + vN.getValue().getText() +")";
                    result.append(nameOfVar).append(" = ").append(newFloat).append(";\n");
                }
                else if(vN.getValue().getType().equals(TokenType.INTEGER))
                {
                    String newInteger = "new Integer(" + vN.getValue().getText() +")";
                    result.append(nameOfVar).append(" = ").append(newInteger).append(";\n");
                }
            }
            else if(eN instanceof VariableNode)
            {
                VariableNode vN = (VariableNode)eN;
                result.append(nameOfVar).append(" = ").append(vN.getVariable().getText().substring(1)).append(";\n");
            }
        }
        return result.toString();
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

    // создает промежуточную переменную для вычисления арифм выражения
    // возвращает имя переменной
    private void MakeArgumentForExpression(ExpressionNode node, String argNum, StringBuilder codeForExpr, CtMethod method) throws Exception
    {
        if(node instanceof ValueNode) // число
        {
            ValueNode vN = (ValueNode)node;
            String nameOfVar = "ARGUM_"+argNum;
            method.addLocalVariable(nameOfVar, pool.get("java.lang.Object"));
            if(vN.getValue().getType().equals(TokenType.FLOAT))
            {
                String newFloat = "new Float(" + vN.getValue().getText() +")";
                codeForExpr.append(nameOfVar).append(" = ").append(newFloat).append(";\n");
            }
            else if(vN.getValue().getType().equals(TokenType.INTEGER))
            {
                String newInteger = "new Integer(" + vN.getValue().getText() +")";
                codeForExpr.append(nameOfVar).append(" = ").append(newInteger).append(";\n");
            }
        }
        else if(node instanceof VariableNode)
        {
            VariableNode vN = (VariableNode)node;
            String nameOfVar = "ARGUM_"+argNum;
            method.addLocalVariable(nameOfVar, pool.get("java.lang.Object"));
            codeForExpr.append(nameOfVar).append(" = ").append(vN.getVariable().getText().substring(1)).append(";\n");
        }
        else if(node instanceof MathFunctionNode)
        {
            MathFunctionNode mFN = (MathFunctionNode)node;
            if(mFN.getMathFun().getType().equals(TokenType.SQRT))
            {
                String nameOfVar = "ARGUM_"+argNum;
                String code = MakeArguments(mFN.getArguments(), method);
                codeForExpr.append(code);
                String str = nameOfVar + " = sqrt("+ "ARG_0" +");\n";
                method.addLocalVariable(nameOfVar, pool.get("java.lang.Object"));
                codeForExpr.append(str);
            }
            else if(mFN.getMathFun().getType().equals(TokenType.POW))
            {
                String nameOfVar = "ARGUM_"+argNum;
                String code = MakeArguments(mFN.getArguments(), method);
                codeForExpr.append(code);
                String str = nameOfVar + " = pow("+ "ARG_0" + "," + "ARG_1" +");\n";
                method.addLocalVariable(nameOfVar, pool.get("java.lang.Object"));
                codeForExpr.append(str);
            }
            else if(mFN.getMathFun().getType().equals(TokenType.RAND))
            {
                String nameOfVar = "ARGUM_"+argNum;
                String str = nameOfVar + " = rand();\n";
                method.addLocalVariable(nameOfVar, pool.get("java.lang.Object"));
                codeForExpr.append(str);
            }
        }
    }

    private BeforeAndToString SolveQuatationNode(QuotationNodes qN, String nameOfVar, CtMethod method) throws Exception
    {
        BeforeAndToString result = new BeforeAndToString();
        StringBuilder codeBefore = new StringBuilder();

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

                VarAndCode bla = SolveSquareBraces(sBN, nameOfVar, method);
                codeBefore.append(bla._allCode);

                sB.append("TEMP_VAR.toString()");
            }
            isFirstIteration = false;
        }
        result.codeBefore = codeBefore.toString();
        result.textToString = sB.toString();
        return result;
    }

    // возвращает то, что нужно вывести
    // result.codeBefore - код для объявления лок переменных
    // result.textToString - name of var
    // WARNING!!! result._allCode can be NULL
    private VarAndCode SolvePUTS(UnarOperationNode uON, CtMethod method) throws Exception
    {
        VarAndCode result = new VarAndCode();

        if(uON.getOperand() instanceof ValueNode) // например, string
        {
            ValueNode vN = (ValueNode)uON.getOperand();
            String sB = "\"" + vN.getValue().getText() + "\"";

            method.addLocalVariable("TEMP_VAR", pool.get("java.lang.Object"));
            result._allCode = "TEMP_VAR = " + sB + ";\n";
            result._nameOfVar = "TEMP_VAR";
            return result;
        }
        else if(uON.getOperand() instanceof QuotationNodes) // кавычки ""
        {
            QuotationNodes qN = (QuotationNodes)uON.getOperand();
            BeforeAndToString sB = SolveQuatationNode(qN, null, method);

            method.addLocalVariable("TEMP_VAR", pool.get("java.lang.Object"));
            result._allCode = sB.codeBefore + "TEMP_VAR = " + sB.textToString + ";\n";
            result._nameOfVar = "TEMP_VAR";
            return result;
        }
        else if(uON.getOperand() instanceof CurlyBracesNodes) // {bla bla}
        {
            CurlyBracesNodes cBN = (CurlyBracesNodes)uON.getOperand();
            StringBuilder sB = new StringBuilder();
            sB.append("\"");
            for(ExpressionNode eN : cBN.getNodes()) // все ноды в кавычках
            {
                if(eN instanceof StringNode)
                {
                    StringNode sN = (StringNode)eN;
                    sB.append(sN.getString().replace("\\", "\\\\").replace("\"", "\\\""));
                }
            }
            sB.append("\"");

            method.addLocalVariable("TEMP_VAR", pool.get("java.lang.Object"));
            result._allCode = "TEMP_VAR = " + sB + ";\n";
            result._nameOfVar = "TEMP_VAR";
            return result;
        }
        else if(uON.getOperand() instanceof VariableNode) // puts $X
        {
            VariableNode vN = (VariableNode)uON.getOperand();
            if(vN.getVariable().getType().equals(TokenType.LINK_VARIABLE))
            {
                String sB = vN.getVariable().getText().substring(1);

                result._allCode = null;
                result._nameOfVar = sB;
                return result;
            }
        }
        else if(uON.getOperand() instanceof SquareBracesNodes)
        {
            SquareBracesNodes sBN = (SquareBracesNodes)uON.getOperand();
            VarAndCode bla = SolveSquareBraces(sBN, null, method);

            method.addLocalVariable("TEMP_VAR", pool.get("java.lang.Object"));
            result._allCode = bla._allCode;
            result._nameOfVar = bla._nameOfVar;

            return result;
        }
        return null;
    }

    private void AddLocalVarIfNeeded(String nameOfVar, CtMethod method) throws Exception
    {
        for(String str : _varNames)
        {
            if(str.equals(nameOfVar))
            {
                return;
            }
        }
        _varNames.add(nameOfVar);
        method.addLocalVariable(nameOfVar, pool.get("java.lang.Object"));
    }
}
