import emulator.CPU;
import emulator.GPU;
import emulator.ROM;
import emulator.dataTypes.*;
import org.junit.Assert;
import org.junit.Test;
import tools.Hex;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class RegisterTests {
    @Test
    public void BytesToShortTest(){
        byte b1 = (byte)Integer.parseInt("50", 16);
        byte b2 = (byte)Integer.parseInt("50", 16);
        short val = RegHelper.BytesToShort(b1, b2);
        Assert.assertEquals(Hex.toString(val).compareTo("0x5050"), 0);
    }

    @Test
    public void ShortToByesTest(){
        byte b1 = (byte)Integer.parseInt("C3", 16);
        byte b2 = (byte)Integer.parseInt("8b", 16);

        short val = (short)Integer.parseInt("C38b", 16);
        byte[] bytes = RegHelper.ShortToBytes(val);

        Assert.assertArrayEquals(new byte[]{b1, b2}, bytes);
    }

    @Test
    public void Reg88ConstructTest(){
        byte b1 = (byte)Integer.parseInt("C3", 16);
        byte b2 = (byte)Integer.parseInt("8b", 16);
        Reg8 r8_1 = new Reg8(b1);
        Reg8 r8_2 = new Reg8(b2);

        Reg88 r = new Reg88(r8_1, r8_2);
        Assert.assertEquals(Hex.toString(r.getVal()).compareTo("0xC38B"), 0);
    }

    @Test
    public void Reg88SetTest(){
        byte b1 = (byte)Integer.parseInt("C3", 16);
        byte b2 = (byte)Integer.parseInt("8b", 16);
        Reg8 r8_1 = new Reg8(b1);
        Reg8 r8_2 = new Reg8(b2);

        Reg88 r = new Reg88(r8_1, r8_2);

        short s = (short)Integer.parseInt("A1B2", 16);
        r.setVal(s);
        Assert.assertEquals(Hex.toString(r.getVal()).compareTo("0xA1B2"), 0);
    }

    @Test
    public void RegFlagTest(){
        byte b = (byte)Integer.parseInt("C3", 16);
        Reg8 r = new Reg8(b);
        RegFlag rf = new RegFlag(r);
        rf.setZ(false);
        rf.setC(false);
        rf.setH(false);
        rf.setN(false);
        Assert.assertEquals("C false", false, rf.getC());
        Assert.assertEquals("Z false", false, rf.getZ());
        Assert.assertEquals("H false", false, rf.getH());
        Assert.assertEquals("N false", false, rf.getN());
        rf.setZ(true);
        rf.setC(true);
        rf.setH(true);
        rf.setN(true);
        Assert.assertEquals("C false", true, rf.getC());
        Assert.assertEquals("Z false", true, rf.getZ());
        Assert.assertEquals("H false", true, rf.getH());
        Assert.assertEquals("N false", true, rf.getN());
    }

    @Test
    public void AddFlagTest(){
        String path = "C:\\Users\\Ryan\\Documents\\Code\\gbemu\\test\\test.gb";
        try {
            ROM rom = new ROM(Files.readAllBytes(
                    new File(path).toPath()));
            GPU gpu = new GPU(rom);
            CPU cpu = new CPU(rom, gpu, "0000");
            cpu.debug = true;
            cpu.run();
        }catch (IOException e){
            System.out.println("No ROM found at " + path);
        }
    }
}
