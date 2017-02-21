package emulator.dataTypes;

/**
 * Created by Ryan on 2/6/2017.
 */
public class RegFlag {
    private Reg8 r;

    public RegFlag(Reg8 r){
        this.r = r;
    }

    private final byte mask = (byte)Integer.parseInt("01", 16);

    public boolean getZ(){
        int z = (r.getVal() >> 7) & mask;
        if(z == 0){
            return false;
        }else if(z == 1){
            return true;
        }else{
            throw new Error("Z is not a bit: " + z);
        }
    }

    public boolean getN(){
        int n = (r.getVal() >> 6) & mask;
        if(n == 0){
            return false;
        }else if(n == 1){
            return true;
        }else{
            throw new Error("N is not a bit: " + n);
        }
    }

    public boolean getH(){
        int h = (r.getVal() >> 5) & mask;
        if(h == 0){
            return false;
        }else if(h == 1){
            return true;
        }else{
            throw new Error("Z is not a bit: " + h);
        }
    }

    public boolean getC(){
        int c = (r.getVal() >> 4) & mask;
        if(c == 0){
            return false;
        }else if(c == 1){
            return true;
        }else{
            throw new Error("Z is not a bit: " + c);
        }
    }

    byte zTrueMask = (byte)Integer.parseInt("10000000", 2);
    byte zFalseMask = (byte)Integer.parseInt("01111111", 2);
    public void setZ(boolean b){
        if(b){
            r.setVal((byte)(r.getVal() | zTrueMask));
        }else{
            r.setVal((byte)(r.getVal() & zFalseMask));
        }
    }

    byte nTrueMask = (byte)Integer.parseInt("01000000", 2);
    byte nFalseMask = (byte)Integer.parseInt("10111111", 2);
    public void setN(boolean b){
        if(b){
            r.setVal((byte)(r.getVal() | nTrueMask));
        }else{
            r.setVal((byte)(r.getVal() & nFalseMask));
        }
    }

    byte hTrueMask = (byte)Integer.parseInt("00100000", 2);
    byte hFalseMask = (byte)Integer.parseInt("11011111", 2);
    public void setH(boolean b){
        if(b){
            r.setVal((byte)(r.getVal() | hTrueMask));
        }else{
            r.setVal((byte)(r.getVal() & hFalseMask));
        }
    }

    byte cTrueMask = (byte)Integer.parseInt("00010000", 2);
    byte cFalseMask = (byte)Integer.parseInt("11101111", 2);
    public void setC(boolean b){
        if(b){
            r.setVal((byte)(r.getVal() | cTrueMask));
        }else{
            r.setVal((byte)(r.getVal() & cFalseMask));
        }
    }
}
