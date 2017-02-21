package emulator;

import emulator.dataTypes.Reg16;
import emulator.dataTypes.RegHelper;
import tools.Hex;

import java.util.Arrays;

/**
 * Created by Ryan on 2/6/2017.
 */
public class ROM {
    private byte[] data = new byte[65536];

    public ROM(byte[] romData){
        for (int i = 0; i < romData.length; i++) {
            data[i] = romData[i];
        }

        data[Hex.makeInt("FF05")] = Hex.makeByte("00");
        data[Hex.makeInt("FF06")] = Hex.makeByte("00");
        data[Hex.makeInt("FF07")] = Hex.makeByte("00");

        data[Hex.makeInt("FF10")] = Hex.makeByte("80");
        data[Hex.makeInt("FF11")] = Hex.makeByte("BF");
        data[Hex.makeInt("FF12")] = Hex.makeByte("F3");

        data[Hex.makeInt("FF14")] = Hex.makeByte("BF");

        data[Hex.makeInt("FF16")] = Hex.makeByte("3F");
        data[Hex.makeInt("FF17")] = Hex.makeByte("00");

        data[Hex.makeInt("FF19")] = Hex.makeByte("BF");
        data[Hex.makeInt("FF1A")] = Hex.makeByte("7F");
        data[Hex.makeInt("FF1B")] = Hex.makeByte("FF");
        data[Hex.makeInt("FF1C")] = Hex.makeByte("9F");

        data[Hex.makeInt("FF1E")] = Hex.makeByte("BF");
        data[Hex.makeInt("FF20")] = Hex.makeByte("FF");
        data[Hex.makeInt("FF21")] = Hex.makeByte("00");
        data[Hex.makeInt("FF22")] = Hex.makeByte("00");
        data[Hex.makeInt("FF23")] = Hex.makeByte("BF");
        data[Hex.makeInt("FF24")] = Hex.makeByte("77");
        data[Hex.makeInt("FF25")] = Hex.makeByte("F3");
        data[Hex.makeInt("FF26")] = Hex.makeByte("F1");

        data[Hex.makeInt("FF40")] = Hex.makeByte("91");

        //data[Hex.makeInt("FF41")] = Hex.makeByte("80"); // THIS IS BECAUSE THE EMULATOR DOES IT

        data[Hex.makeInt("FF42")] = Hex.makeByte("00");
        data[Hex.makeInt("FF43")] = Hex.makeByte("00");

        data[Hex.makeInt("FF45")] = Hex.makeByte("00");

        data[Hex.makeInt("FF47")] = Hex.makeByte("FC");
        data[Hex.makeInt("FF48")] = Hex.makeByte("FF");
        data[Hex.makeInt("FF49")] = Hex.makeByte("FF");
        data[Hex.makeInt("FF4A")] = Hex.makeByte("00");
        data[Hex.makeInt("FF4B")] = Hex.makeByte("00");

        data[Hex.makeInt("FFFF")] = Hex.makeByte("00");

    }

    private int getIndex(short l){
        int loc = l;
        if(loc < 0) loc = 65536 + loc;
        return loc;
    }

    public byte getByte(Reg16 PC){
        short l = PC.getVal();
        int loc = getIndex(l);
        byte res = data[loc];
        PC.inc();
        return res;
    }

    public byte getByte(short l){
        int loc = getIndex(l);
        byte res = data[loc];
        return res;
    }

    public byte getByte(String s){
        int loc = getIndex(Hex.makeShort(s));
        byte res = data[loc];
        return res;
    }

    public short getShort(Reg16 PC){
        byte b1 = getByte(PC);
        byte b2 = getByte(PC);
        return RegHelper.BytesToShort(b2, b1);
    }

    public short getShort(short l){
        int loc = getIndex(l);
        byte b1 = data[loc];
        byte b2 = data[loc+1];
        return RegHelper.BytesToShort(b2, b1);
    }

    public short getShort(String s){
        int loc = getIndex(Hex.makeShort(s));
        byte b1 = data[loc];
        byte b2 = data[loc+1];
        return RegHelper.BytesToShort(b2, b1);
    }

    public byte[] getBytes(short l, Integer n){
        int loc = getIndex(l);
        return Arrays.copyOfRange(data, loc, loc + n);
    }

    public void setByte(short l, byte b){
        int loc = getIndex(l);
        data[loc] = b;
    }

    public void setShort(short l, short s){
        int loc = getIndex(l);
        byte[] b = RegHelper.ShortToBytes(s);
        data[loc] = b[1];
        data[loc+1] = b[0];
    }
}
