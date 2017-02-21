package emulator.dataTypes;
import tools.Hex;

/**
 * Created by Ryan on 2/5/2017.
 */
public class Reg8 {
    private byte val;

    public Reg8(byte val){
        this.val = val;
    }

    public byte getVal() {
        return val;
    }

    public void setVal(byte val) {
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
