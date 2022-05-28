package translator.helpers;

public class Test extends BaseSource
{
    public Test() {
    }

    public void evaluate(Integer a)
    {

    }

    public static void main(String[] var0)
    {
        Test source = new Test();
        Integer a = 5;
        source.evaluate(a);
        System.out.println(a);
    }
}
