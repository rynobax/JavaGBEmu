package emulator;

import emulator.gpu.Color;
import emulator.gpu.MODE;
import tools.Bin;
import tools.Hex;

import static emulator.gpu.MODE.*;

/**
 * Created by Ryan on 2/7/2017.
 */
public class GPU {
    private ROM ROM;
    private MODE mode = HBLANK;
    private int clock = 0;
    private int line = 0;

    public GPU(ROM rom){
        this.ROM = rom;
        setStat();
    }

    //private Color[][] frameBuffer = new Color[160][144];



    public void step(){
        clock++;
        setLY();
        setStat();
        //System.out.println("clock: " + clock);
        switch(mode)
        {
            // OAM read mode, scanline active
            case OAM:
                if(clock >= 80)
                {
                    // Enter scanline mode 3
                    clock = 0;
                    mode = VRAM;
                }
                break;

            // VRAM read mode, scanline active
            // Treat end of mode 3 as end of scanline
            case VRAM:
                if(clock >= 172)
                {
                    // Enter hblank
                    clock = 0;
                    mode = HBLANK;
                    setStat();

                    // Write a scanline to the framebuffer
                    renderScan();
                }
                break;

            // Hblank
            // After the last hblank, push the screen data to canvas
            case HBLANK:
                if(clock >= 204)
                {
                    clock = 0;
                    line++;

                    if(line == 143)
                    {
                        // Enter vblank
                        mode = VBLANK;
                        setStat();
                        //GPU._canvas.putImageData(GPU._scrn, 0, 0); // This updates the screen
                    }
                    else
                    {
                        mode = OAM;
                        setStat();
                    }
                }
                break;

            // Vblank (10 lines)
            case VBLANK:
                if(clock >= 456)
                {
                    clock = 0;
                    line++;

                    if(line > 153)
                    {
                        // Restart scanning modes
                        mode = OAM;
                        setStat();
                        line = 0;
                    }
                }
                break;
        }
    }

    private void setLY() {
        ROM.setByte(Hex.makeShort("FF44"), (byte)line);
    }

    private void setStat() {
        String modeFlag;
        String modeInt;
        switch (mode){
            case HBLANK:
                modeInt = "001";
                modeFlag = "00";
                break;
            case OAM:
                modeInt = "100";
                modeFlag = "10";
                break;
            case VBLANK:
                modeInt = "010";
                modeFlag = "01";
                break;
            case VRAM:
                modeInt = "000";
                modeFlag = "11";
                break;
            default:
                throw new Error("Invalid mode: " + mode);
        }
        byte LY = ROM.getByte(Hex.makeShort("FF44"));
        byte LYC = ROM.getByte(Hex.makeShort("FF45"));
        String LYLYCcmp = "0";
        if(LY == LYC) LYLYCcmp = "1";
        byte mask = Bin.makeByte("00"+LYLYCcmp+modeInt+LYLYCcmp+modeFlag);
        //System.out.println("mask: " + Hex.toString(mask));
        short loc = Hex.makeShort("FF41");
        byte val = ROM.getByte(loc);
        //System.out.println("val: " + Hex.toString(val));
        ROM.setByte(loc, (byte)(val | mask));
    }

    private void renderScan(){
        byte LCDC = ROM.getByte("FF40");
        boolean bit7 = (LCDC & Bin.makeByte("10000000")) != 0;
        boolean bit6 = (LCDC & Bin.makeByte("01000000")) != 0;
        boolean bit5 = (LCDC & Bin.makeByte("00100000")) != 0;
        boolean bit4 = (LCDC & Bin.makeByte("00010000")) != 0;
        boolean bit3 = (LCDC & Bin.makeByte("00001000")) != 0;
        boolean bit2 = (LCDC & Bin.makeByte("00000100")) != 0;
        boolean bit1 = (LCDC & Bin.makeByte("00000010")) != 0;
        boolean bit0 = (LCDC & Bin.makeByte("00000001")) != 0;

        short windowTileMapOffset;
        if(bit6) windowTileMapOffset = Hex.makeShort("9C00");
        else windowTileMapOffset = Hex.makeShort("9800");

        short tileDataTableOffset;
        if(bit4) tileDataTableOffset = Hex.makeShort("8000");
        else tileDataTableOffset = Hex.makeShort("8800");

        short bgTileMapOffset;
        if(bit3) bgTileMapOffset = Hex.makeShort("9C00");
        else bgTileMapOffset = Hex.makeShort("9800");

        boolean windowEnabled = bit5;

        short SCX = ROM.getByte("FF43");
        short SCY = ROM.getByte("FF42");
    }
}
