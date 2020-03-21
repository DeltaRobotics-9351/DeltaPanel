package com.deltarobotics9351.deltapanel;

import com.deltarobotics9351.deltapanel.blocks.BlocksManager;
import com.deltarobotics9351.deltapanel.gamepad.PanelGamepad;
import com.qualcomm.robotcore.eventloop.opmode.OpModeManager;
import com.qualcomm.robotcore.eventloop.opmode.OpModeRegistrar;
import org.firstinspires.ftc.robotcore.internal.opmode.OpModeMeta;
import org.firstinspires.ftc.robotcore.internal.opmode.RegisteredOpModes;

import static com.deltarobotics9351.deltapanel.DeltaLogger.*;

import java.io.IOException;
import java.util.HashMap;

public class DeltaPanel {

    public static DeltaPanelHttpService httpService;
    public static DeltaPanelWebSocketService webSocketService;
    public static Thread serviceThread;

    public static HashMap<String, OpModeMeta.Flavor> opModeList = new HashMap();

    public static boolean performUnsafeSdkOperations = false;

    public static boolean initialized = false;

    public static final PanelGamepad panelGamepad1 = new PanelGamepad();

    public static final PanelGamepad panelGamepad2 = new PanelGamepad();

    public static void main(String[] args){ //for running this on a computer OS
        initialize(null);
    }

    private static boolean checkForSdk(){ //check if we'll be able to perfom "unsafe" sdk operations, by unsafe I mean operations that can't be made outside an Android context
        try {
            RegisteredOpModes.getInstance(); //this operation for some reason can't be done without an android activity, so we'll use this.
        }catch(NoClassDefFoundError e){
            return false;
        }
        return true;
    }

    @OpModeRegistrar
    public static void initialize(OpModeManager manager){
        performUnsafeSdkOperations = checkForSdk(); //check if sdk is present (for testing purposes on a computer OS)

        logInfo(performUnsafeSdkOperations ? "SDK is present" : "SDK is not present");

        if(initialized) return;

        initialized = true;

        serviceThread = new Thread(new ServiceRunnable());
        serviceThread.start();

        BlocksManager.update();
    }

    static class ServiceRunnable implements Runnable {

        @Override
        public void run() {
            try {
                httpService = new DeltaPanelHttpService();
            } catch (IOException e) {
                e.printStackTrace();
                logSevere("Unable to start DeltaPanelHttpService");
            }

            try {
                webSocketService = new DeltaPanelWebSocketService();
            } catch (IOException e) {
                e.printStackTrace();
                logSevere("Unable to start DeltaPanelWebSocketService");
            }

            if(!performUnsafeSdkOperations) return; //if we're not able to perform "unsafe" sdk operations, end this thread here

             RegisteredOpModes.getInstance().waitOpModesRegistered();
             synchronized (opModeList) {
                 for (OpModeMeta opModeMeta : RegisteredOpModes.getInstance().getOpModes()) {
                     opModeList.put(opModeMeta.name, opModeMeta.flavor);
                 }
             }

        }
    }

}
