package translator;

import ast.ExpressionNode;
import ast.StatementsNode;
import javassist.*;

import java.io.IOException;

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
            //lMain = CtNewMethod.make("public void evaluate() throws Exception {\n}", cc); // создает метод в класс cc

            //cc.addMethod(lMain);


            //CtMethod method1 = CtNewMethod.make("public static void main(String[] args){}", cc);
            //method1.addLocalVariable("source", pool.get("TCLSource")); // задефайнили переменную типа LispSource
            //method1.insertAfter("source = new TCLSource(); "); // создали экземпляр объекта
            ////method1.insertAfter("source.initGlobals();"); // определение глобальных переменных
            //method1.insertAfter("source.evaluate(); "); // вызвали метод у объекта

            //cc.addMethod(method1);

            writeToFile();

            Class<?> clazz = blabla();

            //return cc.toClass();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        //return null;
    }

    public Class<?> blabla()
    {
        try
        {
            return cc.toClass();
        } catch(CannotCompileException e)
        {
            e.printStackTrace();
        }
        return null;
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
}
