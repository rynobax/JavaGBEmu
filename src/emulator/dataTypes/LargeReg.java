package emulator.dataTypes;

/**
 * Created by Ryan on 2/6/2017.
 */
public interface LargeReg {
    short getVal();
    void setVal(short val);
    void dec();
    void inc();
}
