package emulator;

import java.util.concurrent.TimeUnit;

import emulator.dataTypes.*;
import emulator.exceptions.UnknownOpcodeException;
import tools.Bin;
import tools.Hex;

/**
 * CPU that runs emulation
 */
public class CPU {
    public boolean debug = false;
    public boolean debugStep = false;

    // 8 Bit Registers
    private Reg8 A = new Reg8(Hex.makeByte("01"));
    private Reg8 F = new Reg8(Hex.makeByte("B0"));
    private Reg8 B = new Reg8(Hex.makeByte("00"));
    private Reg8 C = new Reg8(Hex.makeByte("13"));
    private Reg8 D = new Reg8(Hex.makeByte("00"));
    private Reg8 E = new Reg8(Hex.makeByte("D8"));
    private Reg8 H = new Reg8(Hex.makeByte("01"));
    private Reg8 L = new Reg8(Hex.makeByte("4D"));

    // 16 Bit Registers
    private Reg16 SP = new Reg16(Hex.makeShort("FFFE"));
    private Reg16 PC = new Reg16(Hex.makeShort("0000"));

    // "16" Bit Register aliases
    private Reg88 BC = new Reg88(B, C);
    private Reg88 DE = new Reg88(D, E);
    private Reg88 HL = new Reg88(H, L);

    // Flags
    private RegFlag Flag = new RegFlag(F);
    private boolean IME = true;

    // Cartridge memory
    private ROM ROM;

    // GPU
    private GPU GPU;

    // Timing Info
    final private long NanoSecPerClockCycle = 238;

    // Constructor
    public CPU(ROM rom, GPU gpu, String PCStartStr){
        this.ROM = rom;
        short PCStart = (short)Integer.parseInt(PCStartStr, 16);
        this.PC.setVal(PCStart);
        this.GPU = gpu;
    }

    // Debug state
    private void printState(){
        System.out.println("*****BEGIN STATE*****");
        System.out.println("");
        System.out.println("REGISTERS:");
        System.out.println("af = " + Hex.toString(A.getVal()) + Hex.toString(F.getVal()));
        System.out.println("bc = " + Hex.toString(B.getVal()) + Hex.toString(C.getVal()));
        System.out.println("de = " + Hex.toString(D.getVal()) + Hex.toString(E.getVal()));
        System.out.println("hl = " + Hex.toString(H.getVal()) + Hex.toString(L.getVal()));
        System.out.println("sp = " + Hex.toString(SP.getVal()));
        System.out.println("pc = " + Hex.toString(PC.getVal()));
        System.out.println("");
        System.out.println("FLAGS:");
        System.out.println("Z: " + Flag.getZ());
        System.out.println("N: " + Flag.getN());
        System.out.println("H: " + Flag.getH());
        System.out.println("C: " + Flag.getC());
        System.out.println("");
        System.out.println("FF41: " + Hex.toString(ROM.getByte(Hex.makeShort("FF41"))));
        System.out.println("PC VAL: " + Hex.toString(PC.getVal()));
        System.out.println("Opcode: " + Hex.toString(ROM.getByte(PC.getVal())));
        System.out.print("Line: ");
        for (byte b : ROM.getBytes(PC.getVal(), 4)) {
            System.out.print(Hex.toString(b) + " ");
        }
        System.out.println("\n*****END STATE*****");
    }

    // Main loop
    public void run(){
        while(true) try {
            if(Hex.toString(PC.getVal()).equals("031F")){
            //if(Hex.toString(ROM.getByte(PC.getVal())).equals("3E")){
                debug = true;
                debugStep = true;
            }

            if(debug) printState();
            if(debugStep) try{
                System.in.read();
            }catch (Exception e){
                System.err.println(e);
            }

            long startTime = System.nanoTime();
            int cycles = cycle();
            for (int i = 0; i < cycles; i++) {
                GPU.step();
            }
            long endTime = System.nanoTime();

            // duration = time actually took, opTime = time should take
            long duration = endTime - startTime;
            long opTime = cycles * NanoSecPerClockCycle;

            if (duration < opTime) {
                // Didn't take long enough
                try {
                    long sleepTime = opTime - duration;
                    TimeUnit.NANOSECONDS.sleep(sleepTime);
                } catch (InterruptedException e) {
                    System.err.println(e.toString());
                    break;
                }
            }
        } catch (UnknownOpcodeException e) {
            System.err.println(e.toString());
            break;
        } catch (Exception e){
            System.err.println(e.toString());
            e.printStackTrace();
            break;
        }
    }

