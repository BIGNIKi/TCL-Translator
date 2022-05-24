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
}

