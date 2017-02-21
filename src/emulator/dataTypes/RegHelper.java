package emulator.dataTypes;

import tools.Hex;

/**
 * Created by Ryan on 2/5/2017.
 */
public class RegHelper {
    private static final short byte1Mask = (short)Integer.parseInt("00FF", 16);
    private static final short byte2Mask = (short)Integer.parseInt("FF00", 16);

    public static short BytesToShort(byte val1, byte val2){
        short shortVal1 = (short)((short)val1 & byte1Mask);
        short shortVal2 = (short)((short)val2 & byte1Mask);
        short shiftedVal1 = (short)(shortVal1 << 8);
        short result = (short)(shiftedVal1 | shortVal2);
        return result;
    }

    public static byte[] ShortToBytes(short val){
        byte b1 = (byte)((val & byte2Mask) >> 8);
        byte b2 = (byte)(val & byte1Mask);
        return new byte[]{b1, b2};
    }
}
