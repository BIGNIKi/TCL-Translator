package translator.helpers;

public class BaseSource
{
    private Float add(Float a, Float b){
        return a + b;
    }

    private Integer add(Integer a, Integer b){
        return a + b;
    }

    private Float add(Integer a, Float b){
        return a + b;
    }

    private Float add(Float a, Integer b){
        return a + b;
    }

    protected Object add(Object a, Object b) throws ClassCastException {
        try	{
            return add((Integer)a, (Integer)b);
        }
        catch (Exception ignored){}
        try	{
            return add((Float)a, (Integer) b);
        }
        catch (Exception ignored){}
        try	{
            return add((Integer)a, (Float)b);
        }
        catch (Exception ignored){}
        try	{
            return add((Float)a, (Float)b);
        }
        catch (Exception ignored){}
        throw new ClassCastException();
    }

    private Float sub(Float a, Float b){
        return a - b;
    }

    private Float sub(Integer a, Float b){
        return a - b;
    }

    private Float sub(Float a, Integer b){
        return a - b;
    }

    private Integer sub(Integer a, Integer b){
        return a - b;
    }

    protected Object sub(Object a, Object b) throws ClassCastException {
        try	{
            return sub((Integer)a, (Integer)b);
        }
        catch (Exception ignored){}
        try	{
            return sub((Float)a, (Integer)b);
        }
        catch (Exception ignored){}
        try	{
            return sub((Integer)a, (Float)b);
        }
        catch (Exception ignored){}
        try	{
            return sub((Float)a, (Float)b);
        }
        catch (Exception ignored){}
        throw new ClassCastException();
    }


    private Float sqrt(Float a){
        return (float)Math.sqrt(a);
    }

    private Float sqrt(Integer a){
        return (float)Math.sqrt(a);
    }

    protected Object sqrt(Object a) throws ClassCastException
    {
        try	{
            return sqrt((Integer)a);
        }
        catch (Exception ignored){}
        try	{
            return sqrt((Float)a);
        }
        catch (Exception ignored){}
        throw new ClassCastException();
    }

    private Float pow(Integer a, Integer b){
        return (float)Math.pow(a.doubleValue(), b.doubleValue());
    }

    private Float pow(Integer a, Float b){
        return (float)Math.pow(a.doubleValue(), b.doubleValue());
    }

    private Float pow(Float a, Integer b){
        return (float)Math.pow(a.doubleValue(), b.doubleValue());
    }

    private Float pow(Float a, Float b){
        return (float)Math.pow(a.doubleValue(), b.doubleValue());
    }

    protected Object pow(Object a, Object b) throws ClassCastException
    {
        try	{
            return pow((Integer)a, (Integer)b);
        }
        catch (Exception ignored){}
        try	{
            return pow((Integer)a, (Float)b);
        }
        catch (Exception ignored){}
        try	{
            return pow((Float)a, (Integer)b);
        }
        catch (Exception ignored){}
        try	{
            return pow((Float)a, (Float)b);
        }
        catch (Exception ignored){}
        throw new ClassCastException();
    }

    protected Object rand()
    {
        try	{
            return new Float(Math.random());
        }
        catch (Exception ignored){}
        throw new ClassCastException();
    }

    private Float mul(Float a, Float b){
        return a * b;
    }

    private Float mul(Integer a, Float b){
        return a * b;
    }

    private Float mul(Float a, Integer b){
        return a * b;
    }

    private Integer mul(Integer a, Integer b){
        return a * b;
    }

    protected Object mul(Object a, Object b) throws ClassCastException {
        try	{
            return mul((Integer)a, (Integer)b);
        }
        catch (Exception ignored){}
        try	{
            return mul((Float)a, (Integer)b);
        }
        catch (Exception ignored){}
        try	{
            return mul((Integer)a, (Float)b);
        }
        catch (Exception ignored){}
        try	{
            return mul((Float)a, (Float)b);
        }
        catch (Exception ignored){}
        throw new ClassCastException();
    }

    protected Object div(Object a, Object b) throws ClassCastException {
        try	{
            Float a1 = ((Integer)a).floatValue();
            Float b1 = ((Integer)b).floatValue();
            return a1 / b1;
        }
        catch (Exception ignored){}
        try	{
            Float a1 = (Float)a;
            Float b1 = ((Integer)b).floatValue();
            return a1 / b1;
        }
        catch (Exception ignored){}
        try	{
            Float a1 = ((Integer)a).floatValue();
            Float b1 = (Float)b;
            return a1 / b1;
        }
        catch (Exception ignored){}
        try	{
            Float a1 = (Float)a;
            Float b1 = (Float)b;
            return a1 / b1;
        }
        catch (Exception ignored){}
        throw new ClassCastException();
    }

    protected Object reminder(Object a, Object b) throws ClassCastException {
        try	{
            Float a1 = ((Integer)a).floatValue();
            Float b1 = ((Integer)b).floatValue();
            return a1 % b1;
        }
        catch (Exception ignored){}
        try	{
            Float a1 = (Float)a;
            Float b1 = ((Integer)b).floatValue();
            return a1 % b1;
        }
        catch (Exception ignored){}
        try	{
            Float a1 = ((Integer)a).floatValue();
            Float b1 = (Float)b;
            return a1 % b1;
        }
        catch (Exception ignored){}
        try	{
            Float a1 = (Float)a;
            Float b1 = (Float)b;
            return a1 % b1;
        }
        catch (Exception ignored){}
        throw new ClassCastException();
    }