    private int cycle() throws UnknownOpcodeException{
        byte opcode = ROM.getByte(PC);
        if(Hex.toString(PC.getVal()).compareTo("0x2817") == 0){
            System.out.println("YOU MADE IT");
        }

        switch (Hex.toString(opcode)) {
            case "00":
                return NOP();
            case "01":
                return LD_nn(BC);
            case "02":
                return LD(BC, A);
            case "03":
                return INC_nn(BC);
            case "04":
                return INC_n(B);
            case "05":
                return DEC_n(B);
            case "06":
                return LD(B);
            case "09":
                return ADD_HL(BC);
            case "0A":
                return LD_A_nP(BC);
            case "0B":
                return DEC_nn(BC);
            case "0C":
                return INC_n(C);
            case "0D":
                return DEC_n(C);
            case "0E":
                return LD(C);
            case "11":
                return LD_nn(DE);
            case "12":
                return LD(DE, A);
            case "13":
                return INC_nn(DE);
            case "14":
                return INC_n(D);
            case "15":
                return DEC_n(D);
            case "16":
                return LD(D);
            case "18":
                return JR_n();
            case "19":
                return ADD_HL(DE);
            case "1A":
                return LD_A_nP(DE);
            case "1B":
                return DEC_nn(DE);
            case "1C":
                return INC_n(E);
            case "1D":
                return DEC_n(E);
            case "1E":
                return LD(E);
            case "20":
                return JR_NZ_n();
            case "21":
                return LD_nn(HL);
            case "22":
                return LDI_HL_A();
            case "23":
                return INC_nn(HL);
            case "24":
                return INC_n(H);
            case "25":
                return DEC_n(H);
            case "26":
                return LD(H);
            case "28":
                return JR_Z_n();
            case "29":
                return ADD_HL(HL);
            case "2A":
                return LDI_A_HL();
            case "2B":
                return DEC_nn(HL);
            case "2C":
                return INC_n(L);
            case "2D":
                return DEC_n(L);
            case "2F":
                return CPL();
            case "2E":
                return LD(L);
            case "30":
                return JR_NC_n();
            case "31":
                return LD_nn(SP);
            case "32":
                return LDD_HL_A();
            case "33":
                return INC_nn(SP);
            case "34":
                return INCP(HL);
            case "35":
                return DECP(HL);
            case "36":
                return LD(HL);
            case "38":
                return JR_C_n();
            case "39":
                return ADD_HL(SP);
            case "3A":
                return LDD_A_HL();
            case "3B":
                return DEC_nn(SP);
            case "3C":
                return INC_n(A);
            case "3D":
                return DEC_n(A);
            case "3E":
                return LD_A_n();
            case "40":
                return LD(B, B);
            case "41":
                return LD(B, C);
            case "42":
                return LD(B, D);
            case "43":
                return LD(B, E);
            case "44":
                return LD(B, H);
            case "45":
                return LD(B, L);
            case "46":
                return LD(B, HL);
            case "47":
                return LD(B, A);
            case "48":
                return LD(C, B);
            case "49":
                return LD(C, C);
            case "4A":
                return LD(C, D);
            case "4B":
                return LD(C, E);
            case "4C":
                return LD(C, H);
            case "4D":
                return LD(C, H);
            case "4E":
                return LD(C, HL);
            case "4F":
                return LD(C, A);
            case "50":
                return LD(D, B);
            case "51":
                return LD(D, C);
            case "52":
                return LD(D, D);
            case "53":
                return LD(D, E);
            case "54":
                return LD(D, H);
            case "55":
                return LD(D, L);
            case "56":
                return LD(D, HL);
            case "57":
                return LD(D, A);
            case "58":
                return LD(E, B);
            case "59":
                return LD(E, C);
            case "5A":
                return LD(E, D);
            case "5B":
                return LD(E, E);
            case "5C":
                return LD(E, H);
            case "5D":
                return LD(E, L);
            case "5E":
                return LD(E, HL);
            case "5F":
                return LD(E, A);
            case "60":
                return LD(H, B);
            case "61":
                return LD(H, C);
            case "62":
                return LD(H, D);
            case "63":
                return LD(H, E);
            case "64":
                return LD(H, H);
            case "65":
                return LD(H, L);
            case "66":
                return LD(H, HL);
            case "67":
                return LD(H, A);
            case "68":
                return LD(L, B);
            case "69":
                return LD(L, C);
            case "6A":
                return LD(L, D);
            case "6B":
                return LD(L, E);
            case "6C":
                return LD(L, H);
            case "6D":
                return LD(L, L);
            case "6E":
                return LD(L, HL);
            case "6F":
                return LD(L, A);
            case "70":
                return LD(HL, B);
            case "71":
                return LD(HL, C);
            case "72":
                return LD(HL, D);
            case "73":
                return LD(HL, E);
            case "74":
                return LD(HL, H);
            case "75":
                return LD(HL, L);
            case "77":
                return LD(HL, A);
            case "78":
                return LD_A_n(B);
            case "79":
                return LD_A_n(C);
            case "7A":
                return LD_A_n(D);
            case "7B":
                return LD_A_n(E);
            case "7C":
                return LD_A_n(H);
            case "7E":
                return LD_A_nP(HL);
            case "7F":
                return LD_A_n(A);
            case "80":
                return ADD_A(B);
            case "81":
                return ADD_A(C);
            case "82":
                return ADD_A(D);
            case "83":
                return ADD_A(E);
            case "84":
                return ADD_A(H);
            case "85":
                return ADD_A(L);
            case "87":
                return ADD_A(A);
            case "90":
                return SUB_n(B);
            case "91":
                return SUB_n(C);
            case "92":
                return SUB_n(D);
            case "93":
                return SUB_n(E);
            case "94":
                return SUB_n(H);
            case "95":
                return SUB_n(L);
            case "96":
                return SUB_nP(HL);
            case "97":
                return SUB_n(A);
            case "98":
                return SBC_A_n(B);
            case "99":
                return SBC_A_n(C);
            case "9A":
                return SBC_A_n(D);
            case "9B":
                return SBC_A_n(E);
            case "9C":
                return SBC_A_n(H);
            case "9D":
                return SBC_A_n(L);
            case "9E":
                return SBC_A_nP(HL);
            case "9F":
                return SBC_A_n(A);
            case "A8":
                return XOR_n(B);
            case "A9":
                return XOR_n(C);
            case "AA":
                return XOR_n(D);
            case "AB":
                return XOR_n(E);
            case "AC":
                return XOR_n(H);
            case "AD":
                return XOR_n(L);
            case "AE":
                return XOR_n(HL);
            case "AF":
                return XOR_n(A);
            case "B8":
                return CP_n(B);
            case "B9":
                return CP_n(C);
            case "BA":
                return CP_n(D);
            case "BB":
                return CP_n(E);
            case "BC":
                return CP_n(H);
            case "BD":
                return CP_n(L);
            case "BE":
                return CP_n(HL);
            case "BF":
                return CP_n(A);
            case  "C2":
                return JR_NZ();
            case  "C3":
                return JP_nn();
            case  "CA":
                return JR_Z();
            case  "CD":
                return CALL_nn();
            case  "D2":
                return JR_NC();
            case "D6":
                return SUB_n();
            case  "DA":
                return JR_C();
            case "E0":
                return LDH_n_A();
            case "E2":
                return LD_CP_A();
            case "E9":
                return JP_HL();
            case "EA":
                return LD_n_A();
            case "EE":
                return XOR_n();
            case "F0":
                return LDH_A_n();
            case "F2":
                return LD_A_CP();
            case "F3":
                return DI();
            case "FA":
                return LD_A_nn();
            case "FB":
                return EI();
            case "FE":
                return CP_n();
            default:
                throw new UnknownOpcodeException(opcode, PC.getVal());
        }
    }

