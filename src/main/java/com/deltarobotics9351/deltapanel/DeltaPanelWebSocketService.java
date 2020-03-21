package com.deltarobotics9351.deltapanel;

import com.deltarobotics9351.deltapanel.blocks.BlocksManager;
import com.deltarobotics9351.deltapanel.blocks.BlocksProgram;
import fi.iki.elonen.NanoWSD;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;

import static com.deltarobotics9351.deltapanel.DeltaLogger.*;

public class DeltaPanelWebSocketService extends NanoWSD {

    int connections = 0;
    int currentClients = 0;
    static int clientConnectedInControlPanel = -1;
    public static final long msMaxGamepadPingBeforeReset = 1500;

    public DeltaPanelWebSocketService() throws IOException {
        super(51);
        start(8000, false);
        logInfo("Started the DeltaPanelWebSocketService");
    }

    @Override
    protected WebSocket openWebSocket(IHTTPSession handshake) {
        connections++; currentClients++;
        return new WsdSocket(handshake, connections);
    }

    private class WsdSocket extends WebSocket{

        int clientID = 0;
        long msLastGamepadAUpdate = 0;
        long msLastGamepadBUpdate = 0;

        Thread threadResetGamepadsHighPing = new Thread(new ResetGamepadsOnHighPing());

        public WsdSocket(IHTTPSession handshakeRequest, int id) {
            super(handshakeRequest); clientID = id;
        }

        public boolean isGamepadPingPonging = false;

        @Override
        protected void onOpen() {
            try {
                send("pong");
                logInfo("New WebSocket connection incoming (#" + clientID + ")");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onClose(WebSocketFrame.CloseCode code, String reason, boolean initiatedByRemote) {
            currentClients--;
            logInfo("WebSocket connection #" + clientID + "closed, " + code.toString() );
            if(isGamepadPingPonging){
                DeltaPanel.panelGamepad1.update("unlinked"); //"unlinked" command tells the gamepad to reset its values back to 0 & false
                DeltaPanel.panelGamepad2.update("unlinked"); //"unlinked" command tells the gamepad to reset its values back to 0 & false

                clientConnectedInControlPanel = -1;
            }

            if(threadResetGamepadsHighPing.isAlive()){ //interrupt anti gamepad stuck state thread
                threadResetGamepadsHighPing.interrupt();
            }
        }


        @Override
        protected void onMessage(WebSocketFrame message) {
            message.setUnmasked();

            if(message.getTextPayload().equalsIgnoreCase("ping")){
                try {
                    isGamepadPingPonging = false;

                    if(threadResetGamepadsHighPing.isAlive()){
                        threadResetGamepadsHighPing.interrupt();
                    }

                    send("pong");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(message.getTextPayload().equalsIgnoreCase("ping:gamepad")){
                if(clientConnectedInControlPanel == -1 || clientConnectedInControlPanel == clientID) {
                    try {
                        isGamepadPingPonging = true;
                        clientConnectedInControlPanel = clientID;

                        if(!threadResetGamepadsHighPing.isAlive()){ //start anti gamepad stuck state thread
                            threadResetGamepadsHighPing.start();
                        }

                        send("pong:gamepad");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    try {
                        send("close:alreadyclient"); //request to close connection if there´s already another client making a "gamepad ping pong"
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            if(message.getTextPayload().startsWith("status:")){

                String[] args = message.getTextPayload().split(":");

                String id = args[1];

                try {
                    send("status:"+id+":" + DeltaPanel.httpService.isAlive() + ";" + isAlive());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }else if(message.getTextPayload().equalsIgnoreCase("close")){

                try {
                    close(WebSocketFrame.CloseCode.NormalClosure, "Client requested close", true);
                    DeltaPanel.httpService.stop();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }else if(message.getTextPayload().startsWith("saveFileStr:")){

                String[] args = message.getTextPayload().split(":");

                String id = args[1];
                String dir = args[2];
                String content = args[3];

            }else if(message.getTextPayload().startsWith("loadFileStr:")) {

                String[] args = message.getTextPayload().split(":");

                String id = args[1];
                String dir = args[2];

                FileReader fileReader = null;

                try {
                    fileReader = new FileReader(new File(dir));
                } catch (FileNotFoundException e) {
                    try {
                        send("loadFileStr:error");
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }

                String str = fileReader.toString();
                try {
                    fileReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    send("loadFileStr:"+id+":"+str);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }else if(message.getTextPayload().startsWith("users:")){

                    String[] args = message.getTextPayload().split(":");

                    String id = args[1];

                    try {
                        send("users:"+id+":"+currentClients);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }else if(message.getTextPayload().startsWith("blocksProgramsList:")){
                    String[] args = message.getTextPayload().split(":");
                    String id = args[1];

                    String list = "";

                    int count = -1;

                    for(BlocksProgram bp : BlocksManager.getBlocksPrograms()){
                        String s = bp.BLOCKS_PROGRAM_FILE.getName().replace(";", ".s\\").replace(":", ".d\\");

                        Long millis = bp.BLOCKS_PROGRAM_FILE.lastModified();
                        String lastModified = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(millis);

                        count++;
                        if(count == 0){
                            list += s + ".a\\" + lastModified;
                        }else{
                            list += ";"+ s + ".a\\" + lastModified;
                        }
                    }

                    try {
                        send("blocksProgramsList:"+ id +":"+list.replace(":", ".d\\"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }else if(message.getTextPayload().startsWith("gamepadA:")){

                    if(!isGamepadPingPonging) return; //just to make sure

                    String data = message.getTextPayload().replace("gamepadA:", "");
                    DeltaPanel.panelGamepad1.update(data); //send data to gamepad

                    msLastGamepadAUpdate = System.currentTimeMillis();

                }else if(message.getTextPayload().startsWith("gamepadB:")){

                    if(!isGamepadPingPonging) return; //just to make sure

                    String data = message.getTextPayload().replace("gamepadB:", "");
                    DeltaPanel.panelGamepad2.update(data); //send data to gamepad

                    msLastGamepadBUpdate = System.currentTimeMillis();

                }else{
                    try {
                        send("unknown");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        }

        @Override
        protected void onPong(WebSocketFrame pong) {
            try {
                send("ping");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onException(IOException exception) {
            exception.printStackTrace();
        }

        //in order to avoid the PanelGamepads getting stuck in a state due to high ping, after a defined timeout (1800ms) the gamepads are reset to 0 & false state
        //this was made having in mind the fact that robots may get damaged or even persons can result injured due to an unresponsive state of the client gamepads
        private class ResetGamepadsOnHighPing implements Runnable{
            @Override
            public void run() {
                while(!Thread.interrupted()){

                    long gamepadAPing = System.currentTimeMillis() - msLastGamepadAUpdate; //calculate ping
                    long gamepadBPing = System.currentTimeMillis() - msLastGamepadBUpdate;

                    if(gamepadAPing >= msMaxGamepadPingBeforeReset){ //reset gamepad if max ping is reached (timeout)
                        DeltaPanel.panelGamepad1.update("unlinked");
                        msLastGamepadAUpdate = System.currentTimeMillis();

                        logWarning("Resetted Gamepad A due to timeout");
                    }

                    if(gamepadBPing >= msMaxGamepadPingBeforeReset){ //reset gamepad if max ping is reached (timeout)
                        DeltaPanel.panelGamepad2.update("unlinked");
                        msLastGamepadBUpdate = System.currentTimeMillis();

                        logWarning("Resetted Gamepad B due to timeout");
                    }

                }
            }
        }
    }

}
