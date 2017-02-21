package emulator.exceptions;

import tools.Hex;

/**
 * Created by Ryan on 2/6/2017.
 */
public class UnknownOpcodeException extends Exception {
    private byte opcode;
    private short pcVal;

    public UnknownOpcodeException(byte opcode, short pcVal){
        this.opcode = opcode;
        this.pcVal = pcVal;
    }

    public String toString(){
        return "Unknown Opcode " + Hex.toString(opcode) + " at " + Hex.toString(pcVal);
    }
}
