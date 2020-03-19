package com.deltarobotics9351.deltapanel.blocks;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class BlocksManager {

    public static final File SAVE_FOLDER = new File(System.getenv("APPDATA") + "\\FIRST\\delta");
    public static final File SAVE_BLOCKS_FOLDER = new File(SAVE_FOLDER + "\\blocks");

    private static final List<BlocksProgram> BLOCKS_PROGRAMS = new ArrayList<BlocksProgram>();

    public static void update() {
        SAVE_BLOCKS_FOLDER.mkdirs();
        BLOCKS_PROGRAMS.clear();
        BLOCKS_PROGRAMS.addAll(getBlocksProgramsList());
    }

    public static List<BlocksProgram> getBlocksPrograms(){
        update();
        return BLOCKS_PROGRAMS;
    }

    private static List<BlocksProgram> getBlocksProgramsList(){
        List<BlocksProgram> programsList = new ArrayList<BlocksProgram>();

        if(SAVE_BLOCKS_FOLDER.listFiles() != null){
            for(File f : SAVE_BLOCKS_FOLDER.listFiles()){
                if(f.listFiles() == null){
                    if(f.getName().endsWith(".com.deltarobotics9351.deltapanel.blocks")){
                        programsList.add(new BlocksProgram(f, "", ""));
                    }
                }
            }
        }

        return programsList;
    }

    private static String fileToString(File f)
    {
        String content = "";

        try
        {
            content = new String ( Files.readAllBytes( f.toPath() ) );
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return content;
    }

}
