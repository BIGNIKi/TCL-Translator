package translator.helpers;

import translator.helpers.BaseSource;

public class Test extends BaseSource
{
    public Test() {
    }

    public void evaluate() throws Exception {
        Integer a = 5;
        Float b = a.floatValue();
    }

    public static void main(String[] var0) throws Exception
    {
        Object var3 = null;
        Test source = new Test();
        Object var5 = null;
        source.evaluate();
    }
}
