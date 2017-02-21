import emulator.CPU;
import emulator.GPU;
import emulator.ROM;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main method
 */
public class Main {
    private final static Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String [ ] args){
        if(args.length < 2){
            System.out.println("You must provide a ROM for the emulator to run");
            return;
        }

        String path = Arrays.stream(args).reduce("", (acc, b) -> acc + " " + b).trim();

        System.out.println("Opening file: " + path);
        try {
            ROM ROM = new ROM(Files.readAllBytes(new File(path).toPath()));
            GPU gpu = new GPU(ROM);
            CPU cpu = new CPU(ROM, gpu, "00150");
            //cpu.debug = true;
            //cpu.debugStep = true;
            cpu.run();
        }catch (IOException e){
            System.out.println("No ROM found at " + path);
        }
    }
}
