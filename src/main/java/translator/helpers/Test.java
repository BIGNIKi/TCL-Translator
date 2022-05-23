package translator.helpers;

import translator.helpers.BaseSource;

public class Test extends BaseSource
{
    public Test() {
    }

    public void evaluate() throws Exception {
        Object X = new Integer(256);
        Object Y = new Integer(100);
        Object TEMP_VAR = this.add(X, Y);
        this.add(Y, X);
    }

    public static void main(String[] var0) throws Exception
    {
        Object var3 = null;
        Test source = new Test();
        Object var5 = null;
        source.evaluate();
    }
}