    /*
        JUMP START
     */

    private int JP_nn() {
        short dest = ROM.getShort(PC);
        PC.setVal(dest);

        return 12;
    }

    private int JR_NZ(){
        if(!Flag.getZ()){
            short dest = ROM.getShort(PC);
            PC.setVal(dest);
        }

        return 12;
    }

    private int JR_Z(){
        if(Flag.getZ()){
            short dest = ROM.getShort(PC);
            PC.setVal(dest);
        }else{
            PC.inc();
        }

        return 12;
    }

    private int JR_NC(){
        if(!Flag.getC()){
            short dest = ROM.getShort(PC);
            PC.setVal(dest);
        }else{
            PC.inc();
        }

        return 12;
    }

    private int JR_C(){
        if(Flag.getC()){
            short dest = ROM.getShort(PC);
            PC.setVal(dest);
        }else{
            PC.inc();
        }

        return 12;
    }

    private int JP_HL(){
        PC.setVal(HL.getVal());

        return 4;
    }

    private int JR_n(){
        byte inc = ROM.getByte(PC.getVal());
        PC.setVal((short)(PC.getVal() + inc));

        return 8;
    }

    private int JR_NZ_n(){
        if(!Flag.getZ()) {
            byte inc = ROM.getByte(PC.getVal());
            PC.setVal((short) (PC.getVal() + inc));
        }else{
            PC.inc();
        }

        return 8;
    }

