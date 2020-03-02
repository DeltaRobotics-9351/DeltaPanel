import blocks.BlocksManager;
import blocks.BlocksProgram;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoWSD;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;

public class DeltaPanelWebSocketService extends NanoWSD {

    int connections = 0;

    public DeltaPanelWebSocketService() throws IOException {
        super(51);
        start(8000, false);
        System.out.println("DeltaPanel: Started the DeltaPanelWebSocketService");
    }

    @Override
    protected WebSocket openWebSocket(IHTTPSession handshake) {
        return new WsdSocket(handshake);
    }

    private class WsdSocket extends WebSocket{

        public WsdSocket(IHTTPSession handshakeRequest) {
            super(handshakeRequest);
        }

        @Override
        protected void onOpen() {
            connections++;

            try {
                send("pong");
                System.out.println("DeltaPanel: New WebSocket connection incoming");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onClose(WebSocketFrame.CloseCode code, String reason, boolean initiatedByRemote) {
            connections--;
            System.out.println("DeltaPanel: WebSocket connection closed, " + code.toString() );
        }

        @Override
        protected void onMessage(WebSocketFrame message) {
            message.setUnmasked();
            //message.

            if(message.getTextPayload().equalsIgnoreCase("ping")){
                try {
                    send("pong");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


                if(message.getTextPayload().startsWith("status:")){

                String[] args = message.getTextPayload().split(":");

                String id = args[1];

                try {
                    send("status:"+id+":" + DeltaPanelService.httpService.isAlive() + ";" + isAlive());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }else if(message.getTextPayload().equalsIgnoreCase("close")){

                try {
                    close(WebSocketFrame.CloseCode.NormalClosure, "User requested close", true);
                    DeltaPanelService.httpService.stop();
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
                        send("users:"+id+":"+connections);
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
    }

}
