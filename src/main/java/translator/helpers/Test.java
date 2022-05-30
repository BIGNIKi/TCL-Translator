package translator.helpers;

import java.lang.reflect.Method;

public class Test extends BaseSource {
    public Test() {
    }

    private Object testo(Object a)
    {
        return "йоу";
    }

    public void evaluate() throws Exception {
        Class<Test> c = (Class<Test>) Class.forName("translator.helpers.Test");
        //TCLSource
        Class[] parameterTypes = new Class[1];
        parameterTypes[0] = Object.class;
        Method method = c.getDeclaredMethod("testo", parameterTypes);
        Object[] params = new Object[1];
        params[0] = new Object();
        Object a = method.invoke(this, params);
        System.out.println((String) a);
    }

    public static void main(String[] var0) throws Exception
    {
        Object var3 = null;
        Test source = new Test();
        Object var5 = null;
        source.evaluate();
    }
}
