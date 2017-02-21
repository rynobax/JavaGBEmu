package emulator.dataTypes;

import static emulator.dataTypes.RegHelper.ShortToBytes;

/**
 * Created by Ryan on 2/5/2017.
 */
public class Reg88 implements LargeReg{
    private Reg8 r1;
    private Reg8 r2;

    public Reg88(Reg8 r1, Reg8 r2){
        this.r1 = r1;
        this.r2 = r2;
    }

    public void inc(){
        short val = getVal();
        setVal((short)(val+1));
    }

    public void dec(){
        short val = getVal();
        setVal((short)(val-1));
    }

    public void setVal(short val){
        byte[] bytes = ShortToBytes(val);
        r1.setVal(bytes[0]);
        r2.setVal(bytes[1]);
    }

    public short getVal(){
        return RegHelper.BytesToShort(r1.getVal(), r2.getVal());
    }
}
