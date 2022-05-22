package translator;

import javassist.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class TestFile
{
    public static void Go() throws NotFoundException, CannotCompileException, IOException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException
    {
        final ClassPool pool = ClassPool.getDefault();
        CtClass cc = pool.get("translator.helpers.Source");
        cc.setName("TCLSource");

        CtMethod method1 = CtNewMethod.make("public static void main(String[] args){}", cc);
        method1.insertAfter("System.out.println(\"aboba\");");

        cc.addMethod(method1);

        cc.writeFile();

        Class<?> clazz = cc.toClass();
        Object source = clazz.getDeclaredConstructor().newInstance();
        clazz.getDeclaredMethod("main", String[].class).invoke(source, (Object)new String[0]);
    }
}
