package com.deltarobotics9351.deltapanel;

import java.util.List;

public class DeltaLogger {

    public enum LogLevel{ INFO, DEBUG, WARNING, ERROR, SEVERE }

    public static void log(LogLevel level, String message){
        switch(level){
            case INFO:
                System.out.println("INFO - DeltaPanel: " + message);
                break;
            case DEBUG:
                System.out.println("DEBUG - DeltaPanel: " + message);
                break;
            case WARNING:
                System.out.println("WARN - DeltaPanel: " + message);
                break;
            case ERROR:
                System.out.println("ERROR - DeltaPanel: " + message);
                break;
            case SEVERE:
                System.out.println("SEVERE - DeltaPanel: " + message);
                break;
            default:
                System.out.println("UNKNOWN - DeltaPanel: " + message);
                break;
        }
    }

    public static void logInfo(String message){ log(LogLevel.INFO, message); }

    public static void logDebug(String message){ log(LogLevel.DEBUG, message); }

    public static void logError(String message){ log(LogLevel.ERROR, message); }

    public static void logWarning(String message){ log(LogLevel.WARNING, message); }

    public static void logSevere(String message){ log(LogLevel.SEVERE, message); }

}