    private int JR_Z_n(){
        if(Flag.getZ()) {
            byte inc = ROM.getByte(PC.getVal());
            PC.setVal((short) (PC.getVal() + inc));
        }else{
            PC.inc();
        }

        return 8;
    }

    private int JR_NC_n(){
        if(!Flag.getC()) {
            byte inc = ROM.getByte(PC.getVal());
            PC.setVal((short) (PC.getVal() + inc));
        }else{
            PC.inc();
        }

        return 8;
    }

    private int JR_C_n(){
        if(Flag.getC()) {
            byte inc = ROM.getByte(PC.getVal());
            PC.setVal((short) (PC.getVal() + inc));
        }else{
            PC.inc();
        }

        return 8;
    }

    /*
        CALL START
     */

    private int CALL_nn(){
        short addr = PC.getVal();
        SP.dec();
        ROM.setShort(SP.getVal(), addr);
        PC.setVal(addr);

        return 12;
    }

    /*
        NOP START
     */

    private int NOP(){
        return 4;
    }

    /*
        LD START
     */

    private int LD(Reg8 dest, Reg8 src){
        dest.setVal(src.getVal());

        return 4;
    }

    private int LD(Reg8 dest, Reg88 src){
        dest.setVal(ROM.getByte(src.getVal()));

        return 8;
    }

    private int LD(Reg88 dest){
        ROM.setByte(dest.getVal(), ROM.getByte(PC));

        return 12;
    }

    private int LD(Reg8 dest){
        dest.setVal(ROM.getByte(PC));

        return 8;
    }

    private int LD(Reg88 dest, Reg8 src){
        ROM.setByte(dest.getVal(), src.getVal());

        return 8;
    }

    private int LD_n_A(){
        ROM.setByte(PC.getVal(), A.getVal());
        PC.inc();
        PC.inc();

        return 16;
    }

    private int LD_A_n(Reg8 r){
        A.setVal(r.getVal());

        return 4;
    }

    private int LD_A_n(){
        A.setVal(ROM.getByte(PC));

        return 8;
    }

    private int LD_A_nP(Reg88 r){
        A.setVal(ROM.getByte(r.getVal()));

        return 16;
    }

    private int LD_A_nn(){
        A.setVal(ROM.getByte(ROM.getShort(PC)));

        return 16;
    }

    private int LD_nn(LargeReg dest){
        dest.setVal(ROM.getShort(PC));

        return 12;
    }

    private int LDI_A_HL(){
        A.setVal(ROM.getByte(HL.getVal()));
        HL.inc();

        return 8;
    }

    private int LDD_A_HL(){
        A.setVal(ROM.getByte(HL.getVal()));
        HL.dec();

        return 8;
    }

    private int LDI_HL_A(){
        ROM.setShort(HL.getVal(), A.getVal());
        HL.inc();

        return 8;
    }

    private int LDD_HL_A(){
        ROM.setShort(HL.getVal(), A.getVal());
        HL.dec();

        return 8;
    }

