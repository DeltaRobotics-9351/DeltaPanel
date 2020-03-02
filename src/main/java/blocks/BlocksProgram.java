package blocks;

import java.io.File;

public class BlocksProgram {

    public File BLOCKS_PROGRAM_FILE;

    public String PROGRAM_AS_XML = "";

    public String PROGRAM_AS_DELTASCRIPT = "";

    public BlocksProgram(File BLOCKS_PROGRAM_FILE, String PROGRAM_AS_XML, String PROGRAM_AS_DELTASCRIPT){
        this.BLOCKS_PROGRAM_FILE = BLOCKS_PROGRAM_FILE;
        this.PROGRAM_AS_XML = PROGRAM_AS_XML;
        this.PROGRAM_AS_DELTASCRIPT = PROGRAM_AS_DELTASCRIPT;
    }


}
