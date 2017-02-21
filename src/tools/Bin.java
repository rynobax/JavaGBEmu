package tools;

/**
 * Created by Ryan on 2/6/2017.
 */
public class Bin {
    public static byte makeByte(String s){
        return (byte)Integer.parseInt(s, 2);
    }

    public static short makeShort(String s){
        return (short)Integer.parseInt(s, 2);
    }
}