    private int LDH_n_A(){
        ROM.setByte((short)(Hex.makeShort("FF00")+ROM.getByte(PC)), A.getVal());

        return 12;
    }

    private int LDH_A_n(){
        A.setVal(ROM.getByte((short)(Hex.makeShort("FF00")+ROM.getByte(PC))));

        return 12;
    }

    private int LD_CP_A(){
        ROM.setByte((short)(Hex.makeShort("FF00") + C.getVal()), A.getVal());
        return 8;
    }

    private int LD_A_CP(){
        A.setVal(ROM.getByte((short)(Hex.makeShort("FF00") + C.getVal())));
        return 8;
    }

    /*
        INC_n START
     */

    private int INC_n(Reg8 r){
        byte target = r.getVal();
        byte value = 1;
        r.inc();

        // Flags
        Flag.setZ(r.getVal() == 0);
        Flag.setN(false);
        Flag.setH(hCheckAdd(target, value, 3));

        return 4;
    }

    private int INCP(Reg88 r){
        short loc = r.getVal();
        byte target = ROM.getByte(loc);
        byte value = 1;
        ROM.setByte(loc, (byte)(target+1));

        // Flags
        Flag.setZ(r.getVal() == 0);
        Flag.setN(false);
        Flag.setH(hCheckAdd(target, value, 3));

        return 12;
    }

    private int INC_nn(LargeReg r){
        r.setVal((short)(r.getVal() + 1));

        return 8;
    }

    /*
        DEC_n START
     */

    private int DEC_n(Reg8 r){
        byte target = r.getVal();
        byte value = 1;
        r.dec();

        // Flags
        Flag.setZ(r.getVal() == 0);
        Flag.setN(true);
        Flag.setH(hcCheckSub(target, value, "0F"));

        return 4;
    }

    private int DECP(Reg88 r){
        short loc = r.getVal();
        byte target = ROM.getByte(loc);
        byte value = 1;
        ROM.setByte(loc, (byte)(target-1));

        // Flags
        Flag.setZ(r.getVal() == 0);
        Flag.setN(false);
        Flag.setH(hcCheckSub(target, value, "0F"));

        return 12;
    }

    private int DEC_nn(LargeReg r){
        r.dec();

        return 8;
    }
    /*
        FLAG CHECK START
     */
    private boolean hCheckAdd(short target, short value, Integer bit){
        String maskStr = "";
        for (Integer i = 0; i<16; i++){
            if(i == (bit+1)){
                maskStr += "1";
            }else{
                maskStr += "0";
            }
        }
        byte mask = Bin.makeByte(maskStr);
        Integer res = (target + value) & mask;
        if(res == 16){
            return true;
        }else if(res == 0){
            return false;
        }else{
            throw new Error("hcCheck error: " + res);
        }
    }

    private boolean hcCheckSub(short target, short value, String maskStr){
        byte mask = Hex.makeByte(maskStr);
        boolean res = ((target & mask) - (value & mask)) < 0;

        return res;
    }

    /*
        SUB START
     */

    private int SUB_n(Reg8 valueReg){
        byte target = A.getVal();
        byte value = valueReg.getVal();
        A.setVal((byte)(target - value));

        // Flags
        Flag.setZ(A.getVal() == 0);
        Flag.setN(true);
        Flag.setH(hcCheckSub(target, value, "0F"));

        return 4;
    }

    private int SUB_nP(Reg88 valueReg){
        byte target = A.getVal();
        byte value = ROM.getByte(valueReg.getVal());
        A.setVal((byte)(target - value));

        // Flags
        Flag.setZ(A.getVal() == 0);
        Flag.setN(true);
        Flag.setH(hcCheckSub(target, value, "0F"));

        return 8;
    }

    private int SUB_n(){
        byte target = A.getVal();
        byte value = ROM.getByte(PC);
        A.setVal((byte)(target - value));

        // Flags
        Flag.setZ(A.getVal() == 0);
        Flag.setN(true);
        Flag.setH(hcCheckSub(target, value, "0F"));

        return 8;
    }

    /*
        SBC START
     */

