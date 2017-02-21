package tools;

/**
 * Created by Ryan on 2/5/2017.
 */
public class Hex {
    public static String toString(byte b){
        return String.format("%02X", b);
    }

    public static String toString(short b){
        return String.format("%04X", (short)b);
    }

    public static byte makeByte(String s){
        return (byte)Integer.parseInt(s, 16);
    }

    public static short makeShort(String s){
        return (short)Integer.parseInt(s, 16);
    }

    public static int makeInt(String s){
        return (int)Integer.parseInt(s, 16);
    }
}
