
import translator.helpers.BaseSource;

public class Test extends BaseSource {
    public Test() {
    }

    private Object FOR(Object var1, Object var2, Object var3) throws Exception {
        Object TEMP_VAR = "Command FOR was changed on puts";
        System.out.println(TEMP_VAR.toString());
        TEMP_VAR = "Args: " + ((String)var1).toString() + "\n" + ((String)var2).toString() + "\n" + ((String)var3).toString() + "\n";
        System.out.println(TEMP_VAR.toString());
        return null;
    }

    public void evaluate() throws Exception {
        Object var6 = null;
        String ARG_0 = "set i 1";
        String ARG_1 = "$i < 10";
        String ARG_2 = "incr i";
        this.FOR(ARG_0, ARG_1, ARG_2);
    }

    public static void main(String[] var0) throws Exception
    {
        Object var3 = null;
        Test source = new Test();
        Object var5 = null;
        source.evaluate();
    }
}