    protected boolean GREATER_OR_EQUAL(Object a, Object b)
    {
        try	{
            Integer a1 = (Integer) a;
            Integer b1 = (Integer) b;
            return a1 >= b1;
        }
        catch (Exception ignored){}
        try	{
            Integer a1 = (Integer) a;
            Float b1 = (Float) b;
            return a1 >= b1;
        }
        catch (Exception ignored){}
        try	{
            Float a1 = (Float) a;
            Integer b1 = (Integer) b;
            return a1 >= b1;
        }
        catch (Exception ignored){}
        try	{
            Float a1 = (Float) a;
            Float b1 = (Float) b;
            return a1 >= b1;
        }
        catch (Exception ignored){}
        throw new ClassCastException();
    }

    protected boolean LESS_OR_EQUAL(Object a, Object b)
    {
        try	{
            Integer a1 = (Integer) a;
            Integer b1 = (Integer) b;
            return a1 <= b1;
        }
        catch (Exception ignored){}
        try	{
            Integer a1 = (Integer) a;
            Float b1 = (Float) b;
            return a1 <= b1;
        }
        catch (Exception ignored){}
        try	{
            Float a1 = (Float) a;
            Integer b1 = (Integer) b;
            return a1 <= b1;
        }
        catch (Exception ignored){}
        try	{
            Float a1 = (Float) a;
            Float b1 = (Float) b;
            return a1 <= b1;
        }
        catch (Exception ignored){}
        throw new ClassCastException();
    }

    protected boolean GREATER(Object a, Object b)
    {
        try	{
            Integer a1 = (Integer) a;
            Integer b1 = (Integer) b;
            return a1 > b1;
        }
        catch (Exception ignored){}
        try	{
            Integer a1 = (Integer) a;
            Float b1 = (Float) b;
            return a1 > b1;
        }
        catch (Exception ignored){}
        try	{
            Float a1 = (Float) a;
            Integer b1 = (Integer) b;
            return a1 > b1;
        }
        catch (Exception ignored){}
        try	{
            Float a1 = (Float) a;
            Float b1 = (Float) b;
            return a1 > b1;
        }
        catch (Exception ignored){}
        throw new ClassCastException();
    }

    protected boolean LESS(Object a, Object b)
    {
        try	{
            Integer a1 = (Integer) a;
            Integer b1 = (Integer) b;
            return a1 < b1;
        }
        catch (Exception ignored){}
        try	{
            Integer a1 = (Integer) a;
            Float b1 = (Float) b;
            return a1 < b1;
        }
        catch (Exception ignored){}
        try	{
            Float a1 = (Float) a;
            Integer b1 = (Integer) b;
            return a1 < b1;
        }
        catch (Exception ignored){}
        try	{
            Float a1 = (Float) a;
            Float b1 = (Float) b;
            return a1 < b1;
        }
        catch (Exception ignored){}
        throw new ClassCastException();
    }

    protected boolean IS_EQUAL(Object a, Object b)
    {
        try	{
            Integer a1 = (Integer) a;
            Integer b1 = (Integer) b;
            return a1.equals(b1);
        }
        catch (Exception ignored){}
        try	{
            Integer a1 = (Integer) a;
            Float b1 = (Float) b;
            return b1.equals(a1.floatValue());
        }
        catch (Exception ignored){}
        try	{
            Float a1 = (Float) a;
            Integer b1 = (Integer) b;
            return a1.equals(b1.floatValue());
        }
        catch (Exception ignored){}
        try	{
            Float a1 = (Float) a;
            Float b1 = (Float) b;
            return a1.equals(b1);
        }
        catch (Exception ignored){}
        try	{
            String a1 = (String) a;
            String b1 = (String) b;
            return a1.equals(b1);
        }
        catch (Exception ignored){}
        throw new ClassCastException();
    }

    protected boolean IS_NOT_EQUAL(Object a, Object b)
    {
        try	{
            Integer a1 = (Integer) a;
            Integer b1 = (Integer) b;
            return !a1.equals(b1);
        }
        catch (Exception ignored){}
        try	{
            Integer a1 = (Integer) a;
            Float b1 = (Float) b;
            return !b1.equals(a1.floatValue());
        }
        catch (Exception ignored){}
        try	{
            Float a1 = (Float) a;
            Integer b1 = (Integer) b;
            return !a1.equals(b1.floatValue());
        }
        catch (Exception ignored){}
        try	{
            Float a1 = (Float) a;
            Float b1 = (Float) b;
            return !a1.equals(b1);
        }
        catch (Exception ignored){}
        try	{
            String a1 = (String) a;
            String b1 = (String) b;
            return !a1.equals(b1);
        }
        catch (Exception ignored){}
        throw new ClassCastException();
    }
}

