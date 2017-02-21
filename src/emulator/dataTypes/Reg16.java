package emulator.dataTypes;

import tools.Hex;

/**
 * Created by Ryan on 2/5/2017.
 */
public class Reg16 implements LargeReg {
    private short val;

    public Reg16(short val){
        this.val = val;
    }

    public short getVal() {
        return val;
    }

    public void setVal(short val) {
        this.val = val;
    }

    public void inc(){
        this.val++;
    }

    public void dec(){
        this.val--;
    }

    public String toString(){
        return Hex.toString(this.val);
    }
}