    private int SBC_A_n(Reg8 valueReg){
        Reg8 targetReg = A;
        byte carryFlag = 0;
        if(Flag.getC()) carryFlag = 1;
        byte res = (byte)(targetReg.getVal() - (valueReg.getVal() + carryFlag));

        // Flags
        Flag.setZ(res == 0);
        Flag.setN(true);
        Flag.setH(hcCheckSub(targetReg.getVal(), valueReg.getVal(), "0F"));
        Flag.setC(hcCheckSub(targetReg.getVal(), valueReg.getVal(), "0F"));

        return 4;
    }

    private int SBC_A_nP(Reg88 valueReg){
        Reg8 targetReg = A;
        byte carryFlag = 0;
        if(Flag.getC()) carryFlag = 1;
        byte value = ROM.getByte(valueReg.getVal());
        byte res = (byte)(targetReg.getVal() - (value + carryFlag));

        // Flags
        Flag.setZ(res == 0);
        Flag.setN(true);
        Flag.setH(hcCheckSub(targetReg.getVal(), value, "0F"));
        Flag.setC(hcCheckSub(targetReg.getVal(), value, "0F"));

        return 8;
    }

    /*
        CPL START
     */

    private final byte CPLMask = Hex.makeByte("FF");
    private int CPL(){
        A.setVal((byte)(A.getVal() ^ CPLMask));

        // Flags
        Flag.setN(true);
        Flag.setH(true);

        return 4;
    }

    /*
        ADD START
     */

    private int ADD_A(Reg8 valueReg){
        short target = A.getVal();
        short value = valueReg.getVal();
        A.setVal((byte)(target + value));

        // Flags
        Flag.setZ(A.getVal() == 0);
        Flag.setN(false);
        Flag.setH(hCheckAdd(target, value, 3));
        Flag.setH(A.getVal() > Hex.makeByte("FF"));

        return 4;
    }

    private int ADD_HL(LargeReg valueReg){
        short target = HL.getVal();
        short value = valueReg.getVal();
        HL.setVal((short)(target + value));

        // Flags
        Flag.setN(false);
        Flag.setH(hCheckAdd(target, value, 11));
        Flag.setH(A.getVal() > Hex.makeShort("FFFF"));

        return 8;
    }

    /*
        XOR START
     */
    private int XOR_n(){
        A.setVal((byte)(A.getVal() ^ ROM.getByte(PC)));

        // Flags
        Flag.setZ(A.getVal() == 0);
        Flag.setN(false);
        Flag.setH(false);
        Flag.setC(false);

        return 4;
    }

    private int XOR_n(Reg8 r){
        A.setVal((byte)(A.getVal() ^ r.getVal()));

        // Flags
        Flag.setZ(A.getVal() == 0);
        Flag.setN(false);
        Flag.setH(false);
        Flag.setC(false);

        return 4;
    }

    private int XOR_n(Reg88 r){
        A.setVal((byte)(A.getVal() ^ ROM.getByte(r.getVal())));

        // Flags
        Flag.setZ(A.getVal() == 0);
        Flag.setN(false);
        Flag.setH(false);
        Flag.setC(false);

        return 4;
    }

    /*
        INTERRUPTS START
     */

    private int DI(){
        IME = false;

        return 4;
    }

    private int EI(){
        IME = true;

        return 4;
    }

    /*
        CP START
     */

    private int CP_n(Reg8 valueReg){
        byte target = A.getVal();
        byte value = valueReg.getVal();

        // Flags
        Flag.setZ(target == value);
        Flag.setN(true);
        Flag.setH(!hcCheckSub(target, value, "0F"));
        Flag.setC(target < value);

        return 4;
    }

    private int CP_n(Reg88 valueRef){
        byte target = A.getVal();
        byte value = ROM.getByte(valueRef.getVal());

        // Flags
        Flag.setZ(target == value);
        Flag.setN(true);
        Flag.setH(!hcCheckSub(target, value, "0F"));
        Flag.setC(target < value);

        return 8;
    }

    private int CP_n(){
        byte target = A.getVal();
        byte value = ROM.getByte(PC);

        // Flags
        Flag.setZ(target == value);
        Flag.setN(true);
        Flag.setH(!hcCheckSub(target, value, "0F"));
        Flag.setC(target < value);

        return 8;
    }

    /*
        RST START
     */
    private int RST_n(){
        return 32;
    }
}
