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
}

